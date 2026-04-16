package com.example.movie.dto;

import com.example.movie.domain.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatDTO {
    private UUID id;
    private String seatNumber;
    private SeatStatus status;
}
