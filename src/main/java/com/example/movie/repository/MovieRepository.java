package com.example.movie.repository;

import com.example.movie.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {
    
    @Query("SELECT m FROM Movie m WHERE " +
           "(:genre IS NULL OR m.genre = :genre) AND " +
           "(:search IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Movie> searchMovies(@Param("genre") String genre, @Param("search") String search);
}
