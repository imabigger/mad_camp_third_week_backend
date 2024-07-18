package com.momentum.momentum.model.gptModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ChatGptRequest implements Serializable {
    private String model;
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    private Double temperature;
    private Boolean stream;
    private List<ChatGptRequestMessage> messages;
    private List<Map<String, Object>> tools; // function들의 리스트

    @Builder
    public ChatGptRequest(String model, Integer maxTokens, Double temperature,
                          Boolean stream, List<ChatGptRequestMessage> messages, List<Map<String, Object>> tools) {
        this.model = model;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
        this.stream = stream;
        this.messages = messages;
        this.tools = tools;
    }
}
