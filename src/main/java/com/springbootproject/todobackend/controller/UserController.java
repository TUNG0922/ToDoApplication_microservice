package com.springbootproject.todobackend.controller;

import com.springbootproject.todobackend.model.User;
import com.springbootproject.todobackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // allow Vue frontend
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Sign-up endpoint
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists!");
        }

        userRepository.save(user);
        return ResponseEntity.ok("You have successfully signed up!");
    }

    // Sign-in endpoint
    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signin(@RequestBody User requestUser) {
        Optional<User> userOptional = userRepository.findByEmail(requestUser.getEmail());
        Map<String, Object> response = new HashMap<>();

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(requestUser.getPassword())) {
                response.put("success", true);
                response.put("message", "Sign-in successful");
                Map<String, Object> userData = new HashMap<>();
                userData.put("name", user.getName());
                userData.put("email", user.getEmail());
                response.put("user", userData);
            } else {
                response.put("success", false);
                response.put("message", "Invalid email or password");
            }
        } else {
            response.put("success", false);
            response.put("message", "Invalid email or password");
        }

        return ResponseEntity.ok(response);
    }

    // Get all users (for assignee dropdown)
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch users: " + e.getMessage());
        }
    }
}
