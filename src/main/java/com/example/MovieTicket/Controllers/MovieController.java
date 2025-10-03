package com.example.MovieTicket.Controllers;

import com.example.MovieTicket.Models.Movie;
import com.example.MovieTicket.Services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class MovieController {
    @Autowired
    private MovieService service;


    @GetMapping("/movies")
    public List<Movie> getAllMovies(){
        return service.getAllMovies();
    }
    @GetMapping("/movies/{movieId}")
    public Movie getMovieById(@PathVariable int movieId){
        return service.getMovieById(movieId);
    }
}
