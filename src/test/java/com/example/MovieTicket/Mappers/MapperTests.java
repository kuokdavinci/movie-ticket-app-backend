package com.example.MovieTicket.Mappers;

import com.example.MovieTicket.DTOs.BookingResponseDTO;
import com.example.MovieTicket.DTOs.SeatDTO;
import com.example.MovieTicket.DTOs.ShowtimeDTO;
import com.example.MovieTicket.Models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class MapperTests {

    private SeatMapper seatMapper;
    private ShowtimeMapper showtimeMapper;

    @Mock
    private ShowtimeMapper mockedShowtimeMapper;

    @InjectMocks
    private BookingMapper bookingMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        seatMapper = new SeatMapper();
        showtimeMapper = new ShowtimeMapper();
    }

    @Test
    void seatMapper_mapsCorrectly() {
        Movie movie = new Movie();
        movie.setMovieId(7);

        Showtime showtime = new Showtime();
        showtime.setShowtimeId(11);
        showtime.setMovie(movie);

        Seat seat = new Seat();
        seat.setSeat_id(3);
        seat.setSeatNumber(20);
        seat.setPrice(BigDecimal.valueOf(50000));
        seat.setShowtime(showtime);

        SeatDTO dto = seatMapper.toSeatDTO(seat);

        assertEquals(3, dto.seatId());
        assertEquals(20, dto.seatNumber());
        assertEquals(11, dto.showtimeId());
        assertEquals(7, dto.movieId());
    }

    @Test
    void showtimeMapper_mapsCorrectly() {
        Movie movie = new Movie();
        movie.setName("Inception");
        movie.setImageURL("img.png");

        Showtime showtime = new Showtime();
        showtime.setStartTime(LocalTime.of(9, 0));
        showtime.setEndTime(LocalTime.of(11, 30));
        showtime.setMovie(movie);

        ShowtimeDTO dto = showtimeMapper.toShowtimeDTO(showtime);

        assertEquals(LocalTime.of(9, 0), dto.startTime());
        assertEquals(LocalTime.of(11, 30), dto.endTime());
        assertEquals("Inception", dto.name());
        assertEquals("img.png", dto.imageUrl());
    }

    @Test
    void bookingMapper_mapsCorrectly() {
        ShowtimeDTO showtimeDTO = new ShowtimeDTO(LocalTime.NOON, LocalTime.MIDNIGHT, "Movie", "img");

        Seat seat = new Seat();
        seat.setSeat_id(9);

        Booking booking = new Booking();
        booking.setBooking_id(4);
        booking.setSeat(seat);
        booking.setPrice(BigDecimal.valueOf(75000));
        booking.setBookingTime(LocalDateTime.of(2026, 1, 1, 10, 0));
        booking.setShowtime(new Showtime());

        when(mockedShowtimeMapper.toShowtimeDTO(booking.getShowtime())).thenReturn(showtimeDTO);

        BookingResponseDTO dto = bookingMapper.toBookingDTO(booking);

        assertEquals(4, dto.bookingId());
        assertEquals(9, dto.seatNumber());
        assertEquals(BigDecimal.valueOf(75000), dto.price());
        assertEquals(showtimeDTO, dto.showtime());
    }
}
