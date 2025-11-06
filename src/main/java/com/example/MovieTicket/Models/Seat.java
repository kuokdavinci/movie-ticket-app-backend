package com.example.MovieTicket.Models;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int seat_id;

    private int seatNumber;
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "showtime_id")
    private Showtime showtime;
}
