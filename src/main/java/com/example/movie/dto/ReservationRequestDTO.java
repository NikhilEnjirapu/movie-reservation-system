package com.example.movie.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ReservationRequestDTO {
    @NotNull(message = "Showtime ID cannot be null")
    private UUID showtimeId;

    @NotEmpty(message = "At least one seat must be selected")
    private List<UUID> seatIds;
}
