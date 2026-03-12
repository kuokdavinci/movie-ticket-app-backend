package com.example.MovieTicket.Services;

import com.example.MovieTicket.Models.Movie;
import com.example.MovieTicket.Repositories.MovieRepo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {
    private final MovieRepo repo;

    public MovieService(MovieRepo repo) {
        this.repo = repo;
    }

    @Cacheable(value = "moviesList", key = "'all'")
    public List<Movie> getAllMovies() {
        return repo.findAll();
    }

    @Cacheable(value = "moviesById", key = "#movieId", unless = "#result == null")
    public Movie getMovieById(int movieId) {
        return repo.findById(movieId).orElse(null);
    }

    @Caching(evict = {
            @CacheEvict(value = "moviesList", key = "'all'"),
            @CacheEvict(value = "moviesSearch", allEntries = true)
    })
    public Movie addMovie(Movie movie) {
        return repo.save(movie);
    }

    @Caching(evict = {
            @CacheEvict(value = "moviesList", key = "'all'"),
            @CacheEvict(value = "moviesById", key = "#movie.movieId"),
            @CacheEvict(value = "moviesSearch", allEntries = true)
    })
    public Movie updateMovie(Movie movie) {
        return repo.save(movie);
    }

    @Caching(evict = {
            @CacheEvict(value = "moviesList", key = "'all'"),
            @CacheEvict(value = "moviesById", key = "#movieId"),
            @CacheEvict(value = "moviesSearch", allEntries = true)
    })
    public void deleteMovie(int movieId) {
         repo.deleteById(movieId);
    }

    @Cacheable(value = "moviesSearch", key = "#keyword == null ? '' : #keyword.trim().toLowerCase()")
    public List<Movie> searchMovieByNameAndGenre(String keyword) {
        return repo.searchMovieByNameAndGenre(keyword);
    }
}
