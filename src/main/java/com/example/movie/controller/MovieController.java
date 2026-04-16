package com.example.movie.controller;

import com.example.movie.dto.MovieDTO;
import com.example.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {
    
    private final MovieService movieService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDTO> addMovie(@RequestBody MovieDTO movieDTO) {
        System.out.println("MovieController: Adding new movie: " + movieDTO.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.create(movieDTO));
    }
    
    @GetMapping
    public ResponseEntity<List<MovieDTO>> getAllMovies(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String genre) {
        
        if (search == null && genre == null) {
            return ResponseEntity.ok(movieService.findAll());
        }
        
        return ResponseEntity.ok(movieService.search(genre, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable UUID id) {
        return ResponseEntity.ok(movieService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable UUID id, @RequestBody MovieDTO movieDTO) {
        System.out.println("MovieController: Updating movie ID: " + id);
        return ResponseEntity.ok(movieService.update(id, movieDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMovie(@PathVariable UUID id) {
        System.out.println("MovieController: Deleting movie ID: " + id);
        movieService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<MovieDTO>> getRecommendations(Principal principal) {
        // principal.getName() is the User ID string from JwtAuthenticationFilter
        UUID userId = principal != null ? UUID.fromString(principal.getName()) : null;
        return ResponseEntity.ok(movieService.getRecommendations(userId));
    }
}
