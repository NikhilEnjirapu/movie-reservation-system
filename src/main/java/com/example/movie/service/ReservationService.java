package com.example.movie.service;

import com.example.movie.domain.*;
import com.example.movie.dto.ReservationRequestDTO;
import com.example.movie.dto.ReservationResponseDTO;
import com.example.movie.exception.ResourceNotFoundException;
import com.example.movie.exception.SeatNotAvailableException;
import com.example.movie.repository.MovieRepository;
import com.example.movie.repository.ReservationRepository;
import com.example.movie.repository.SeatRepository;
import com.example.movie.repository.ShowtimeRepository;
import com.example.movie.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public ReservationResponseDTO reserveSeats(UUID userId, ReservationRequestDTO request) {
        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));

        List<Seat> availableSeats = seatRepository.reserveAvailableSeats(request.getShowtimeId(), request.getSeatIds());

        if (availableSeats.size() != request.getSeatIds().size()) {
            throw new SeatNotAvailableException("One or more requested seats are already booked.");
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Reservation reservation = Reservation.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .showtimeId(showtime.getId())
                .status(ReservationStatus.PENDING_PAYMENT)
                .createdAt(LocalDateTime.now())
                .build();

        BigDecimal simulatedPricePerSeat = BigDecimal.valueOf(15.00);
        BigDecimal totalPrice = simulatedPricePerSeat.multiply(BigDecimal.valueOf(availableSeats.size()));
        reservation.setTotalPrice(totalPrice);

        List<ReservationSeat> resSeats = availableSeats.stream()
            .map(seat -> ReservationSeat.builder()
                    .id(UUID.randomUUID())
                    .seatId(seat.getId())
                    .build())
            .collect(Collectors.toList());
        reservation.setReservationSeats(resSeats);

        try {
            Reservation savedReservation = reservationRepository.save(reservation);
            return mapToDTO(savedReservation);
        } catch (RuntimeException ex) {
            seatRepository.updateStatusByIds(availableSeats.stream().map(Seat::getId).toList(), SeatStatus.AVAILABLE);
            throw ex;
        }
    }

    public List<ReservationResponseDTO> getUserReservations(UUID userId) {
        return reservationRepository.findByUserId(userId).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public List<ReservationResponseDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public void cancelReservation(UUID reservationId, UUID userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        if (!reservation.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized to cancel this reservation");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
             throw new IllegalArgumentException("Reservation is already cancelled");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        seatRepository.updateStatusByIds(
                reservation.getReservationSeats().stream().map(ReservationSeat::getSeatId).toList(),
                SeatStatus.AVAILABLE);
        reservationRepository.save(reservation);
    }

    private ReservationResponseDTO mapToDTO(Reservation res) {
        Showtime showtime = showtimeRepository.findById(res.getShowtimeId())
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));
        Movie movie = movieRepository.findById(showtime.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        List<UUID> seatIds = res.getReservationSeats().stream()
                .map(ReservationSeat::getSeatId)
                .toList();
        List<Seat> seats = new ArrayList<>();
        seatRepository.findAllById(seatIds).forEach(seats::add);
        Map<UUID, Seat> seatById = new HashMap<>();
        for (Seat seat : seats) {
            seatById.put(seat.getId(), seat);
        }

        return ReservationResponseDTO.builder()
                .id(res.getId())
                .userId(res.getUserId())
                .showtimeId(res.getShowtimeId())
                .movieTitle(movie.getTitle())
                .startTime(showtime.getStartTime())
                .totalPrice(res.getTotalPrice())
                .status(res.getStatus())
                .seatNumbers(res.getReservationSeats().stream()
                     .map(rs -> seatById.get(rs.getSeatId()))
                     .filter(java.util.Objects::nonNull)
                     .map(Seat::getSeatNumber)
                     .collect(Collectors.toList()))
                .createdAt(res.getCreatedAt())
                .build();
    }
}
