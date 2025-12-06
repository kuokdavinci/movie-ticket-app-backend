package com.example.MovieTicket.DTOs;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookingResponseDTO(
        int userId,
        int showtimeId,
        int seatID,
        BigDecimal price,
        LocalDateTime bookingTime
) {
}
