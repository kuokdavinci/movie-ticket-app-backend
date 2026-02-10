package com.example.MovieTicket.Controllers;

import com.example.MovieTicket.DTOs.BookingResponseDTO;
import com.example.MovieTicket.DTOs.SeatDTO;
import com.example.MovieTicket.Models.*;
import com.example.MovieTicket.Services.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController controller;

    private UserPrincipal principal;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("alice");
        principal = new UserPrincipal(user);
    }

    @Test
    void getAvailableShowtimes_returnsList() {
        Showtime showtime = new Showtime();
        when(bookingService.getShowtimesByMovie(1)).thenReturn(List.of(showtime));

        List<Showtime> result = controller.getAvailableShowtimes(1);

        assertEquals(1, result.size());
    }

    @Test
    void createBooking_callsService() {
        Booking booking = new Booking();
        when(bookingService.bookTicket(1, 2, 10, user)).thenReturn(booking);

        Booking result = controller.createBooking(1, Map.of("showtime_id", 2, "seat_number", 10), principal);

        assertEquals(booking, result);
    }

    @Test
    void getSeats_wrapsInMap() {
        SeatDTO dto = new SeatDTO(1, 10, null, 2, 3);
        when(bookingService.getAvailableSeats(2)).thenReturn(List.of(dto));

        Map<String, Object> result = controller.getSeats(2);

        assertTrue(result.containsKey("availableSeats"));
    }

    @Test
    void getUserBookings_returnsList() {
        BookingResponseDTO dto = new BookingResponseDTO(1, null, 10, null, LocalDateTime.now());
        when(bookingService.getBookingsByUser(user)).thenReturn(List.of(dto));

        List<BookingResponseDTO> result = controller.getUserBookings(principal);

        assertEquals(1, result.size());
    }

    @Test
    void deleteBooking_notFound() {
        when(bookingService.getBookingById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.deleteBooking(1, principal);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteBooking_forbidden() {
        User another = new User();
        another.setUsername("bob");

        Booking booking = new Booking();
        booking.setUser(another);
        when(bookingService.getBookingById(1)).thenReturn(Optional.of(booking));

        ResponseEntity<?> response = controller.deleteBooking(1, principal);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void deleteBooking_noContent() {
        Booking booking = new Booking();
        booking.setUser(user);
        when(bookingService.getBookingById(1)).thenReturn(Optional.of(booking));

        ResponseEntity<?> response = controller.deleteBooking(1, principal);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookingService).deleteBooking(1);
    }
}
