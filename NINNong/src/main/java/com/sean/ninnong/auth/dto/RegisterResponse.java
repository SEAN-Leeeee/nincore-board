package com.sean.ninnong.auth.dto;

import java.time.LocalDateTime;

public record RegisterResponse(String email, String message, LocalDateTime processedAt) {
    public static RegisterResponse from(String email) {
        return new RegisterResponse(
                email,
                "계정이 생성되었습니다.",
                LocalDateTime.now()
        );
    }
}
