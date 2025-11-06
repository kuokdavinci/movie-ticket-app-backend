package com.example.MovieTicket.Controllers;

import com.example.MovieTicket.Models.Movie;
import com.example.MovieTicket.Services.JWTService;
import com.example.MovieTicket.Services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
@AutoConfigureMockMvc(addFilters = false)  // Disables security filters including JWT
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean  // New annotation replacing @MockBean
    private MovieService movieService;

    @MockitoBean  // Mock JWT service if needed by controller
    private JWTService jwtService;

    private Movie movie1;
    private Movie movie2;
    private List<Movie> movieList;

    @BeforeEach
    void setUp() {
        movie1 = new Movie();
        movie1.setMovie_id(1);
        movie1.setName("Tom & Jerry");

        movie2 = new Movie();
        movie2.setMovie_id(2);
        movie2.setName("Avengers");

        movieList = Arrays.asList(movie1, movie2);
    }

    @Test
    void testGetAllMovies() throws Exception {
        when(movieService.getAllMovies()).thenReturn(movieList);

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Tom & Jerry"))
                .andExpect(jsonPath("$[1].name").value("Avengers"));

        verify(movieService).getAllMovies();
    }

    @Test
    void testGetMovieById_Found() throws Exception {
        when(movieService.getMovieById(1)).thenReturn(movie1);

        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tom & Jerry"));

        verify(movieService).getMovieById(1);
    }

    @Test
    void testGetMovieById_NotFound() throws Exception {
        when(movieService.getMovieById(99)).thenReturn(null);

        mockMvc.perform(get("/api/movies/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Movie not found!"));

        verify(movieService).getMovieById(99);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddMovie() throws Exception {
        when(movieService.addMovie(any(Movie.class))).thenReturn(movie1);

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Tom & Jerry\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Tom & Jerry"));

        verify(movieService).addMovie(any(Movie.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateMovie() throws Exception {
        when(movieService.updateMovie(any(Movie.class))).thenReturn(movie1);

        mockMvc.perform(put("/api/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Tom & Jerry\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tom & Jerry"));

        verify(movieService).updateMovie(any(Movie.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteMovie_Found() throws Exception {
        when(movieService.getMovieById(1)).thenReturn(movie1);
        doNothing().when(movieService).deleteMovie(1);

        mockMvc.perform(delete("/api/movies/1"))
                .andExpect(status().isNoContent());

        verify(movieService).deleteMovie(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteMovie_NotFound() throws Exception {
        when(movieService.getMovieById(99)).thenReturn(null);

        mockMvc.perform(delete("/api/movies/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Movie not found!"));

        verify(movieService).getMovieById(99);
    }

    @Test
    void testSearchMovies() throws Exception {
        when(movieService.searchMovieByNameAndGenre("Tom"))
                .thenReturn(List.of(movie1));

        mockMvc.perform(get("/api/movies/search")
                        .param("keyword", "Tom"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Tom & Jerry"));

        verify(movieService).searchMovieByNameAndGenre(eq("Tom"));
    }
}