package com.example.movie.dto;

import com.example.movie.domain.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDTO {
    private UUID id;
    private UUID userId;
    private UUID showtimeId;
    private String movieTitle;
    private LocalDateTime startTime;
    private BigDecimal totalPrice;
    private ReservationStatus status;
    private List<String> seatNumbers;
    private LocalDateTime createdAt;
}
