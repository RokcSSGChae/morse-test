package com.morse.streaming.handler;

/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import java.io.IOException;

import com.morse.streaming.service.RoomService;

import lombok.RequiredArgsConstructor;
import org.kurento.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * Protocol handler for 1 to N video call communication.
 *
 * @author Boni Garcia (bgarcia@gsyc.es)
 * @since 5.0.0
 */
@Component
@RequiredArgsConstructor
public class SignalingHandler extends TextWebSocketHandler {
    private static final String RECORDER_FILE_PATH = "file:///tmp/HelloWorldRecorded.webm";
    private static final Logger log = LoggerFactory.getLogger(SignalingHandler.class);
    private static final Gson gson = new GsonBuilder().create();
    private final RoomService roomService;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonObject jsonMessage = gson.fromJson(message.getPayload(), JsonObject.class);
        log.debug("Incoming message from session '{}': {}", session.getId(), jsonMessage);
        switch (jsonMessage.get("id").getAsString()) {
            case "presenter":
                try {
                    roomService.presenterConnection(session, jsonMessage);
                } catch (Throwable t) {
                    handleErrorResponse(t, session, "presenterResponse");
                }
                break;
            case "viewer":
                try {
                    roomService.joinRoom(session, jsonMessage);
                } catch (Throwable t) {
                    handleErrorResponse(t, session, "viewerResponse");
                }
                break;
            case "onIceCandidate": {
                try{
                    roomService.iceCandidate(session, jsonMessage);
                }catch (Throwable t){
                    handleErrorResponse(t,session,"iceCandidate");
                }

                break;
            }
            case "stop":
                //stop(session);
                break;
            default:
                break;
        }
    }



    private void handleErrorResponse(Throwable throwable, WebSocketSession session, String responseId)
            throws IOException {
        //stop(session);
        log.error(throwable.getMessage(), throwable);
        JsonObject response = new JsonObject();
        response.addProperty("id", responseId);
        response.addProperty("response", "rejected");
        response.addProperty("message", throwable.getMessage());
        session.sendMessage(new TextMessage(response.toString()));
    }

    private void setListener(RecorderEndpoint recorder,WebSocketSession session){
        recorder.addRecordingListener(event -> {
            JsonObject response = new JsonObject();
            response.addProperty("id", "recording");
            try {
                synchronized (session) {
                    session.sendMessage(new TextMessage(response.toString()));
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });

        recorder.addStoppedListener(event -> {
            JsonObject response = new JsonObject();
            response.addProperty("id", "stopped");
            try {
                synchronized (session) {
                    session.sendMessage(new TextMessage(response.toString()));
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });

        recorder.addPausedListener(event -> {
            JsonObject response = new JsonObject();
            response.addProperty("id", "paused");
            try {
                synchronized (session) {
                    session.sendMessage(new TextMessage(response.toString()));
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

    }

}

