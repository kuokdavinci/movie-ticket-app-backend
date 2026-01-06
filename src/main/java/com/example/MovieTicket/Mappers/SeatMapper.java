package com.example.MovieTicket.Mappers;

import com.example.MovieTicket.Models.Seat;
import com.example.MovieTicket.DTOs.SeatDTO;
import org.springframework.stereotype.Service;

@Service
public class SeatMapper {
    public SeatDTO toSeatDTO(Seat seat){
        return new SeatDTO(seat.getSeatId(), seat.getSeatNumber(), seat.getPrice(),seat.getShowtime().getShowtimeId(),seat.getShowtime().getMovie().getMovieId());
    }
}
