package com.morse.streaming.repository;

import com.google.gson.Gson;
import com.morse.streaming.config.RedisConfig;
import com.morse.streaming.model.OnAirRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RequiredArgsConstructor
@Repository
public class OnAirRoomRepository {
	private final RedisTemplate<String, OnAirRoom> redisTemplate;

	public void createOnAirRoom(OnAirRoom onAirRoom) {
		redisTemplate.opsForValue().set(Integer.toString(onAirRoom.getRoomIdx()), onAirRoom);
	}
	
	public OnAirRoom getOnAirRoom(int roomIdx) {
		return redisTemplate.opsForValue().get(Integer.toString(roomIdx));
	}

	public void updateOnAirInfo(int roomInx, OnAirRoom onAirRoom){
		redisTemplate.opsForValue().set(Integer.toString(roomInx),onAirRoom);
	}

	public void deleteRoom(int roomIdx){
		redisTemplate.delete(Integer.toString(roomIdx));
	}
	
}
