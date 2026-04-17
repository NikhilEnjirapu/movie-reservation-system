package com.example.movie.service;

import com.example.movie.domain.*;
import com.example.movie.dto.ReservationRequestDTO;
import com.example.movie.dto.ReservationResponseDTO;
import com.example.movie.exception.ResourceNotFoundException;
import com.example.movie.exception.SeatNotAvailableException;
import com.example.movie.repository.ReservationRepository;
import com.example.movie.repository.SeatRepository;
import com.example.movie.repository.ShowtimeRepository;
import com.example.movie.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReservationResponseDTO reserveSeats(UUID userId, ReservationRequestDTO request) {
        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));

        // 1. Concurrency Handling: Pessimistic Lock on requested available seats
        List<Seat> availableSeats = seatRepository.findAvailableSeatsForUpdate(request.getSeatIds(), SeatStatus.AVAILABLE);

        if (availableSeats.size() != request.getSeatIds().size()) {
            throw new SeatNotAvailableException("One or more requested seats are already booked.");
        }

        // 2. Mark logic as RESERVED (pre-payment hold)
        for (Seat seat : availableSeats) {
            seat.setStatus(SeatStatus.RESERVED);
        }
        seatRepository.saveAll(availableSeats);

        // 3. Create Reservation
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Reservation reservation = Reservation.builder()
                .user(user)
                .showtime(showtime)
                .status(ReservationStatus.PENDING_PAYMENT)
                .build();
        
        // Mock price calculation
        BigDecimal simulatedPricePerSeat = BigDecimal.valueOf(15.00);
        BigDecimal totalPrice = simulatedPricePerSeat.multiply(BigDecimal.valueOf(availableSeats.size()));
        reservation.setTotalPrice(totalPrice);

        // Create mapping
        List<ReservationSeat> resSeats = availableSeats.stream()
            .map(seat -> ReservationSeat.builder().reservation(reservation).seat(seat).build())
            .collect(Collectors.toList());
        reservation.setReservationSeats(resSeats);

        Reservation savedReservation = reservationRepository.save(reservation);

        return mapToDTO(savedReservation);
    }
    
    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getUserReservations(UUID userId) {
        return reservationRepository.findByUserId(userId).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public void cancelReservation(UUID reservationId, UUID userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized to cancel this reservation");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
             throw new IllegalArgumentException("Reservation is already cancelled");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);

        // Free up the seats
        reservation.getReservationSeats().forEach(rs -> {
            Seat seat = rs.getSeat();
            seat.setStatus(SeatStatus.AVAILABLE);
            seatRepository.save(seat);
        });

        reservationRepository.save(reservation);
    }

    private ReservationResponseDTO mapToDTO(Reservation res) {
        return ReservationResponseDTO.builder()
                .id(res.getId())
                .userId(res.getUser().getId())
                .showtimeId(res.getShowtime().getId())
                .movieTitle(res.getShowtime().getMovie().getTitle())
                .startTime(res.getShowtime().getStartTime())
                .totalPrice(res.getTotalPrice())
                .status(res.getStatus())
                .seatNumbers(res.getReservationSeats().stream()
                     .map(rs -> rs.getSeat().getSeatNumber())
                     .collect(Collectors.toList()))
                .createdAt(res.getCreatedAt())
                .build();
    }
}
