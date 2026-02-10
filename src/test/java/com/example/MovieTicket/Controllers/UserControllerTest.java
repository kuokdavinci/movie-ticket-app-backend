package com.example.MovieTicket.Controllers;

import com.example.MovieTicket.Models.User;
import com.example.MovieTicket.Services.JWTService;
import com.example.MovieTicket.Services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    private UserService service;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private UserController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_created() {
        User user = new User();
        when(service.register(user)).thenReturn(user);

        ResponseEntity<?> response = controller.register(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void register_conflict() {
        User user = new User();
        when(service.register(user)).thenThrow(new RuntimeException("Username already exists"));

        ResponseEntity<?> response = controller.register(user);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void login_returnsVerifyResult() {
        User user = new User();
        when(service.verify(user)).thenReturn("token");

        String result = controller.login(user);

        assertEquals("token", result);
    }

    @Test
    void getCurrentUser_unauthorizedWhenHeaderMissing() {
        ResponseEntity<?> response = controller.getCurrentUser(null);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void getCurrentUser_unauthorizedWhenNoUser() {
        when(jwtService.extractUsername("token")).thenReturn("alice");
        when(service.findByUsername("alice")).thenReturn(null);

        ResponseEntity<?> response = controller.getCurrentUser("Bearer token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void getCurrentUser_ok() {
        User user = new User();
        user.setUsername("alice");
        when(jwtService.extractUsername("token")).thenReturn("alice");
        when(service.findByUsername("alice")).thenReturn(user);

        ResponseEntity<?> response = controller.getCurrentUser("Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }
}
