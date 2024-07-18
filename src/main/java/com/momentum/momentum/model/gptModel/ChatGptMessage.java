package com.momentum.momentum.model.gptModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class ChatGptMessage {
    private String role;
    private String content;
    @JsonProperty("function_call")
    private FunctionCall functionCall;
    @JsonProperty("tool_calls")
    private List<ToolCall> toolCalls;

    @Builder
    public ChatGptMessage(String role, String content, FunctionCall functionCall, List<ToolCall> toolCalls) {
        this.role = role;
        this.content = content;
        this.functionCall = functionCall;
        this.toolCalls = toolCalls;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class FunctionCall {
        private String name;
        private Map<String, Object> arguments;

        @Builder
        public FunctionCall(String name, Map<String, Object> arguments) {
            this.name = name;
            this.arguments = arguments;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ToolCall {
        private String id;
        private String type;
        private Function function;


        @Data
        public static class Function {
            private String name;
            private String arguments; // 결국 이게 응답!
        }
    }
}
