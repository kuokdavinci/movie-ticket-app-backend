package com.example.MovieTicket.Models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "seats")
@Getter
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int seat_id;

    private int seatNumber;
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "showtime_id")
    private Showtime showtime;

    public int getSeatId() {
        return this.seat_id;
    }
}
