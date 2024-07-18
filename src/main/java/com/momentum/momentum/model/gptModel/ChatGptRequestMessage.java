package com.momentum.momentum.model.gptModel;

import lombok.Builder;
import lombok.Data;

@Data
public class ChatGptRequestMessage {
    private String role;
    private String content;

    @Builder
    public ChatGptRequestMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
