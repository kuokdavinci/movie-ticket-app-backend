package com.example.MovieTicket.Repositories;

import com.example.MovieTicket.Models.Booking;
import com.example.MovieTicket.Models.Seat;
import com.example.MovieTicket.Models.Showtime;
import com.example.MovieTicket.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Integer> {
    boolean existsByShowtimeAndSeat(Showtime showtime, Seat seat);
    List<Booking> findByUser(User user);
    List<Booking> findByShowtime(Showtime showtime);
}
