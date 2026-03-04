package com.example.MovieTicket.Services;

import com.example.MovieTicket.DTOs.BookingResponseDTO;

import com.example.MovieTicket.DTOs.SeatDTO;
import com.example.MovieTicket.Mappers.BookingMapper;
import com.example.MovieTicket.Mappers.SeatMapper;
import com.example.MovieTicket.Models.*;
import com.example.MovieTicket.Repositories.BookingRepo;
import com.example.MovieTicket.Repositories.SeatRepo;
import com.example.MovieTicket.Repositories.ShowtimeRepo;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private final BookingRepo bookingRepo;
    private final ShowtimeRepo showtimeRepo;
    private final SeatRepo seatRepo;
    private final BookingMapper bookingMapper;
    private final SeatMapper seatMapper;

    public BookingService(BookingRepo bookingRepo,
                          ShowtimeRepo showtimeRepo,
                          SeatRepo seatRepo,
                          BookingMapper bookingMapper,
                          SeatMapper seatMapper) {
        this.bookingRepo = bookingRepo;
        this.showtimeRepo = showtimeRepo;
        this.seatRepo = seatRepo;
        this.bookingMapper = bookingMapper;
        this.seatMapper = seatMapper;
    }

    public List<Showtime> getShowtimesByMovie(int movieId) {
        return showtimeRepo.findByMovie_MovieIdAndStartTimeAfter(movieId, LocalTime.ofSecondOfDay(LocalTime.now().getHour()));
    }

    @Transactional
    public Booking bookTicket(int movieId, int showtimeId, int seatNumber, User user) {
        Showtime showtime = showtimeRepo.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Showtime not found!"));

        Seat seat = seatRepo.findByShowtime_ShowtimeIdAndSeatNumber(showtimeId, seatNumber)
                .orElseThrow(() -> new RuntimeException("Seat not found! " + seatNumber));

        Booking booking = new Booking();
        booking.setBookingTime(LocalDateTime.now());
        booking.setUser(user);
        booking.setShowtime(showtime);
        booking.setSeat(seat);
        booking.setPrice(seat.getPrice() != null ? seat.getPrice() : BigDecimal.ZERO);

        try {
            return bookingRepo.saveAndFlush(booking);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Seat " + seatNumber + " is booked!", e);
        }
    }

    public List<BookingResponseDTO> getBookingsByUser(User user) {
        return bookingRepo.findByUser(user).stream().map(bookingMapper::toBookingDTO).collect(Collectors.toList());
    }

    public List<SeatDTO> getAvailableSeats(int showtimeId) {
        if (showtimeRepo.findById(showtimeId).isEmpty()) {
            throw new RuntimeException("Showtime doesn't exist");
        }

        return seatRepo.findAvailableSeatsByShowtimeId(showtimeId)
                .stream()
                .map(seatMapper::toSeatDTO)
                .collect(Collectors.toList());
    }

    public void deleteBooking(int bookingId) {
        bookingRepo.deleteById(bookingId);
    }


    public Optional<Booking> getBookingById(int bookingId) {
        return bookingRepo.findById(bookingId);
    }
}
