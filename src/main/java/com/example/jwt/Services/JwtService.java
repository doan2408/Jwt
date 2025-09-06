package com.example.jwt.Services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    private final String SECRET = "jwt1234567890jwt1234567890jwt1234567890";
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    public String generateToken(UserDetails userDetails) {
            return Jwts.builder() // start building the JWT
                    .setSubject(userDetails.getUsername()) // add username as the subject (gắn username vào payload)
                    .setIssuedAt(new Date()) // add issued date (now)
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // add expiration date
                    .signWith(SignatureAlgorithm.HS256, SECRET) // Sign with HS256 algorithm and the secret key
                    .compact(); // build and return as a string
    }

    public String extractUsername(String token) {
        return Jwts.parser() // Initialize a parser for the token
                .setSigningKey(SECRET) // Set the secret key for signature validation
                .parseClaimsJws(token) // Parse token into claims (payload)
                .getBody() // get the token's body (payload)
                .getSubject(); // Retrieve the subject (username we stored earlier)
    }

    // validate if the token is valid for the given user
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = this.extractUsername(token); // Extract username from the token
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // check if the token has expired
    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser() // Initialize parser
                .setSigningKey(SECRET) // set the secret key
                .parseClaimsJws(token) // parse token -> claims
                .getBody() // get payload
                .getExpiration(); // get expiration date
        return expiration.before(new Date()); // returns true if expiration < now
    }
}
