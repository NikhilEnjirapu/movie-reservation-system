package com.example.movie.service;

import com.example.movie.domain.*;
import com.example.movie.dto.ReservationRequestDTO;
import com.example.movie.dto.ReservationResponseDTO;
import com.example.movie.exception.SeatNotAvailableException;
import com.example.movie.repository.MovieRepository;
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
    @Mock
    private MovieRepository movieRepository;

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
                .movieId(UUID.randomUUID())
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
        when(seatRepository.reserveAvailableSeats(showtimeId, request.getSeatIds())).thenReturn(List.of(mockSeat));
        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).name("Test").email("test@test.com").password("pw").role(Role.USER).build()));
        when(movieRepository.findById(showtime.getMovieId())).thenReturn(Optional.of(Movie.builder().id(showtime.getMovieId()).title("Test Movie").build()));

        Reservation mockSavedReservation = Reservation.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .showtimeId(showtimeId)
                .status(ReservationStatus.PENDING_PAYMENT)
                .reservationSeats(List.of(ReservationSeat.builder().seatId(mockSeat.getId()).build()))
                .build();

        when(reservationRepository.save(any(Reservation.class))).thenReturn(mockSavedReservation);
        when(seatRepository.findAllById(List.of(mockSeat.getId()))).thenReturn(List.of(mockSeat));

        // Act
        ReservationResponseDTO response = reservationService.reserveSeats(userId, request);

        // Assert
        assertNotNull(response);
        assertEquals(ReservationStatus.PENDING_PAYMENT, response.getStatus());
        assertEquals("Test Movie", response.getMovieTitle());
        verify(seatRepository, times(1)).reserveAvailableSeats(showtimeId, request.getSeatIds());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void reserveSeats_FailsWhenSeatUnavailable() {
        // Arrange
        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setShowtimeId(showtimeId);
        request.setSeatIds(List.of(mockSeat.getId()));

        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
        when(seatRepository.reserveAvailableSeats(showtimeId, request.getSeatIds())).thenReturn(List.of());

        // Act & Assert
        assertThrows(SeatNotAvailableException.class, () -> reservationService.reserveSeats(userId, request));

        verify(seatRepository, never()).updateStatusByIds(anyList(), any());
        verify(reservationRepository, never()).save(any());
    }
}
