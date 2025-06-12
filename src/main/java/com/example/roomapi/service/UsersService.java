package com.example.roomapi.service;

import com.example.roomapi.model.Room;
import com.example.roomapi.model.UserStatus;
import com.example.roomapi.model.Users;
import com.example.roomapi.repository.RoomRepository;
import com.example.roomapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsersService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository; // <-- добавлено

    public UsersService(UserRepository userRepository, RoomRepository roomRepository) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }

    public Users getById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean canChangeRoom(Users user) {
        return user.getStatus() == UserStatus.ACTIVE;
    }

    public void setUserStatus(UUID userId, UserStatus status) {
        Users user = getById(userId);
        user.setStatus(status);
        userRepository.save(user);
    }

    public Users createUser(String name, UUID roomId) {
        if (userRepository.existsByName(name)) {
            throw new IllegalStateException("User with this name already exists");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        Users user = new Users(name, room);
        return userRepository.save(user);
    }

    public void removeByName(String name) {
        Users user = userRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    public void setStatusByName(String name, UserStatus status) {
        Users user = userRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(status);
        userRepository.save(user);
    }


    //for telegram, unique name
    public void addUser(Users user) {
        Optional<Users> existing = userRepository.findByName(user.getName());
        if (existing.isPresent()) {
            throw new RuntimeException("user with this name already exists");
        }
        userRepository.save(user);
    }
    public boolean existsByName(String name) {
        return userRepository.existsByName(name);
    }

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

}