package com.morse.streaming.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoomResponseDto {
    String id;
    String response;
    String message;
    String sdpAnswer;

    @Builder
    CreateRoomResponseDto(String id, String response, String message, String sdpAnswer) {
        this.id = id;
        this.response = response;
        this.message = message;
        this.sdpAnswer = sdpAnswer;
    }
}
