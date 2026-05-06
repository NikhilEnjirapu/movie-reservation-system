package com.example.movie.security;

import com.example.movie.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            if (tokenProvider.validateToken(token)) {
                String userId = resolveCurrentUserId(token);
                String role = tokenProvider.getRoleFromToken(token);

                if (userId != null) {
                    System.out.println("JwtAuthFilter: Authenticating User ID: " + userId + " with Role: " + role);

                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userId, null, Collections.singletonList(authority));

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("JwtAuthFilter: SecurityContext updated for: " + userId);
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String resolveCurrentUserId(String token) {
        String tokenUserId = tokenProvider.getUserIdFromToken(token);
        if (userRepository.existsById(java.util.UUID.fromString(tokenUserId))) {
            return tokenUserId;
        }

        String email = tokenProvider.getEmailFromToken(token);
        return userRepository.findByEmail(email)
                .map(user -> {
                    System.out.println("JwtAuthFilter: Remapped stale token user ID " + tokenUserId + " to current user " + user.getId());
                    return user.getId().toString();
                })
                .orElse(null);
    }
}
