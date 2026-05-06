package com.example.movie.repository;

import com.example.movie.domain.Seat;
import com.example.movie.domain.SeatStatus;

import java.util.List;
import java.util.UUID;

public interface SeatRepositoryCustom {
    List<Seat> reserveAvailableSeats(UUID showtimeId, List<UUID> seatIds);
    void updateStatusByIds(List<UUID> seatIds, SeatStatus status);
}
