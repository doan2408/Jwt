package com.example.jwt.Services;

import com.example.jwt.Enity.RefreshToken;
import com.example.jwt.Enity.User;
import com.example.jwt.Repository.RefreshTokenRepository;
import com.example.jwt.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenDurationSec;

    private RefreshTokenRepository refreshTokenRepository;

    private UserRepository userRepository;

    public RefreshToken createRefreshToken(Integer userId) {
        RefreshToken refreshToken = new RefreshToken();

        User user = userRepository.findById(userId).orElse(null);

        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusSeconds(refreshTokenDurationSec));

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if(token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired. Please login again.");
        }
        return token;
    }

    @Transactional
    public void deleteByUserId(Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        refreshTokenRepository.deleteByUser(user);
    }

}
