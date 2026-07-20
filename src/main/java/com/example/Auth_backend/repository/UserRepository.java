package com.example.Auth_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Auth_backend.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    <User> Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}