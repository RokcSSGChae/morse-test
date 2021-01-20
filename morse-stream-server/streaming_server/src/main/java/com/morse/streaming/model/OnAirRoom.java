package com.morse.streaming.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kurento.client.MediaPipeline;
import org.kurento.client.RecorderEndpoint;
import org.kurento.client.WebRtcEndpoint;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
@Setter
@NoArgsConstructor
public class OnAirRoom {
    private int roomIdx;
    private String title;
    private String presenterId;
    private String pipeline;
    private String webRtcEndpoint;
    private HashMap<Integer, JoinedViewer> viewers;

    @Builder
    public OnAirRoom(int roomIdx, String title, String presenterId) {
        this.roomIdx = roomIdx;
        this.title = title;
        this.presenterId = presenterId;
        viewers = new HashMap<Integer, JoinedViewer>();
    }

}