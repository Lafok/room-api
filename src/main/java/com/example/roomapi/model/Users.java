package com.example.roomapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Users {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @JsonIgnore
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

    //user status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    public void setStatus(UserStatus status) {
        this.status = status;
    }
    public UserStatus getStatus() {
        return status;
    }

}
