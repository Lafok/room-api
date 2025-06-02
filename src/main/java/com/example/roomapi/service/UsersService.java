package com.example.roomapi.service;

import com.example.roomapi.model.UserStatus;
import com.example.roomapi.model.Users;
import com.example.roomapi.repository.UserRepository; // 💡 ВАЖНО: правильный импорт
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsersService {

    private final UserRepository userRepository;

    public UsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean canChangeRoom(Users user) {
        return user.getStatus() == UserStatus.ACTIVE;
    }

    public Users getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // user status
    public void setUserStatus(UUID userId, UserStatus status) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(status);
        userRepository.save(user);
    }

}
