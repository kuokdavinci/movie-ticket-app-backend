package com.example.MovieTicket.Services;

import com.example.MovieTicket.Models.Booking;
import com.example.MovieTicket.Models.Seat;
import com.example.MovieTicket.Models.Showtime;
import com.example.MovieTicket.Models.User;
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

@Service
public class BookingService {

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private ShowtimeRepo showtimeRepo;

    @Autowired
    private SeatRepo seatRepo;

    public List<Showtime> getShowtimesByMovie(int movieId) {
        return showtimeRepo.findByMovie_MovieIdAndStartTimeAfter(movieId, LocalTime.ofSecondOfDay(LocalTime.now().getHour()));
    }

    @Transactional
    public Booking bookTicket(int movieId, int showtimeId, int seatNumber, User user) {
        Showtime showtime = showtimeRepo.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu"));

        Seat seat = seatRepo.findBySeatNumber(seatNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ghế " + seatNumber));

        // Kiểm tra trùng ghế
        if (bookingRepo.existsByShowtimeAndSeat(showtime, seat)) {
            throw new RuntimeException("Ghế số " + seatNumber + " đã được đặt trước đó!");
        }

        Booking booking = new Booking();
        booking.setBookingTime(LocalDateTime.now());
        booking.setUser(user);
        booking.setShowtime(showtime);
        booking.setSeat(seat);
        booking.setPrice(seat.getPrice() != null ? seat.getPrice() : BigDecimal.ZERO);

        return bookingRepo.save(booking);
    }

    public List<Booking> getBookingsByUser(User user) {
        return bookingRepo.findByUser(user);
    }
    public List<Seat> getAvailableSeats(int showtimeId) {
        Showtime showtime = showtimeRepo.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("No available seat"));


        List<Seat> allSeats = seatRepo.findAll();

        List<Seat> bookedSeats = bookingRepo.findByShowtime(showtime)
                .stream()
                .map(Booking::getSeat)
                .toList();

        return allSeats.stream()
                .filter(seat -> !bookedSeats.contains(seat))
                .toList();
    }
}
