package com.example.MovieTicket.Services;

import com.example.MovieTicket.DTOs.BookingResponseDTO;
import com.example.MovieTicket.DTOs.SeatDTO;
import com.example.MovieTicket.Mappers.BookingMapper;
import com.example.MovieTicket.Mappers.SeatMapper;
import com.example.MovieTicket.Models.*;
import com.example.MovieTicket.Repositories.BookingRepo;
import com.example.MovieTicket.Repositories.SeatRepo;
import com.example.MovieTicket.Repositories.ShowtimeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock private BookingRepo bookingRepo;
    @Mock private ShowtimeRepo showtimeRepo;
    @Mock private SeatRepo seatRepo;
    @Mock private BookingMapper bookingMapper;
    @Mock private SeatMapper seatMapper;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getShowtimesByMovie_returnsRepoResult() {
        Showtime st = new Showtime();
        when(showtimeRepo.findByMovie_MovieIdAndStartTimeAfter(eq(1), any(LocalTime.class)))
                .thenReturn(List.of(st));

        List<Showtime> result = bookingService.getShowtimesByMovie(1);

        assertEquals(1, result.size());
        verify(showtimeRepo).findByMovie_MovieIdAndStartTimeAfter(eq(1), any(LocalTime.class));
    }

    @Test
    void bookTicket_success() {
        User user = new User();
        user.setUsername("alice");

        Movie movie = new Movie();
        movie.setMovieId(1);
        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        Seat seat = new Seat();
        seat.setSeatNumber(10);
        seat.setPrice(BigDecimal.valueOf(120000));

        when(showtimeRepo.findById(2)).thenReturn(Optional.of(showtime));
        when(seatRepo.findByShowtime_ShowtimeIdAndSeatNumber(2, 10)).thenReturn(Optional.of(seat));
        when(bookingRepo.saveAndFlush(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.bookTicket(1, 2, 10, user);

        assertEquals(user, result.getUser());
        assertEquals(showtime, result.getShowtime());
        assertEquals(seat, result.getSeat());
        assertEquals(BigDecimal.valueOf(120000), result.getPrice());
        assertNotNull(result.getBookingTime());
        verify(bookingRepo).saveAndFlush(any(Booking.class));
    }

    @Test
    void bookTicket_defaultPriceZeroWhenSeatPriceNull() {
        User user = new User();
        Movie movie = new Movie();
        movie.setMovieId(1);
        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        Seat seat = new Seat();
        seat.setSeatNumber(5);
        seat.setPrice(null);

        when(showtimeRepo.findById(2)).thenReturn(Optional.of(showtime));
        when(seatRepo.findByShowtime_ShowtimeIdAndSeatNumber(2, 5)).thenReturn(Optional.of(seat));
        when(bookingRepo.saveAndFlush(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.bookTicket(1, 2, 5, user);

        assertEquals(BigDecimal.ZERO, result.getPrice());
    }

    @Test
    void bookTicket_throwWhenShowtimeNotFound() {
        when(showtimeRepo.findById(2)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.bookTicket(1, 2, 10, new User()));

        assertEquals("Showtime not found!", ex.getMessage());
    }

    @Test
    void bookTicket_throwWhenSeatNotFound() {
        Movie movie = new Movie();
        movie.setMovieId(1);
        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        when(showtimeRepo.findById(2)).thenReturn(Optional.of(showtime));
        when(seatRepo.findByShowtime_ShowtimeIdAndSeatNumber(2, 10)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.bookTicket(1, 2, 10, new User()));

        assertTrue(ex.getMessage().contains("Seat not found"));
    }

    @Test
    void bookTicket_throwConflictWhenAlreadyBookedByUniqueConstraint() {
        Movie movie = new Movie();
        movie.setMovieId(1);
        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        Seat seat = new Seat();
        when(showtimeRepo.findById(2)).thenReturn(Optional.of(showtime));
        when(seatRepo.findByShowtime_ShowtimeIdAndSeatNumber(2, 10)).thenReturn(Optional.of(seat));
        when(bookingRepo.saveAndFlush(any(Booking.class))).thenThrow(new DataIntegrityViolationException("duplicate key"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> bookingService.bookTicket(1, 2, 10, new User()));

        assertEquals(409, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("is booked"));
    }

    @Test
    void getBookingsByUser_mapsToDto() {
        User user = new User();
        Booking booking = new Booking();
        BookingResponseDTO dto = new BookingResponseDTO(1, null, 3, BigDecimal.TEN, LocalDateTime.now());

        when(bookingRepo.findByUser(user)).thenReturn(List.of(booking));
        when(bookingMapper.toBookingDTO(booking)).thenReturn(dto);

        List<BookingResponseDTO> result = bookingService.getBookingsByUser(user);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void getAvailableSeats_returnsOnlyAvailableFromRepositoryQuery() {
        int showtimeId = 3;

        Showtime showtime = new Showtime();
        showtime.setShowtimeId(showtimeId);

        Seat seat2 = new Seat();
        seat2.setSeat_id(2);
        seat2.setSeatNumber(2);
        seat2.setShowtime(showtime);

        SeatDTO seatDTO = new SeatDTO(2, 2, null, showtimeId, 1);

        when(showtimeRepo.findById(showtimeId)).thenReturn(Optional.of(showtime));
        when(seatRepo.findAvailableSeatsByShowtimeId(showtimeId)).thenReturn(List.of(seat2));
        when(seatMapper.toSeatDTO(seat2)).thenReturn(seatDTO);

        List<SeatDTO> result = bookingService.getAvailableSeats(showtimeId);

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).seatNumber());
        verify(seatRepo).findAvailableSeatsByShowtimeId(showtimeId);
        verify(seatMapper).toSeatDTO(seat2);
    }

    @Test
    void getAvailableSeats_throwWhenShowtimeMissing() {
        when(showtimeRepo.findById(100)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookingService.getAvailableSeats(100));

        assertEquals("Showtime doesn't exist", ex.getMessage());
    }

    @Test
    void deleteBooking_callsRepo() {
        bookingService.deleteBooking(5);
        verify(bookingRepo).deleteById(5);
    }

    @Test
    void getBookingById_callsRepo() {
        Booking booking = new Booking();
        when(bookingRepo.findById(5)).thenReturn(Optional.of(booking));

        Optional<Booking> result = bookingService.getBookingById(5);

        assertTrue(result.isPresent());
        verify(bookingRepo).findById(5);
    }
}
