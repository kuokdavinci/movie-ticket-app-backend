package com.example.MovieTicket.Services;

import com.example.MovieTicket.Models.User;
import com.example.MovieTicket.Repositories.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepo repo;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public UserService(UserRepo repo, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.repo = repo;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public User register(User user) {
        if (repo.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");

        return repo.save(user);
    }

    public String verify(User user) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
        if(authentication.isAuthenticated())
            return jwtService.generateToken(user.getUsername());
        return "Failed";
    }
    public User findByUsername(String username) {
        return repo.findByUsername(username);
    }
}
