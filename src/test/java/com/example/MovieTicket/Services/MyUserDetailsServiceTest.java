package com.example.MovieTicket.Services;

import com.example.MovieTicket.Models.User;
import com.example.MovieTicket.Models.UserPrincipal;
import com.example.MovieTicket.Repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class MyUserDetailsServiceTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private MyUserDetailsService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_success() {
        User user = new User();
        user.setUsername("alice");
        user.setPassword("secret");
        user.setRole("ROLE_USER");
        when(userRepo.findByUsername("alice")).thenReturn(user);

        UserDetails result = service.loadUserByUsername("alice");

        assertInstanceOf(UserPrincipal.class, result);
        assertEquals("alice", result.getUsername());
        assertEquals("secret", result.getPassword());
    }

    @Test
    void loadUserByUsername_throwWhenMissing() {
        when(userRepo.findByUsername("missing")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("missing"));
    }
}
