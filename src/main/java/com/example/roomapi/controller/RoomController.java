package com.example.roomapi.controller;

import com.example.roomapi.model.Room;
import com.example.roomapi.model.Users;
import com.example.roomapi.service.RoomService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }


    public static class AddUserRequest {
        public String name;
    }

    @PostMapping
    public ResponseEntity<?> createRoom(@RequestParam String name) {
        try {
            Room room = roomService.createRoom(name);
            return ResponseEntity.status(HttpStatus.CREATED).body(room);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
