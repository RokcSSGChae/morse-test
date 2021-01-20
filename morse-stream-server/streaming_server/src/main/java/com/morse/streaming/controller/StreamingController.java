package com.morse.streaming.controller;

import com.morse.streaming.dto.CommonMessage;
import com.morse.streaming.dto.request.CreateRoomRequestDto;
import com.morse.streaming.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(value = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class StreamingController {

    private final RoomService createRoomService;
    @PostMapping("/create")
    public ResponseEntity<CommonMessage> createRoomController(
                                                    @RequestBody CreateRoomRequestDto createRoomRequestDto) {
        CommonMessage commonMessage = createRoomService.createRoom(createRoomRequestDto);

        if(commonMessage.getCode()==200)
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();//Advisor를 통해서 Exception으로 리팩토링 예정
    }



}
