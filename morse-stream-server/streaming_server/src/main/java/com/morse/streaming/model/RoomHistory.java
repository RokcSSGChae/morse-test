package com.morse.streaming.model;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "RoomsHistory")
public class RoomHistory{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_idx")
    private Long roomIdx;

    @NotNull
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "end_stream_at")
    private Date endStreamAt;

    @Column(name = "storage_location")
    String saveFile;
}
