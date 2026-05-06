package com.example.movie.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "seats")
@CompoundIndex(name = "showtime_seat_unique", def = "{'showtimeId': 1, 'seatNumber': 1}", unique = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {
    @Id
    private UUID id;
    private UUID showtimeId;
    private String seatNumber;
    private SeatStatus status;
    private LocalDateTime updatedAt;
    @Version
    private Long version;
}
