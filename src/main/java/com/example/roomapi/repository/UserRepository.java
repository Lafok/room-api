package com.example.roomapi.repository;

import com.example.roomapi.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<Users, UUID> {
}
