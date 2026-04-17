package com.example.movie.service;

import com.example.movie.domain.*;
import com.example.movie.dto.ReservationRequestDTO;
import com.example.movie.dto.ReservationResponseDTO;
import com.example.movie.exception.SeatNotAvailableException;
import com.example.movie.repository.ReservationRepository;
import com.example.movie.repository.SeatRepository;
import com.example.movie.repository.ShowtimeRepository;
import com.example.movie.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private ShowtimeRepository showtimeRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationService reservationService;

    private UUID showtimeId;
    private UUID userId;
    private Showtime showtime;
    private Seat mockSeat;

    @BeforeEach
    void setUp() {
        showtimeId = UUID.randomUUID();
        userId = UUID.randomUUID();

        showtime = Showtime.builder()
                .id(showtimeId)
                .movie(Movie.builder().title("Test Movie").build())
                .startTime(java.time.LocalDateTime.now())
                .build();

        mockSeat = Seat.builder()
                .id(UUID.randomUUID())
                .seatNumber("A1")
                .status(SeatStatus.AVAILABLE)
                .build();
    }

    @Test
    void reserveSeats_Success() {
        // Arrange
        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setShowtimeId(showtimeId);
        request.setSeatIds(List.of(mockSeat.getId()));

        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
        when(seatRepository.findAvailableSeatsForUpdate(request.getSeatIds(), SeatStatus.AVAILABLE)).thenReturn(List.of(mockSeat));
        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).name("Test").email("test@test.com").password("pw").role(Role.USER).build()));

        Reservation mockSavedReservation = Reservation.builder()
                .id(UUID.randomUUID())
                .user(User.builder().id(userId).build())
                .showtime(showtime)
                .status(ReservationStatus.PENDING_PAYMENT)
                .reservationSeats(List.of(ReservationSeat.builder().seat(mockSeat).build()))
                .build();

        when(reservationRepository.save(any(Reservation.class))).thenReturn(mockSavedReservation);

        // Act
        ReservationResponseDTO response = reservationService.reserveSeats(userId, request);

        // Assert
        assertNotNull(response);
        assertEquals(ReservationStatus.PENDING_PAYMENT, response.getStatus());
        assertEquals("Test Movie", response.getMovieTitle());
        verify(seatRepository, times(1)).saveAll(anyList());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void reserveSeats_FailsWhenSeatUnavailable() {
        // Arrange
        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setShowtimeId(showtimeId);
        request.setSeatIds(List.of(mockSeat.getId()));

        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
        // Return empty list to simulate seat not being AVAILABLE or missing
        when(seatRepository.findAvailableSeatsForUpdate(request.getSeatIds(), SeatStatus.AVAILABLE)).thenReturn(List.of());

        // Act & Assert
        assertThrows(SeatNotAvailableException.class, () -> reservationService.reserveSeats(userId, request));
        
        verify(seatRepository, never()).saveAll(anyList());
        verify(reservationRepository, never()).save(any());
    }
}
