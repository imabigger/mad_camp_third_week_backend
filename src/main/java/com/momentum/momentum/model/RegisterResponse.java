package com.momentum.momentum.model;

import com.momentum.momentum.entity.User;
import lombok.Data;

@Data
public class RegisterResponse {
    private User user;
    private String accessToken;

    public RegisterResponse(User user, String accessToken) {
        this.user = user;
        this.accessToken = accessToken;
    }
}
