package com.springbootproject.todobackend.repository;

import com.springbootproject.todobackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    // Add this method
    Optional<User> findByEmail(String email);
}
