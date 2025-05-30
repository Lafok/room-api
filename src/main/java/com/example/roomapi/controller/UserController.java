package com.example.roomapi.controller;

import com.example.roomapi.model.Users;
import com.example.roomapi.service.RoomService;
import com.example.roomapi.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final RoomService roomService;
    private final UsersService usersService;

    public UserController(RoomService roomService, UsersService usersService) {
        this.roomService = roomService;
        this.usersService = usersService;
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID userId) {
        Users user = usersService.getById(userId);

        if (!usersService.canChangeRoom(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("User cannot change room at the moment.");
        }

        roomService.deleteUser(userId); // если можно — удаляем
        return ResponseEntity.ok().build();
    }
}
