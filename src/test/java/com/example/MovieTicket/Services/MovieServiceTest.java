package com.example.MovieTicket.Services;

import com.example.MovieTicket.Models.Movie;
import com.example.MovieTicket.Repositories.MovieRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieServiceTest {

    @Mock
    private MovieRepo movieRepo;

    @InjectMocks
    private MovieService movieService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllMovies() {
        Movie movie1 = new Movie();
        Movie movie2 = new Movie();
        when(movieRepo.findAll()).thenReturn(Arrays.asList(movie1, movie2));

        List<Movie> result = movieService.getAllMovies();

        assertEquals(2, result.size());
        verify(movieRepo, times(1)).findAll();
    }

    @Test
    void testGetMovieById() {
        Movie movie = new Movie();
        movie.setMovie_id(1);
        when(movieRepo.findById(1)).thenReturn(Optional.of(movie));

        Movie result = movieService.getMovieById(1);

        assertNotNull(result);
        assertEquals(1, result.getMovie_id());
        verify(movieRepo, times(1)).findById(1);
    }

    @Test
    void testAddMovie() {
        Movie movie = new Movie();
        when(movieRepo.save(movie)).thenReturn(movie);

        Movie result = movieService.addMovie(movie);

        assertNotNull(result);
        verify(movieRepo, times(1)).save(movie);
    }

    @Test
    void testDeleteMovie() {
        movieService.deleteMovie(1);
        verify(movieRepo, times(1)).deleteById(1);
    }
    @Test
    void testSearchMovieByNameAndGenre_Found() {
        Movie movie1 = new Movie();
        movie1.setName("Avatar");
        when(movieRepo.searchMovieByNameAndGenre("Avatar")).thenReturn(Arrays.asList(movie1));

        List<Movie> result = movieService.searchMovieByNameAndGenre("Avatar");

        assertEquals(1, result.size());
        assertEquals("Avatar", result.get(0).getName());
        verify(movieRepo, times(1)).searchMovieByNameAndGenre("Avatar");
    }
    @Test
    void testSearchMovieByNameAndGenre_NotFound() {
        when(movieRepo.searchMovieByNameAndGenre("Unknown")).thenReturn(Arrays.asList());

        List<Movie> result = movieService.searchMovieByNameAndGenre("Unknown");

        assertTrue(result.isEmpty());
        verify(movieRepo, times(1)).searchMovieByNameAndGenre("Unknown");
    }
}
