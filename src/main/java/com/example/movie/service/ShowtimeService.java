package com.example.movie.service;

import com.example.movie.domain.Movie;
import com.example.movie.domain.Seat;
import com.example.movie.domain.SeatStatus;
import com.example.movie.domain.Showtime;
import com.example.movie.dto.ShowtimeDTO;
import com.example.movie.exception.ResourceNotFoundException;
import com.example.movie.repository.MovieRepository;
import com.example.movie.repository.ReservationRepository;
import com.example.movie.repository.SeatRepository;
import com.example.movie.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    private final ReservationRepository reservationRepository;

    public ShowtimeDTO create(ShowtimeDTO dto) {
        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        if (hasOverlappingShowtime(dto.getScreenId(), dto.getStartTime(), dto.getEndTime(), null)) {
            throw new IllegalArgumentException("Overlapping showtime exists for this screen.");
        }

        Showtime showtime = Showtime.builder()
                .id(UUID.randomUUID())
                .movieId(movie.getId())
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
                    .id(UUID.randomUUID())
                    .showtimeId(saved.getId())
                    .seatNumber(String.valueOf(row) + num)
                    .status(SeatStatus.AVAILABLE)
                    .updatedAt(java.time.LocalDateTime.now())
                    .build());
        }
        seatRepository.saveAll(seats);

        return mapToDTO(saved);
    }

    public List<ShowtimeDTO> findAll() {
        return showtimeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ShowtimeDTO findById(UUID id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));
        return mapToDTO(showtime);
    }

    public List<ShowtimeDTO> findByMovieId(UUID movieId) {
        return showtimeRepository.findByMovieId(movieId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ShowtimeDTO update(UUID id, ShowtimeDTO dto) {
        Showtime existing = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));

        if (hasOverlappingShowtime(dto.getScreenId(), dto.getStartTime(), dto.getEndTime(), id)) {
            throw new IllegalArgumentException("Overlapping showtime exists for this screen.");
        }

        existing.setScreenId(dto.getScreenId());
        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());
        // Note: Total seats update would require complex seat reconciliation, skipping for simplicity or throwing if mismatched
        
        Showtime updated = showtimeRepository.save(existing);
        return mapToDTO(updated);
    }

    public void delete(UUID id) {
        if (!showtimeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Showtime not found");
        }
        reservationRepository.findByShowtimeId(id).forEach(reservationRepository::delete);
        seatRepository.deleteByShowtimeId(id);
        showtimeRepository.deleteById(id);
    }

    private boolean hasOverlappingShowtime(Integer screenId, java.time.LocalDateTime startTime,
                                           java.time.LocalDateTime endTime, UUID excludedShowtimeId) {
        return showtimeRepository.findByScreenId(screenId).stream()
                .filter(existing -> excludedShowtimeId == null || !existing.getId().equals(excludedShowtimeId))
                .anyMatch(existing -> existing.getStartTime().isBefore(endTime) && existing.getEndTime().isAfter(startTime));
    }

    private ShowtimeDTO mapToDTO(Showtime showtime) {
        Movie movie = movieRepository.findById(showtime.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        return ShowtimeDTO.builder()
                .id(showtime.getId())
                .movieId(showtime.getMovieId())
                .movieTitle(movie.getTitle())
                .screenId(showtime.getScreenId())
                .startTime(showtime.getStartTime())
                .endTime(showtime.getEndTime())
                .totalSeats(showtime.getTotalSeats())
                .build();
    }
}
