package com.example.MovieTicket.Controllers;

import com.example.MovieTicket.DTOs.UserResponseDTO;
import com.example.MovieTicket.Models.User;
import com.example.MovieTicket.Services.JWTService;
import com.example.MovieTicket.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class UserController {
    private final UserService service;
    private final JWTService jwtService;

    public UserController(UserService service, JWTService jwtService) {
        this.service = service;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user){
        try {
            User savedUser = service.register(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(toUserResponse(savedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to register: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public String login(@RequestBody User user){
        return service.verify(user);
    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Missing token");
        }
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtService.extractUsername(token);
            User user = service.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: User not found");
            }
            return ResponseEntity.ok(toUserResponse(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token");
        }
    }

    private UserResponseDTO toUserResponse(User user) {
        return new UserResponseDTO(user.getUser_id(), user.getUsername(), user.getRole());
    }
}
