package com.example.MovieTicket.Services;

import com.example.MovieTicket.Models.Movie;
import com.example.MovieTicket.Repositories.MovieRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {
    @Autowired
    MovieRepo repo;

    public List<Movie> getAllMovies() {
        return repo.findAll();
    }

    public Movie getMovieById(int movieId) {
        return repo.findById(movieId).orElse(null);
    }

    public Movie addMovie(Movie movie) {
        return repo.save(movie);
    }

    public Movie updateMovie(Movie movie) {
        return repo.save(movie);
    }

    public void deleteMovie(int movieId) {
         repo.deleteById(movieId);
    }

    public List<Movie> searchMovieByNameAndGenre(String keyword) {
        return repo.searchMovieByNameAndGenre(keyword);
    }
}
