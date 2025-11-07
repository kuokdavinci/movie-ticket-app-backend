package com.example.MovieTicket.Models;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SeatDTO {
    private int seatId;
    private int seatNumber;
    private BigDecimal price;
    private int showtimeId;

    public SeatDTO(int seatId, int seatNumber, BigDecimal price, int movieId, int showtimeId) {
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.price = price;
        this.showtimeId = showtimeId;
    }
}

