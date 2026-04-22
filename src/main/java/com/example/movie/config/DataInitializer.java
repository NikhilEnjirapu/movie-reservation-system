package com.example.movie.config;

import com.example.movie.domain.*;
import com.example.movie.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedAdmin();
        if (movieRepository.count() == 0) {
            seedMoviesAndShowtimes();
        }
    }

    private void seedAdmin() {
        String adminEmail = "enjirapunikhil@gmail.com";
        seedOneAdmin(adminEmail, "Admin", "12345");
        
        String secondaryAdmin = "admin@gmail.com";
        seedOneAdmin(secondaryAdmin, "admin", "12345");
    }

    private void seedOneAdmin(String email, String name, String password) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User admin = User.builder()
                    .name(name)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("Admin user created: " + email);
        }
    }

    private void seedMoviesAndShowtimes() {
        Movie m1 = Movie.builder()
                .title("Void Explorer")
                .description("A perilous journey to a distant black hole reveals profound secrets about the fabric of time.")
                .genre("Sci-Fi")
                .posterUrl("/scifi.png")
                .build();
        
        Movie m2 = Movie.builder()
                .title("Neon Agent")
                .description("An elite operative unravels a global conspiracy amidst the rain-soaked neon streets of the hyper-city.")
                .genre("Action")
                .posterUrl("/action.png")
                .build();

        movieRepository.save(m1);
        movieRepository.save(m2);

        // Seed some showtimes for today
        LocalDateTime now = LocalDateTime.now();
        Showtime st1 = Showtime.builder()
                .movie(m1)
                .screenId(1)
                .startTime(now.plusHours(2))
                .endTime(now.plusHours(4).plusMinutes(30))
                .totalSeats(32)
                .build();
        
        Showtime st2 = Showtime.builder()
                .movie(m2)
                .screenId(2)
                .startTime(now.plusHours(3))
                .endTime(now.plusHours(5))
                .totalSeats(32)
                .build();

        showtimeRepository.save(st1);
        showtimeRepository.save(st2);

        seedSeats(st1);
        seedSeats(st2);

        System.out.println("Sample movies and showtimes seeded.");
    }

    private void seedSeats(Showtime st) {
        int seatsPerRow = 8;
        java.util.List<Seat> seatsToSave = new java.util.ArrayList<>();
        for (int i = 0; i < st.getTotalSeats(); i++) {
            char row = (char) ('A' + (i / seatsPerRow));
            int num = (i % seatsPerRow) + 1;
            seatsToSave.add(Seat.builder()
                    .showtime(st)
                    .seatNumber(String.valueOf(row) + num)
                    .status(SeatStatus.AVAILABLE)
                    .build());
        }
        seatRepository.saveAll(seatsToSave);
    }
}
