package com.example.MovieTicket.DTOs;

import java.math.BigDecimal;

public record SeatDTO(
        int seatId,
        int seatNumber,
        BigDecimal price,
        int showtimeId,
        int movieId
) {
}
