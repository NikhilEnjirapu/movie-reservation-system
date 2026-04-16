package com.example.movie.repository;

import com.example.movie.domain.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, UUID> {
    List<Showtime> findByMovieId(UUID movieId);
    List<Showtime> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(s) > 0 FROM Showtime s WHERE s.screenId = :screenId " +
           "AND ((s.startTime < :endTime AND s.endTime > :startTime))")
    boolean existsOverlappingShowtime(@Param("screenId") Integer screenId, 
                                      @Param("startTime") LocalDateTime startTime, 
                                      @Param("endTime") LocalDateTime endTime);
}
