package com.example.MovieTicket.DTOs;

import java.time.LocalTime;

public record ShowtimeDTO(LocalTime startTime,LocalTime endTime, String name, String imageUrl) {
}
