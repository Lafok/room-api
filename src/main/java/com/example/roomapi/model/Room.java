package com.example.roomapi.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Room {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    // Constructors
    public Room() {
    }

    public Room(String name) {
        this.name = name;
    }

    // Getters & Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Users> users = new ArrayList<>();

    public List<Users> getUsers() {
        return users;
    }

    public void addUser(Users user) {
        users.add(user);
        user.setRoom(this);
    }
}
