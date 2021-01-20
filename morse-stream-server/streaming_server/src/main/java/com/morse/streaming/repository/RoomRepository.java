package com.morse.streaming.repository;

import com.morse.streaming.model.RoomHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<RoomHistory,Long> {

}
