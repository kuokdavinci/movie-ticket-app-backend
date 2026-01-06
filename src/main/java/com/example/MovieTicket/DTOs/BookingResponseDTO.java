package com.example.MovieTicket.DTOs;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookingResponseDTO(
        int bookingId,
        ShowtimeDTO showtime,
        int seatNumber,
        BigDecimal price,
        LocalDateTime bookingTime
) {
}
