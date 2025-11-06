package com.example.MovieTicket.Repositories;

import com.example.MovieTicket.Models.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowtimeRepo extends JpaRepository<Showtime, Integer> {
    List<Showtime> findByMovie_MovieId(int movieId);
    List<Showtime> findByMovie_MovieIdAndStartTimeAfter(int movieId, LocalTime now);
    Optional<Showtime> findByShowtimeIdAndStartTimeAfter(int showtimeId, LocalTime now);
}
