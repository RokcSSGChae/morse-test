package com.morse.streaming.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.morse.streaming.dto.CommonMessage;
import com.morse.streaming.dto.request.CreateRoomRequestDto;
import com.morse.streaming.enums.JoinRoomResponseEnum;
import com.morse.streaming.enums.PresentResponseEnum;
import com.morse.streaming.model.JoinedViewer;
import com.morse.streaming.model.OnAirRoom;
import com.morse.streaming.model.TestUser;
import com.morse.streaming.repository.OnAirRoomRepository;
import com.morse.streaming.util.ResponseMessage;
import com.morse.streaming.util.ReturnCode;
import lombok.RequiredArgsConstructor;
import org.kurento.client.*;
import org.kurento.jsonrpc.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final KurentoClient kurento;
    private final ResponseMessage responseMessage;
    private final OnAirRoomRepository onAirRoomRepository;
    private final ReturnCode returnCode;
    private final static Logger log = LoggerFactory.getLogger(RoomService.class);
    private final RedisTemplate<String,OnAirRoom> roomRedisTemplate;

    public void stopStreaming(int roomIdx) {
        //onAirRoomRepository.getOnAirRoom(roomIdx).getPipeline().release();
        //onAirRoomRepository.getOnAirRoom(roomIdx).getWebRtcEndpoint().release();
        onAirRoomRepository.deleteRoom(roomIdx);
    }

    public CommonMessage createRoom(CreateRoomRequestDto createRoomRequestDto) {
        try {
            onAirRoomRepository.createOnAirRoom(
                    OnAirRoom.builder().
                            roomIdx(createRoomRequestDto.getRoomIdx()).
                            title(createRoomRequestDto.getTitle()).
                            presenterId(createRoomRequestDto.getPresenterId()).
                            build());

            return CommonMessage
                    .builder()
                    .code(returnCode.SUCCESS)
                    .build();

        } catch (Exception e) {
            log.error("{}",e);
            return CommonMessage
                    .builder()
                    .code(returnCode.FAIL)
                    .build();
        }
    }

    public void presenterConnection(final WebSocketSession session, JsonObject jsonMessage)
            throws IOException {
        OnAirRoom onAirRoom = onAirRoomRepository.getOnAirRoom(1);

        if (onAirRoom == null) {
            sendMessage(session, PresentResponseEnum.NO_ROOM_RESPONSE, null);
        }

        if (onAirRoom.getPipeline() == null) {
            MediaPipeline pipeline = kurento.createMediaPipeline();
            onAirRoom.setPipeline(pipeline.toString());
            WebRtcEndpoint presenterWebRtc = new WebRtcEndpoint.Builder(pipeline).useDataChannels().build();
            onAirRoom.setWebRtcEndpoint(presenterWebRtc.toString());

            addIceCandidateFoundListener(session, presenterWebRtc);

            String sdpOffer = jsonMessage.getAsJsonPrimitive("sdpOffer").getAsString();
            String sdpAnswer = presenterWebRtc.processOffer(sdpOffer);

            onAirRoomRepository.updateOnAirInfo(onAirRoom.getRoomIdx(),onAirRoom);
            sendMessage(session, PresentResponseEnum.ACCEPT_PRESENT_RESPONSE, sdpAnswer);
            presenterWebRtc.gatherCandidates();

        } else {
            sendMessage(session, PresentResponseEnum.ALREADY_STARTED_RESPONSE, null);
        }
    }

    public void joinRoom(final WebSocketSession session, JsonObject jsonMessage)
            throws IOException {

        int roomIdx = jsonMessage.get("roomIdx").getAsInt();
        String nickname = jsonMessage.get("nickname").getAsString();
        int viewerIdx = jsonMessage.get("viewerIdx").getAsInt();

        OnAirRoom onAirRoom = onAirRoomRepository.getOnAirRoom(roomIdx);

        System.out.println("viewer : " + onAirRoom.getPipeline());

        if (onAirRoom == null) {
            sendMessage(session, JoinRoomResponseEnum.NO_ROOM_RESPONSE, null);
        } else if (onAirRoom.getPipeline() == null) {
            sendMessage(session, JoinRoomResponseEnum.NOT_STARTED_RESPONSE, null);
        } else {
            if (onAirRoom.getViewers().containsKey(viewerIdx)) {
                sendMessage(session, JoinRoomResponseEnum.ALREADY_JOINED_RESPONSE, null);
                return;
            }

            MediaPipeline roomPipeLine = findPipeline(onAirRoom.getPipeline());
            WebRtcEndpoint presenterEndpoint = findEndpoint(roomPipeLine,onAirRoom.getWebRtcEndpoint());

            WebRtcEndpoint nextWebRtc = new WebRtcEndpoint.
                    Builder(roomPipeLine).useDataChannels().build();

            nextWebRtc.setTurnUrl("testuser:root@117.17.196.61:3478", new Continuation<Void>() {
                @Override
                public void onSuccess(Void result) throws Exception {
                    log.debug("Set Turn Success");
                }

                @Override
                public void onError(Throwable cause) throws Exception {

                }
            });

            addIceCandidateFoundListener(session, nextWebRtc);

            onAirRoom.getViewers().put(viewerIdx,
                    JoinedViewer.builder().
                            viewerIdx(viewerIdx).
                            nickname(nickname).
                            webRtcEndpoint(nextWebRtc.toString()).
                            build());

            onAirRoomRepository.updateOnAirInfo(roomIdx,onAirRoom);
            presenterEndpoint.connect(nextWebRtc);
            String sdpOffer = jsonMessage.getAsJsonPrimitive("sdpOffer").getAsString();
            String sdpAnswer = nextWebRtc.processOffer(sdpOffer);

            sendMessage(session, JoinRoomResponseEnum.ACCEPT_VIEW_RESPONSE, sdpAnswer);

            nextWebRtc.gatherCandidates();
        }

    }

    public void iceCandidate(WebSocketSession session, JsonObject jsonMessage) {
        WebRtcEndpoint webRtcEndpoint = null;
        JsonParser jsonParser = new JsonParser();
        Object object = jsonParser.parse(jsonMessage.get("candidate").getAsString());
        JsonObject candidate = (JsonObject) object;

        int roomIdx = jsonMessage.get("roomIdx").getAsInt();
        OnAirRoom onAirRoom = onAirRoomRepository.getOnAirRoom(roomIdx);
        MediaPipeline mediaPipeline = findPipeline(onAirRoom.getPipeline());
        if (mediaPipeline != null) {
            if (jsonMessage.get("isStreamer").getAsBoolean()) {
                webRtcEndpoint = findEndpoint(mediaPipeline,onAirRoom.getWebRtcEndpoint());
                if (webRtcEndpoint != null) {
                    IceCandidate cand =
                            new IceCandidate(candidate.get("candidate").getAsString(),
                                    candidate.get("sdpMid").getAsString(),
                                    candidate.get("sdpMLineIndex").getAsInt());
                    webRtcEndpoint.addIceCandidate(cand);
                }
            } else {
                int viewerIdx = jsonMessage.get("viewerIdx").getAsInt();
                JoinedViewer joinedViewer = onAirRoom.getViewers().get(viewerIdx);
                if (joinedViewer != null) {
                    webRtcEndpoint = findEndpoint(mediaPipeline,joinedViewer.getWebRtcEndpoint());
                    if (webRtcEndpoint != null) {
                        System.out.println(candidate.get("candidate").getAsString());
                        IceCandidate cand =
                                new IceCandidate(candidate.get("candidate").getAsString(),
                                        candidate.get("sdpMid").getAsString(),
                                        candidate.get("sdpMLineIndex").getAsInt());
                        webRtcEndpoint.addIceCandidate(cand);
                    }
                }
            }
        }
    }

    /* Find Media Pipeline By String*/
    public MediaPipeline findPipeline(String pipeline){
        ServerManager serverManager = kurento.getServerManager();
        List<MediaPipeline> mediaPipelineList = serverManager.getPipelines();
        for(MediaPipeline mediaPipeline : mediaPipelineList){
            if(pipeline.equals(mediaPipeline.toString())){
                return mediaPipeline;
            }
        }
        return null;
    }

    /* Find Media Endpoint By String*/
    public WebRtcEndpoint findEndpoint(MediaPipeline pipeline,String endpoint){
        List<MediaObject> endpointList=pipeline.getChildren();
        for(MediaObject endpointItem : endpointList){
            if(endpoint.equals(endpointItem.toString())){
                return (WebRtcEndpoint)endpointItem;
            }
        }
        return null;
    }


    /* WebSocket Response */
    public void sendMessage(WebSocketSession session, Enum status, String sdpAnswer) throws IOException {
        JsonObject response = new JsonObject();

        if (status.getDeclaringClass() == JoinRoomResponseEnum.class) {
            if (JoinRoomResponseEnum.ACCEPT_VIEW_RESPONSE.equals(status)) {
                response.addProperty("id", responseMessage.ID_VIEWER_CONNECTION);
                response.addProperty("response", responseMessage.ACCEPT);
                response.addProperty("sdpAnswer", sdpAnswer);
            } else if (JoinRoomResponseEnum.ALREADY_JOINED_RESPONSE.equals(status)) {
                response.addProperty("id", responseMessage.ID_VIEWER_CONNECTION);
                response.addProperty("response", responseMessage.REJECTED);
                response.addProperty("message", responseMessage.VIEWER_NOT_STARTED);
            } else if (JoinRoomResponseEnum.NO_ROOM_RESPONSE.equals(status)) {
                response.addProperty("id", responseMessage.ID_VIEWER_CONNECTION);
                response.addProperty("response", responseMessage.REJECTED);
                response.addProperty("message", responseMessage.VIEWER_NOT_STARTED);
            } else if (JoinRoomResponseEnum.NOT_STARTED_RESPONSE.equals(status)) {
                response.addProperty("id", responseMessage.ID_VIEWER_CONNECTION);
                response.addProperty("response", responseMessage.REJECTED);
                response.addProperty("message", responseMessage.VIEWER_NOT_STARTED);
            }
        } else {
            if (PresentResponseEnum.ACCEPT_PRESENT_RESPONSE.equals(status)) {
                response.addProperty("id", responseMessage.ID_PRESENTER_CONNECTION);
                response.addProperty("response", responseMessage.ACCEPT);
                response.addProperty("sdpAnswer", sdpAnswer);
            } else if (PresentResponseEnum.ALREADY_STARTED_RESPONSE.equals(status)) {
                response.addProperty("id", responseMessage.ID_PRESENTER_CONNECTION);
                response.addProperty("response", responseMessage.REJECTED);
                response.addProperty("message", responseMessage.PRESENT_ALREADY_STARTED);
            } else if (JoinRoomResponseEnum.NO_ROOM_RESPONSE.equals(status)) {
                response.addProperty("id", responseMessage.ID_PRESENTER_CONNECTION);
                response.addProperty("response", responseMessage.REJECTED);
                response.addProperty("message", responseMessage.PRESENT_NO_ROOM);
            }
        }

        session.sendMessage(new TextMessage(response.toString()));

    }

    public void addIceCandidateFoundListener(WebSocketSession session, WebRtcEndpoint endpoint) {
        endpoint.addIceCandidateFoundListener(event -> {
            JsonObject response = new JsonObject();
            response.addProperty("id", "iceCandidate");
            response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));
            try {
                synchronized (session) {
                    session.sendMessage(new TextMessage(response.toString()));
                }
            } catch (IOException e) {
                log.debug(e.getMessage());
            }
        });
    }
}
