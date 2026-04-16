package com.example.movie.service;

import com.example.movie.domain.Reservation;
import com.example.movie.domain.ReservationStatus;
import com.example.movie.domain.Seat;
import com.example.movie.domain.SeatStatus;
import com.example.movie.dto.PaymentRequestDTO;
import com.example.movie.dto.PaymentResponseDTO;
import com.example.movie.exception.ResourceNotFoundException;
import com.example.movie.repository.ReservationRepository;
import com.example.movie.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.PENDING_PAYMENT) {
            return PaymentResponseDTO.builder()
                    .success(false)
                    .message("Reservation is not in a state that allows payment.")
                    .build();
        }

        // Simulated external payment gateway processing
        // In a real scenario, this would call Stripe/PayPal API
        boolean paymentSuccessful = simulatePayment(request.getCardData());

        if (paymentSuccessful) {
            reservation.setStatus(ReservationStatus.UPCOMING);
            
            // Finalize seats status
            reservation.getReservationSeats().forEach(rs -> {
                Seat seat = rs.getSeat();
                seat.setStatus(SeatStatus.BOOKED);
                seatRepository.save(seat);
            });
            
            reservationRepository.save(reservation);
            
            return PaymentResponseDTO.builder()
                    .success(true)
                    .message("Payment successful. Your reservation is confirmed.")
                    .build();
        } else {
            return PaymentResponseDTO.builder()
                    .success(false)
                    .message("Payment failed. Please check your card details.")
                    .build();
        }
    }

    private boolean simulatePayment(String cardData) {
        // Simple simulation: fail if card data contains "FAIL"
        return cardData == null || !cardData.toUpperCase().contains("FAIL");
    }
}
