package com.example.MovieTicket.Mappers;

import com.example.MovieTicket.DTOs.BookingResponseDTO;
import com.example.MovieTicket.DTOs.ShowtimeDTO;
import com.example.MovieTicket.Models.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingMapper {
    @Autowired
    private ShowtimeMapper showtimeMapper;
    public BookingResponseDTO toBookingDTO(Booking booking){
        ShowtimeDTO showtimeDTO = showtimeMapper.toShowtimeDTO(booking.getShowtime());
        return new BookingResponseDTO(booking.getBooking_id(),showtimeDTO,booking.getSeat().getSeatId(),booking.getPrice(),booking.getBookingTime());
    }
}
