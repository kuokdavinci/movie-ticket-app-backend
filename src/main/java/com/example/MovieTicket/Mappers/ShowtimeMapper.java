package com.example.MovieTicket.Mappers;

import com.example.MovieTicket.DTOs.ShowtimeDTO;
import com.example.MovieTicket.Models.Showtime;
import org.springframework.stereotype.Service;

@Service
public class ShowtimeMapper {
    public ShowtimeDTO toShowtimeDTO(Showtime showtime){
        return new ShowtimeDTO(
                showtime.getStartTime(),
                showtime.getEndTime(),
                showtime.getMovie().getName(),
                showtime.getMovie().getImageURL());
    }
}
