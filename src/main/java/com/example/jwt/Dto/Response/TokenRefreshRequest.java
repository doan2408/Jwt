package com.example.jwt.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenRefreshRequest {
    private String refreshToken;
}
