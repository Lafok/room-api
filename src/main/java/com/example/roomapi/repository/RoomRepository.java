package com.example.roomapi.repository;

import com.example.roomapi.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    boolean existsByName(String name);

    Optional<Room> findByName(String name);
}
