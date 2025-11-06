package com.example.MovieTicket.Controllers;

import com.example.MovieTicket.Models.*;
import com.example.MovieTicket.Services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movies/{movie_id}/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/show-times")
    public List<Showtime> getAvailableShowtimes(@PathVariable("movie_id") int movie_id) {
        return bookingService.getShowtimesByMovie(movie_id);
    }

    @PostMapping
    public Booking createBooking(
            @PathVariable("movie_id") int movie_id,
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserPrincipal principal) {

        int showtimeId = (int) body.get("showtime_id");
        int seatNumber = (int) body.get("seat_number");

        User user = principal.getUser();

        return bookingService.bookTicket(movie_id, showtimeId, seatNumber, user);
    }
    @GetMapping("/show-times/{showtime_id}/seats")
    public Map<String, Object> getSeats(@PathVariable("showtime_id") int showtime_id) {
        List<Seat> availableSeats = bookingService.getAvailableSeats(showtime_id);

        return Map.of(
                "availableSeats", availableSeats
        );
    }

    @GetMapping("/my-bookings")
    public List<Booking> getUserBookings(@AuthenticationPrincipal UserPrincipal principal) {
        User user = principal.getUser();
        return bookingService.getBookingsByUser(user);
    }
}
