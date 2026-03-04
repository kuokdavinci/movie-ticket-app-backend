package com.example.MovieTicket.Repositories;

import com.example.MovieTicket.Models.Booking;
import com.example.MovieTicket.Models.Seat;
import com.example.MovieTicket.Models.Showtime;
import com.example.MovieTicket.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Integer> {
    boolean existsByShowtimeAndSeat(Showtime showtime, Seat seat);
    List<Booking> findByUser(User user);

    @Query("""
            SELECT s
            FROM Seat s
            LEFT JOIN Booking b ON b.seat = s AND b.showtime.showtimeId = :showtimeId
            WHERE s.showtime.showtimeId = :showtimeId AND b IS NULL
            """)
    List<Seat> findAvailableSeatsByShowtimeId(int showtimeId);
}
