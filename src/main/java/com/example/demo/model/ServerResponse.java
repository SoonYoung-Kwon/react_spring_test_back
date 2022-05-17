package com.example.demo.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter // Getter 필수
public class ServerResponse {
    private String accessToken;
    private String refreshToken;
    private String msg;
}
