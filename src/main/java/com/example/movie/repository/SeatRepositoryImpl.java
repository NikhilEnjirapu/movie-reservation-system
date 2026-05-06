package com.example.movie.repository;

import com.example.movie.domain.Seat;
import com.example.movie.domain.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements SeatRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Seat> reserveAvailableSeats(UUID showtimeId, List<UUID> seatIds) {
        LocalDateTime now = LocalDateTime.now();
        List<Seat> reservedSeats = new ArrayList<>();

        for (UUID seatId : seatIds) {
            Query query = Query.query(Criteria.where("_id").is(seatId)
                    .and("showtimeId").is(showtimeId)
                    .and("status").is(SeatStatus.AVAILABLE));
            Update update = new Update()
                    .set("status", SeatStatus.RESERVED)
                    .set("updatedAt", now)
                    .inc("version", 1);

            Seat reservedSeat = mongoTemplate.findAndModify(
                    query,
                    update,
                    FindAndModifyOptions.options().returnNew(true),
                    Seat.class);

            if (reservedSeat == null) {
                updateStatusByIds(reservedSeats.stream().map(Seat::getId).toList(), SeatStatus.AVAILABLE);
                return List.of();
            }

            reservedSeats.add(reservedSeat);
        }

        return reservedSeats;
    }

    @Override
    public void updateStatusByIds(List<UUID> seatIds, SeatStatus status) {
        if (seatIds == null || seatIds.isEmpty()) {
            return;
        }

        Query query = Query.query(Criteria.where("_id").in(seatIds));
        Update update = new Update()
                .set("status", status)
                .set("updatedAt", LocalDateTime.now())
                .inc("version", 1);
        mongoTemplate.updateMulti(query, update, Seat.class);
    }
}
