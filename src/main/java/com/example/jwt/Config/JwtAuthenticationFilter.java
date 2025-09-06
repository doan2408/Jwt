package com.example.jwt.Config;

import com.example.jwt.Services.JwtService;
import com.example.jwt.Services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // register this class as a spring bean, so it can be auto-injected
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // extends OncePerRequestFilter -> ensure the filter runs once per request

    public final JwtService jwtService;

    // inject custom UserDetailsService to load user information from database
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // Get "Authorization" header from the request
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extract the token (remove "Bearer")
            String token = authHeader.substring(7);

            // Extract Username from token using JwtService
            String username = jwtService.extractUsername(token);

            // if username exists and no authentication is set in SecurityContext
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // load user information from database (Spring security UserDetails)
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if(jwtService.isTokenValid(token, userDetails)) {
                    // Create Authentication object with user info and authorities
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Store (lưu) Authentication in SecurityContext so Spring recognizes (nhận diện) the user
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        // continue the request / response through the filter chain
        chain.doFilter(request, response);
    }
}
