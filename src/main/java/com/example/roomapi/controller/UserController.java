package com.example.roomapi.controller;

import com.example.roomapi.service.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final RoomService roomService;

    public UserController(RoomService roomService) {
        this.roomService = roomService;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable UUID userId) {
        roomService.deleteUser(userId);
    }
}
