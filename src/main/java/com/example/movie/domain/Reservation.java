package com.example.movie.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    @Id
    private UUID id;
    private UUID userId;
    private UUID showtimeId;
    private BigDecimal totalPrice;
    private ReservationStatus status;
    private LocalDateTime createdAt;
    @Builder.Default
    private List<ReservationSeat> reservationSeats = new ArrayList<>();
}
