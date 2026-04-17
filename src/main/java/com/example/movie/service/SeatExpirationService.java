package com.example.movie.service;

import com.example.movie.domain.Seat;
import com.example.movie.domain.SeatStatus;
import com.example.movie.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatExpirationService {

    private final SeatRepository seatRepository;

    @Scheduled(fixedRate = 60000) // Runs every minute
    @Transactional
    public void releaseExpiredSeats() {
        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(5);
        
        // Find seats that are RESERVED and haven't been updated in 5 minutes
        List<Seat> expiredSeats = seatRepository.findExpiredReservedSeats(SeatStatus.RESERVED, expiryTime);
        
        if (!expiredSeats.isEmpty()) {
            expiredSeats.forEach(seat -> seat.setStatus(SeatStatus.AVAILABLE));
            seatRepository.saveAll(expiredSeats);
            System.out.println("Released " + expiredSeats.size() + " expired reserved seats.");
        }
    }
}
