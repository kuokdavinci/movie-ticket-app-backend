package com.example.MovieTicket.Services;

import com.example.MovieTicket.Models.Booking;
import com.example.MovieTicket.Models.Movie;
import com.example.MovieTicket.Models.Seat;
import com.example.MovieTicket.Models.Showtime;
import com.example.MovieTicket.Models.User;
import com.example.MovieTicket.Repositories.BookingRepo;
import com.example.MovieTicket.Repositories.MovieRepo;
import com.example.MovieTicket.Repositories.SeatRepo;
import com.example.MovieTicket.Repositories.ShowtimeRepo;
import com.example.MovieTicket.Repositories.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@ActiveProfiles("test")
class BookingPessimisticLockTest {

    @Autowired private BookingService bookingService;
    @Autowired private BookingRepo bookingRepo;
    @Autowired private MovieRepo movieRepo;
    @Autowired private ShowtimeRepo showtimeRepo;
    @Autowired private SeatRepo seatRepo;
    @Autowired private UserRepo userRepo;
    @Autowired private PlatformTransactionManager transactionManager;
    @Autowired private EntityManager entityManager;

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    @AfterEach
    void tearDown() {
        executor.shutdownNow();
    }

    @Test
    void pessimisticLock_blocksConcurrentBookingForSameSeat() throws Exception {
        Movie movie = new Movie();
        movie.setName("Lock Test");
        movie.setDescription("desc");
        movie.setDuration(100);
        movie.setGenre("test");
        movie.setImageURL("img");
        movie = movieRepo.save(movie);
        final int movieId = movie.getMovieId();

        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setStartTime(LocalTime.now().plusHours(2));
        showtime.setEndTime(LocalTime.now().plusHours(3));
        showtime = showtimeRepo.save(showtime);
        final Showtime savedShowtime = showtime;
        final int showtimeId = savedShowtime.getShowtimeId();

        Seat seat = new Seat();
        seat.setShowtime(showtime);
        seat.setSeatNumber(1);
        seat.setPrice(BigDecimal.TEN);
        seat = seatRepo.save(seat);
        final int seatId = seat.getSeatId();
        final int seatNumber = seat.getSeatNumber();

        User user1 = new User();
        user1.setUsername("u1");
        user1.setPassword("pw");
        user1.setRole("USER");
        user1 = userRepo.save(user1);
        final User savedUser1 = user1;

        User user2 = new User();
        user2.setUsername("u2");
        user2.setPassword("pw");
        user2.setRole("USER");
        user2 = userRepo.save(user2);
        final User savedUser2 = user2;

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch locked = new CountDownLatch(1);

        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
        txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        Future<?> locker = executor.submit(() -> {
            try {
                start.await(5, TimeUnit.SECONDS);
                txTemplate.execute(status -> {
                    Seat lockedSeat = entityManager.find(Seat.class, seatId, LockModeType.PESSIMISTIC_WRITE);
                    assertNotNull(lockedSeat);
                    locked.countDown();
                    sleep(600);

                    Booking booking = new Booking();
                    booking.setBookingTime(LocalDateTime.now());
                    booking.setUser(savedUser1);
                    booking.setShowtime(savedShowtime);
                    booking.setSeat(lockedSeat);
                    booking.setPrice(BigDecimal.TEN);
                    bookingRepo.saveAndFlush(booking);
                    return null;
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Future<Long> contender = executor.submit(() -> {
            start.await(5, TimeUnit.SECONDS);
            locked.await(5, TimeUnit.SECONDS);
            long t0 = System.nanoTime();
            try {
                bookingService.bookTicket(movieId, showtimeId, seatNumber, savedUser2);
                fail("Expected conflict booking same seat");
            } catch (ResponseStatusException ex) {
                assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
            }
            long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0);
            return elapsedMs;
        });

        start.countDown();

        locker.get(5, TimeUnit.SECONDS);
        long contenderElapsed = contender.get(5, TimeUnit.SECONDS);

        assertTrue(contenderElapsed >= 300, "Expected contender to block on lock, elapsed=" + contenderElapsed + "ms");
        assertEquals(1, bookingRepo.count());
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
