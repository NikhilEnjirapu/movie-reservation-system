package com.example.movie.controller;

import com.example.movie.dto.SeatDTO;
import com.example.movie.dto.ShowtimeDTO;
import com.example.movie.repository.SeatRepository;
import com.example.movie.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;
    private final SeatRepository seatRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShowtimeDTO> createShowtime(@RequestBody ShowtimeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(showtimeService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ShowtimeDTO>> getAllShowtimes(@RequestParam(required = false) UUID movieId) {
        if (movieId != null) {
            return ResponseEntity.ok(showtimeService.findByMovieId(movieId));
        }
        return ResponseEntity.ok(showtimeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowtimeDTO> getShowtimeById(@PathVariable UUID id) {
        return ResponseEntity.ok(showtimeService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShowtimeDTO> updateShowtime(@PathVariable UUID id, @RequestBody ShowtimeDTO dto) {
        return ResponseEntity.ok(showtimeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteShowtime(@PathVariable UUID id) {
        showtimeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<SeatDTO>> getSeatsForShowtime(@PathVariable UUID id) {
        return ResponseEntity.ok(seatRepository.findByShowtimeId(id).stream()
                .map(seat -> SeatDTO.builder()
                        .id(seat.getId())
                        .seatNumber(seat.getSeatNumber())
                        .status(seat.getStatus())
                        .build())
                .collect(Collectors.toList()));
    }
}
