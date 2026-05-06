package com.example.movie.service;

import com.example.movie.domain.Movie;
import com.example.movie.dto.MovieDTO;
import com.example.movie.exception.ResourceNotFoundException;
import com.example.movie.repository.MovieRepository;
import com.example.movie.repository.ReservationRepository;
import com.example.movie.repository.SeatRepository;
import com.example.movie.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    public MovieDTO create(MovieDTO dto) {
        Movie movie = Movie.builder()
                .id(UUID.randomUUID())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .genre(dto.getGenre())
                .posterUrl(dto.getPosterUrl())
                .build();
        Movie saved = movieRepository.save(movie);
        return mapToDTO(saved);
    }

    public List<MovieDTO> findAll() {
        return movieRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public MovieDTO findById(UUID id) {
        return movieRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
    }

    public List<MovieDTO> search(String genre, String searchKeyword) {
        List<Movie> movies;
        if (genre != null && !genre.isBlank() && searchKeyword != null && !searchKeyword.isBlank()) {
            movies = movieRepository.findByGenreIgnoreCaseAndTitleContainingIgnoreCase(genre, searchKeyword);
        } else if (genre != null && !genre.isBlank()) {
            movies = movieRepository.findByGenreIgnoreCase(genre);
        } else if (searchKeyword != null && !searchKeyword.isBlank()) {
            movies = movieRepository.findByTitleContainingIgnoreCase(searchKeyword);
        } else {
            movies = movieRepository.findAll();
        }

        return movies.stream()
                .filter(movie -> genre == null || genre.isBlank() || genre.equalsIgnoreCase(movie.getGenre()))
                .filter(movie -> searchKeyword == null || searchKeyword.isBlank()
                        || movie.getTitle().toLowerCase(Locale.ROOT).contains(searchKeyword.toLowerCase(Locale.ROOT)))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

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

    public void delete(UUID id) {
        if (!movieRepository.existsById(id)) {
            throw new ResourceNotFoundException("Movie not found with id: " + id);
        }
        showtimeRepository.findByMovieId(id).forEach(showtime -> {
            reservationRepository.findByShowtimeId(showtime.getId()).forEach(reservationRepository::delete);
            seatRepository.deleteByShowtimeId(showtime.getId());
            showtimeRepository.delete(showtime);
        });
        movieRepository.deleteById(id);
    }

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
