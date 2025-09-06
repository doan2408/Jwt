package com.example.jwt.Repository;

import com.example.jwt.Enity.RefreshToken;
import com.example.jwt.Enity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);

    @Transactional
    void deleteByUser(User user);
}
