package com.example.movie.repository;

import com.example.movie.domain.Showtime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShowtimeRepository extends MongoRepository<Showtime, UUID> {
    List<Showtime> findByMovieId(UUID movieId);
    List<Showtime> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Showtime> findByScreenId(Integer screenId);
}
