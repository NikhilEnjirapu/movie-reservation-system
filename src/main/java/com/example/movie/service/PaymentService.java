package com.example.movie.service;

import com.example.movie.domain.Reservation;
import com.example.movie.domain.ReservationSeat;
import com.example.movie.domain.ReservationStatus;
import com.example.movie.domain.SeatStatus;
import com.example.movie.dto.PaymentRequestDTO;
import com.example.movie.dto.PaymentResponseDTO;
import com.example.movie.exception.ResourceNotFoundException;
import com.example.movie.repository.ReservationRepository;
import com.example.movie.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

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

            seatRepository.updateStatusByIds(
                    reservation.getReservationSeats().stream().map(ReservationSeat::getSeatId).toList(),
                    SeatStatus.BOOKED);
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
