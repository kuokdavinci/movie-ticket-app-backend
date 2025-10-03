package com.example.MovieTicket.Controllers;

import com.example.MovieTicket.Models.Movie;
import com.example.MovieTicket.Services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class MovieController {
    @Autowired
    private MovieService service;

    @GetMapping("/movies")
    public ResponseEntity<List<Movie>> getAllMovies() {
        return new ResponseEntity<>(service.getAllMovies(), HttpStatus.OK);
    }

    @GetMapping("/movies/{movieId}")
    public ResponseEntity<?> getMovieById(@PathVariable int movieId) {
        Movie movie = service.getMovieById(movieId);
        if (movie != null)
            return new ResponseEntity<>(movie, HttpStatus.OK);
        else
            return new ResponseEntity<>("Movie not found!",HttpStatus.NOT_FOUND);
    }

    @PostMapping("/movies")
    public ResponseEntity<?> addMovie(@RequestBody Movie movie) {
        try {
            return new ResponseEntity<>(service.addMovie(movie), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create movie", HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/movies/{movieId}")
    public ResponseEntity<?> updateMovie(@PathVariable int movieId, @RequestBody Movie movie){
        try{
             movie.setMovie_id(movieId);
            return new ResponseEntity<>(service.updateMovie(movie),HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>("Failed to update movie",HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/movies/{movieId}")
    public ResponseEntity<?> deleteMovie(@PathVariable int movieId) {
        try {
            Movie movie = service.getMovieById(movieId);
            if (movie != null) {
                service.deleteMovie(movieId);
                return ResponseEntity.noContent().build();
            } else
                return new ResponseEntity<>("Movie not found!", HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>("Failed to delete movie",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
