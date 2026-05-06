package com.example.movie.repository;

import com.example.movie.domain.Seat;
import com.example.movie.domain.SeatStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SeatRepository extends MongoRepository<Seat, UUID>, SeatRepositoryCustom {
    List<Seat> findByShowtimeId(UUID showtimeId);
    void deleteByShowtimeId(UUID showtimeId);
    List<Seat> findByStatusAndUpdatedAtBefore(SeatStatus status, LocalDateTime expiryTime);
}
