package com.example.movie.controller;

import com.example.movie.domain.Movie;
import com.example.movie.dto.MovieDTO;
import com.example.movie.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @Test
    void getAllMovies_Success() throws Exception {
        UUID movieId = UUID.randomUUID();
        MovieDTO movie = MovieDTO.builder()
                .id(movieId)
                .title("Test Movie")
                .build();

        when(movieService.findAll()).thenReturn(List.of(movie));

        mockMvc.perform(get("/api/v1/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Movie"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createMovie_ForbiddenForUser() throws Exception {
        mockMvc.perform(post("/api/v1/movies")
                .contentType("application/json")
                .content("{\"title\":\"Hacker Movie\"}"))
                .andExpect(status().isForbidden());
    }
}
