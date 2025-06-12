package com.example.roomapi.repository;

import com.example.roomapi.model.Room;
import com.example.roomapi.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {
    boolean existsByName(String name);

    Optional<Users> findByName(String name);

}
