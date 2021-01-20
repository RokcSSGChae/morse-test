package com.morse.streaming.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateRoomRequestDto {
    private int roomIdx;
    private String title;
    private String presenterId;
}