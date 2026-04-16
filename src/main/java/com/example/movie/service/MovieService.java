package com.example.movie.service;

import com.example.movie.domain.Movie;
import com.example.movie.dto.MovieDTO;
import com.example.movie.exception.ResourceNotFoundException;
import com.example.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    @Transactional
    public MovieDTO create(MovieDTO dto) {
        Movie movie = Movie.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .genre(dto.getGenre())
                .posterUrl(dto.getPosterUrl())
                .build();
        Movie saved = movieRepository.save(movie);
        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<MovieDTO> findAll() {
        return movieRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MovieDTO findById(UUID id) {
        return movieRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<MovieDTO> search(String genre, String searchKeyword) {
        return movieRepository.searchMovies(genre, searchKeyword).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MovieDTO update(UUID id, MovieDTO dto) {
        Movie existing = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setGenre(dto.getGenre());
        existing.setPosterUrl(dto.getPosterUrl());

        Movie updated = movieRepository.save(existing);
        return mapToDTO(updated);
    }

    @Transactional
    public void delete(UUID id) {
        if (!movieRepository.existsById(id)) {
            throw new ResourceNotFoundException("Movie not found with id: " + id);
        }
        movieRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<MovieDTO> getRecommendations(UUID userId) {
        // Mock recommendation logic
        List<Movie> allMovies = movieRepository.findAll();
        if(allMovies.size() <= 2) {
            return allMovies.stream().map(this::mapToDTO).collect(Collectors.toList());
        }
        return allMovies.subList(allMovies.size() - 2, allMovies.size()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private MovieDTO mapToDTO(Movie movie) {
        return MovieDTO.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .genre(movie.getGenre())
                .posterUrl(movie.getPosterUrl())
                .build();
    }
}
