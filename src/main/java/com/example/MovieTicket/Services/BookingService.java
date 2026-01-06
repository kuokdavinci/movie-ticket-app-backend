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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private ShowtimeRepo showtimeRepo;

    @Autowired
    private SeatRepo seatRepo;

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private SeatMapper seatMapper;

    public List<Showtime> getShowtimesByMovie(int movieId) {
        return showtimeRepo.findByMovie_MovieIdAndStartTimeAfter(movieId, LocalTime.ofSecondOfDay(LocalTime.now().getHour()));
    }

    @Transactional
    public Booking bookTicket(int movieId, int showtimeId, int seatNumber, User user) {
        Showtime showtime = showtimeRepo.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Showtime not found!"));

        Seat seat = seatRepo.findBySeatNumber(seatNumber)
                .orElseThrow(() -> new RuntimeException("Seat not found! " + seatNumber));

        if (bookingRepo.existsByShowtimeAndSeat(showtime, seat)) {
            throw new RuntimeException("Seat " + seatNumber + " is booked!");
        }

        Booking booking = new Booking();
        booking.setBookingTime(LocalDateTime.now());
        booking.setUser(user);
        booking.setShowtime(showtime);
        booking.setSeat(seat);
        booking.setPrice(seat.getPrice() != null ? seat.getPrice() : BigDecimal.ZERO);

        return bookingRepo.save(booking);
    }

    public List<BookingResponseDTO> getBookingsByUser(User user) {
        return bookingRepo.findByUser(user).stream().map(bookingMapper::toBookingDTO).collect(Collectors.toList());
    }
    public List<SeatDTO> getAvailableSeats(int showtimeId) {
        Showtime showtime = showtimeRepo.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Showtime doesn't exist"));

        Movie movie = showtime.getMovie();
        int movieId = (movie != null) ? movie.getMovieId() : 0;

        List<Seat> allSeats = seatRepo.findByShowtime_ShowtimeId(showtimeId);

        List<Seat> bookedSeats = bookingRepo.findByShowtime(showtime)
                .stream()
                .map(Booking::getSeat)
                .toList();

        return allSeats.stream()
                .filter(seat -> !bookedSeats.contains(seat))
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
