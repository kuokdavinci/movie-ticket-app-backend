package com.example.MovieTicket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MovieTicketApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieTicketApplication.class, args);
	}

}
