package com.example.movie.repository;

import com.example.movie.domain.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeatRepository extends JpaRepository<Seat, UUID> {
    
    // Pessimistic Write Lock for strong consistency during booking
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id IN :seatIds AND s.status = 'AVAILABLE'")
    List<Seat> findAvailableSeatsForUpdate(@Param("seatIds") List<UUID> seatIds);
    
    List<Seat> findByShowtimeId(UUID showtimeId);

    @Query("SELECT s FROM Seat s WHERE s.status = 'RESERVED' AND s.updatedAt < :expiryTime")
    List<Seat> findExpiredReservedSeats(@Param("expiryTime") java.time.LocalDateTime expiryTime);
}
