package com.example.MovieTicket.Repositories;

import com.example.MovieTicket.Models.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepo extends JpaRepository<Seat, Integer> {
    Optional<Seat> findBySeatNumber(int seatNumber);

    List<Seat> findByShowtime_ShowtimeId(int showtimeId);
}