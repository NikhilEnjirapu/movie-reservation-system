package com.example.movie.service;

import com.example.movie.domain.Movie;
import com.example.movie.domain.Seat;
import com.example.movie.domain.SeatStatus;
import com.example.movie.domain.Showtime;
import com.example.movie.dto.ShowtimeDTO;
import com.example.movie.exception.ResourceNotFoundException;
import com.example.movie.repository.MovieRepository;
import com.example.movie.repository.SeatRepository;
import com.example.movie.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public ShowtimeDTO create(ShowtimeDTO dto) {
        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        if (showtimeRepository.existsOverlappingShowtime(dto.getScreenId(), dto.getStartTime(), dto.getEndTime())) {
            throw new IllegalArgumentException("Overlapping showtime exists for this screen.");
        }

        Showtime showtime = Showtime.builder()
                .movie(movie)
                .screenId(dto.getScreenId())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .totalSeats(dto.getTotalSeats())
                .build();

        Showtime saved = showtimeRepository.save(showtime);

        // Initialize seats for this showtime
        List<Seat> seats = new ArrayList<>();
        int seatsPerRow = 8;
        for (int i = 0; i < dto.getTotalSeats(); i++) {
            char row = (char) ('A' + (i / seatsPerRow));
            int num = (i % seatsPerRow) + 1;
            seats.add(Seat.builder()
                    .showtime(saved)
                    .seatNumber(String.valueOf(row) + num)
                    .status(SeatStatus.AVAILABLE)
                    .build());
        }
        seatRepository.saveAll(seats);

        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<ShowtimeDTO> findAll() {
        return showtimeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ShowtimeDTO findById(UUID id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));
        return mapToDTO(showtime);
    }

    @Transactional(readOnly = true)
    public List<ShowtimeDTO> findByMovieId(UUID movieId) {
        return showtimeRepository.findByMovieId(movieId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShowtimeDTO update(UUID id, ShowtimeDTO dto) {
        Showtime existing = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));

        existing.setScreenId(dto.getScreenId());
        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());
        // Note: Total seats update would require complex seat reconciliation, skipping for simplicity or throwing if mismatched
        
        Showtime updated = showtimeRepository.save(existing);
        return mapToDTO(updated);
    }

    @Transactional
    public void delete(UUID id) {
        if (!showtimeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Showtime not found");
        }
        showtimeRepository.deleteById(id);
    }

    private ShowtimeDTO mapToDTO(Showtime showtime) {
        return ShowtimeDTO.builder()
                .id(showtime.getId())
                .movieId(showtime.getMovie().getId())
                .movieTitle(showtime.getMovie().getTitle())
                .screenId(showtime.getScreenId())
                .startTime(showtime.getStartTime())
                .endTime(showtime.getEndTime())
                .totalSeats(showtime.getTotalSeats())
                .build();
    }
}
