package com.example.MovieTicket.Mappers;

import com.example.MovieTicket.DTOs.BookingResponseDTO;
import com.example.MovieTicket.Models.Booking;
import org.springframework.stereotype.Service;

@Service
public class BookingMapper {
    public BookingResponseDTO toBookingDTO(Booking booking){
        return new BookingResponseDTO(booking.getUser().getUser_id(),booking.getShowtime().getShowtimeId(),booking.getSeat().getSeatId(),booking.getPrice(),booking.getBookingTime());
    }
}
