package com.morse.streaming.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class CommonMessage {
    private String message;
    private int code;

    public CommonMessage() {}

    public CommonMessage( String message ) {
        this.message=message;
        this.code=200;
    }

    public CommonMessage(String message, int code) {
        this.message = message;
        this.code = code;
    }
}
