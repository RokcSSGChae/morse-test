package com.morse.streaming.model;

import com.google.gson.JsonObject;
import org.kurento.client.IceCandidate;
import org.kurento.client.MediaPipeline;
import org.kurento.client.WebRtcEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class TestUser {
    private static final Logger log = LoggerFactory.getLogger(TestUser.class);

    private WebRtcEndpoint webRtcEndpoint;
    private MediaPipeline pipeline;

    public WebRtcEndpoint getWebRtcEndpoint() {
        return webRtcEndpoint;
    }

    public void setWebRtcEndpoint(WebRtcEndpoint webRtcEndpoint) {
        this.webRtcEndpoint = webRtcEndpoint;
    }

    public void addCandidate(IceCandidate candidate) {
        webRtcEndpoint.addIceCandidate(candidate);
    }

    public void setPipeline(MediaPipeline pipeline){
        this.pipeline=pipeline;
    }

    public MediaPipeline getPipeline(){
        return this.pipeline;
    }
}
