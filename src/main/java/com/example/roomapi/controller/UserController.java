package com.example.roomapi.controller;

import com.example.roomapi.model.UserStatus;
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

        roomService.deleteUser(userId); // if possible - delete
        return ResponseEntity.ok().build();
    }

    // change user status
    @PatchMapping("/{userId}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable UUID userId, @RequestParam String status) {
        try {
            UserStatus newStatus = UserStatus.valueOf(status.toUpperCase());
            usersService.setUserStatus(userId, newStatus);
            return ResponseEntity.ok("User status updated");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
