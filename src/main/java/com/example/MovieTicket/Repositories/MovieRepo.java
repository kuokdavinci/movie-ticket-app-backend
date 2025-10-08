package com.example.MovieTicket.Repositories;

import com.example.MovieTicket.Models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepo extends JpaRepository<Movie,Integer> {
    @Query("SELECT m from Movie m WHERE "+
            "LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "+
            "LOWER(m.genre) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Movie> searchMovieByNameAndGenre(String keyword);
}
