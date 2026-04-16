package com.example.movie.controller;

import com.example.movie.dto.ReservationRequestDTO;
import com.example.movie.dto.ReservationResponseDTO;
import com.example.movie.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ReservationResponseDTO> createReservation(
            @Valid @RequestBody ReservationRequestDTO request, 
            Principal principal) {
        
        // principal.getName() now returns the User ID string from JWT
        UUID userId = UUID.fromString(principal.getName());
            
        ReservationResponseDTO response = reservationService.reserveSeats(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ReservationResponseDTO>> getMyReservations(Principal principal) {
        System.out.println("ReservationController: Fetching bookings for principal: " + principal.getName());
        try {
            UUID userId = UUID.fromString(principal.getName());
            return ResponseEntity.ok(reservationService.getUserReservations(userId));
        } catch (IllegalArgumentException e) {
            System.err.println("ReservationController Error: Principal name is not a valid UUID: " + principal.getName());
            throw e;
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> cancelReservation(@PathVariable UUID id, 
                                                  Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        reservationService.cancelReservation(id, userId);
        return ResponseEntity.noContent().build();
    }
}
