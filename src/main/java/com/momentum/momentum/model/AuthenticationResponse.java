package com.momentum.momentum.model;

import lombok.Data;

@Data
public class AuthenticationResponse {
    private final String accessToken;

    public AuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }

}