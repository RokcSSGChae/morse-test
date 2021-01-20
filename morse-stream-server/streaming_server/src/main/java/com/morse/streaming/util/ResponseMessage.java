package com.morse.streaming.util;

import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ResponseMessage {
    /* Response Case Code */
    public final String ID_PRESENTER_CONNECTION = "presenterResponse";
    public final String ID_VIEWER_CONNECTION = "viewerResponse";
    public final String ID_ICECANDIDATE = "iceCandidate";

    /* Response Code */
    public final String ACCEPT = "accepted";
    public final String REJECTED = "reject";

    /* Response Viewing Error Message */
    public final String VIEWER_NOT_STARTED = "not started yet";
    public final String VIEWER_ALREADY_VIEWING = "you already watch this streaming";

    /* Response PRESENT Error Message */
    public final String PRESENT_NO_ROOM = "Room is not created";
    public final String PRESENT_ALREADY_STARTED = "you already start this present";

}
