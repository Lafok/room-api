package com.example.roomapi.controller;

import com.example.roomapi.model.Room;
import com.example.roomapi.model.Users;
import com.example.roomapi.service.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public Room createRoom(@RequestParam String name) {
        return roomService.createRoom(name);
    }

    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable UUID id) {
        roomService.deleteRoom(id);
    }

    public static class AddUserRequest {
        public String name;
    }

    @PostMapping("/{roomId}/users")
    public Users addUserToRoom(@PathVariable UUID roomId, @RequestBody AddUserRequest request) {
        return roomService.addUserToRoom(roomId, request.name);
    }

}
