package com.morse.streaming.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IceCandidateResponseDto {
    private String candidate;
    private String id;
    private String message;

    @Builder
    IceCandidateResponseDto(String id, String candidate, String message) {
        this.id = id;
        this.candidate = candidate;
        this.message = message;
    }
}
