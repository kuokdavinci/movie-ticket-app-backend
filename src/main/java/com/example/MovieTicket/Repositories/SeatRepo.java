package com.example.MovieTicket.Repositories;

import com.example.MovieTicket.Models.Seat;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepo extends JpaRepository<Seat, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))
    Optional<Seat> findByShowtime_ShowtimeIdAndSeatNumber(int showtimeId, int seatNumber);

    List<Seat> findByShowtime_ShowtimeId(int showtimeId);

    @Query("""
            SELECT s
            FROM Seat s
            LEFT JOIN Booking b ON b.seat = s AND b.showtime.showtimeId = :showtimeId
            WHERE s.showtime.showtimeId = :showtimeId
              AND b.booking_id IS NULL
            """)
    List<Seat> findAvailableSeatsByShowtimeId(@Param("showtimeId") int showtimeId);
}
