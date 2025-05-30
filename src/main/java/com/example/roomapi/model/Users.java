package com.example.roomapi.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Users {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    public Users() {
    }

    public Users(String name, Room room) {
        this.name = name;
        this.room = room;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Room getRoom() {
        return room;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
