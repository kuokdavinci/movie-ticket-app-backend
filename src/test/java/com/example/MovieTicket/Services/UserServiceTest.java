package com.example.MovieTicket.Services;

import com.example.MovieTicket.Models.User;
import com.example.MovieTicket.Repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock private UserRepo repo;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JWTService jwtService;
    @Mock private Authentication authentication;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_success_encodesPasswordAndSetsRole() {
        User user = new User();
        user.setUsername("alice");
        user.setPassword("123456");

        when(repo.existsByUsername("alice")).thenReturn(false);
        when(repo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.register(user);

        assertNotEquals("123456", saved.getPassword());
        assertEquals("ROLE_USER", saved.getRole());
        verify(repo).save(user);
    }

    @Test
    void register_throwWhenUsernameExists() {
        User user = new User();
        user.setUsername("alice");
        when(repo.existsByUsername("alice")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.register(user));

        assertEquals("Username already exists", ex.getMessage());
        verify(repo, never()).save(any());
    }

    @Test
    void verify_returnsTokenWhenAuthenticated() {
        User user = new User();
        user.setUsername("alice");
        user.setPassword("pw");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken("alice")).thenReturn("token");

        String result = userService.verify(user);

        assertEquals("token", result);
        verify(jwtService).generateToken("alice");
    }

    @Test
    void verify_returnsFailedWhenNotAuthenticated() {
        User user = new User();
        user.setUsername("alice");
        user.setPassword("pw");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        String result = userService.verify(user);

        assertEquals("Failed", result);
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void findByUsername_delegatesToRepo() {
        User user = new User();
        when(repo.findByUsername("alice")).thenReturn(user);

        User result = userService.findByUsername("alice");

        assertEquals(user, result);
    }
}
