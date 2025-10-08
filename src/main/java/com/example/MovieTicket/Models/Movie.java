package com.example.MovieTicket.Models;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name="movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int movie_id;
    private String name;
    private String description;
    private int duration;
    private String genre;
    private String imageURL;

}
