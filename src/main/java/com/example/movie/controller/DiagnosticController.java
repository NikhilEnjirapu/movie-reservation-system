package com.example.movie.controller;

import com.example.movie.domain.SeatStatus;
import com.example.movie.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/diagnostic")
@RequiredArgsConstructor
public class DiagnosticController {

    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> result = new LinkedHashMap<>();

        try {
            long showtimeCount = showtimeRepository.count();
            result.put("showtimes", showtimeCount);
        } catch (Exception e) {
            result.put("showtimes_error", e.getClass().getName() + ": " + e.getMessage());
        }

        try {
            long userCount = userRepository.count();
            result.put("users", userCount);
        } catch (Exception e) {
            result.put("users_error", e.getClass().getName() + ": " + e.getMessage());
        }

        try {
            long reservationCount = reservationRepository.count();
            result.put("reservations", reservationCount);
        } catch (Exception e) {
            result.put("reservations_error", e.getClass().getName() + ": " + e.getMessage());
        }

        try {
            var showtimes = showtimeRepository.findAll();
            if (!showtimes.isEmpty()) {
                var firstShowtime = showtimes.get(0);
                result.put("first_showtime_id", firstShowtime.getId());

                var seats = seatRepository.findByShowtimeId(firstShowtime.getId());
                result.put("seats_for_first_showtime", seats.size());

                if (!seats.isEmpty()) {
                    var firstSeat = seats.get(0);
                    result.put("first_seat_id", firstSeat.getId());
                    result.put("first_seat_status", firstSeat.getStatus());
                    long availableSeats = seats.stream()
                            .filter(seat -> seat.getStatus() == SeatStatus.AVAILABLE)
                            .count();
                    result.put("available_seat_count", availableSeats);
                }
            }
        } catch (Exception e) {
            result.put("diagnostic_error", e.getClass().getName() + ": " + e.getMessage());
            if (e.getCause() != null) {
                result.put("diagnostic_cause", e.getCause().getClass().getName() + ": " + e.getCause().getMessage());
            }
        }

        result.put("status", "diagnostic_complete");
        result.put("timestamp", new java.util.Date().toString());
        return ResponseEntity.ok(result);
    }
}
