package com.example.MovieTicket.Services;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

class JWTServiceTest {

    @Test
    void generateExtractAndValidateToken_success() {
        JWTService jwtService = new JWTService();

        String token = jwtService.generateToken("alice");
        UserDetails userDetails = User.withUsername("alice").password("pw").roles("USER").build();

        assertNotNull(token);
        assertEquals("alice", jwtService.extractUsername(token));
        assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    void validateToken_falseForDifferentUser() {
        JWTService jwtService = new JWTService();

        String token = jwtService.generateToken("alice");
        UserDetails another = User.withUsername("bob").password("pw").roles("USER").build();

        assertFalse(jwtService.validateToken(token, another));
    }
}
