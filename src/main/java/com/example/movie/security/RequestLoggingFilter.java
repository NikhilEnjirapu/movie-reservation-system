package com.example.movie.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpServletRequest) {
            String method = httpServletRequest.getMethod();
            String uri = httpServletRequest.getRequestURI();
            System.out.println("DEBUG - Incoming Request: [" + method + "] " + uri);
        }
        chain.doFilter(request, response);
    }
}
