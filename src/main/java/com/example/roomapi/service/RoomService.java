package com.example.roomapi.service;

import com.example.roomapi.model.Room;
import com.example.roomapi.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room createRoom(String name) {
        Room room = new Room(name);
        return roomRepository.save(room);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public void deleteRoom(UUID id) {
        roomRepository.deleteById(id);
    }


}
