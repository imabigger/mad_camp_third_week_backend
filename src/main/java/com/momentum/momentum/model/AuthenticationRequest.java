package com.momentum.momentum.model;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String userId;
    private String username;
    private String password;
    private String accessToken;
    private String refreshToken;
}