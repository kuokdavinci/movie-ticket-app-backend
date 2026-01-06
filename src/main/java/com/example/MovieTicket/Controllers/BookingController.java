package com.example.MovieTicket.Controllers;

import com.example.MovieTicket.DTOs.BookingResponseDTO;
import com.example.MovieTicket.DTOs.SeatDTO;
import com.example.MovieTicket.Models.*;
import com.example.MovieTicket.Services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/movies/{movie_id}/bookings/show-times")
    public List<Showtime> getAvailableShowtimes(@PathVariable("movie_id") int movie_id) {
        return bookingService.getShowtimesByMovie(movie_id);
    }

    @PostMapping("/movies/{movie_id}/bookings")
    public Booking createBooking(
            @PathVariable("movie_id") int movie_id,
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserPrincipal principal) {

        int showtimeId = (int) body.get("showtime_id");
        int seatNumber = (int) body.get("seat_number");

        User user = principal.getUser();

        return bookingService.bookTicket(movie_id, showtimeId, seatNumber, user);
    }
    @GetMapping("/movies/{movie_id}/bookings/show-times/{showtime_id}/seats")
    public Map<String, Object> getSeats(@PathVariable("showtime_id") int showtime_id) {
        List<SeatDTO> availableSeats = bookingService.getAvailableSeats(showtime_id);

        return Map.of(
                "availableSeats", availableSeats
        );
    }

    @GetMapping("/my-bookings")
    public List<BookingResponseDTO> getUserBookings(@AuthenticationPrincipal UserPrincipal principal) {
        User user = principal.getUser();
        return bookingService.getBookingsByUser(user);
    }
    @DeleteMapping("/my-bookings/{bookingId}")
    public ResponseEntity<?> deleteBooking(@PathVariable int bookingId,
                                           @AuthenticationPrincipal UserPrincipal principal) {
        try {
            Optional<Booking> bookingOpt = bookingService.getBookingById(bookingId);

            if (bookingOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Booking not found!");
            }

            Booking booking = bookingOpt.get();

            User currentUser = principal.getUser();
            if (!booking.getUser().getUsername().equals(currentUser.getUsername())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You are not allowed to delete this booking!");
            }
            bookingService.deleteBooking(bookingId);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete booking: " + e.getMessage());
        }
    }
}
