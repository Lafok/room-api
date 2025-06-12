package com.example.roomapi.service;

import com.example.roomapi.model.Room;
import com.example.roomapi.model.Users;
import com.example.roomapi.repository.RoomRepository;
import com.example.roomapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }


    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }


    @Autowired
    private UserRepository userRepository;

    public Users addUserToRoom(UUID roomId, String name) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        Users user = new Users(name, room);
        return userRepository.save(user);
    }

    // GET users
    public List<Users> getUsersInRoom(UUID roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        return room.getUsers();
    }

    // DEL users
    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    public Room createRoom(String name) {
        if (roomRepository.existsByName(name)) {
            throw new IllegalStateException("Room with name '" + name + "' already exists");
        }
        Room room = new Room(name);
        return roomRepository.save(room);
    }


    @Transactional
    public Room getOrCreateRoom(String name) {
        return roomRepository.findByName(name)
                .orElseGet(() -> createRoom(name));
    }

    @Transactional
    public void deleteRoomByName(String name) {
        Room room = roomRepository.findByName(name)
                .orElseThrow(() -> new IllegalStateException("Room with name '" + name + "' not found."));

        // Safety check: prevent deleting rooms with users
        if (room.getUsers() != null && !room.getUsers().isEmpty()) {
            throw new IllegalStateException("Cannot delete room '" + name + "' because it is not empty. Please ask all users to /leave first.");
        }

        roomRepository.delete(room);
    }

    @Transactional
    public void deleteRoom(UUID id) {
        roomRepository.deleteById(id);
    }


}
