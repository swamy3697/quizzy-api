package com.interger.quizzy.model.responses;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private Long userId;
    private String accessToken;
    private String refreshToken;
    private String message;

}
