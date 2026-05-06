package com.example.movie.service;

import com.example.movie.domain.Seat;
import com.example.movie.domain.SeatStatus;
import com.example.movie.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatExpirationService {

    private final SeatRepository seatRepository;

    @Scheduled(fixedRate = 60000) // Runs every minute
    public void releaseExpiredSeats() {
        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(5);

        List<Seat> expiredSeats = seatRepository.findByStatusAndUpdatedAtBefore(SeatStatus.RESERVED, expiryTime);

        if (!expiredSeats.isEmpty()) {
            seatRepository.updateStatusByIds(expiredSeats.stream().map(Seat::getId).toList(), SeatStatus.AVAILABLE);
            System.out.println("Released " + expiredSeats.size() + " expired reserved seats.");
        }
    }
}
