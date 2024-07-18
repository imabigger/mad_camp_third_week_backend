package com.momentum.momentum.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.momentum.momentum.config.ChatGptConfig;
import com.momentum.momentum.entity.GameSettings;
import com.momentum.momentum.entity.SRoom;
import com.momentum.momentum.entity.StoryContent;
import com.momentum.momentum.entity.UserStats;
import com.momentum.momentum.model.GenerateStoryRequest;
import com.momentum.momentum.model.gptModel.ChatGptMessage;
import com.momentum.momentum.model.gptModel.ChatGptRequest;
import com.momentum.momentum.model.gptModel.ChatGptRequestMessage;
import com.momentum.momentum.model.gptModel.ChatGptResponse;
import com.momentum.momentum.repository.RoomRepository;
import com.momentum.momentum.repository.StoryContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatGptService {

    private final RestTemplate restTemplate;

    private final StoryContentRepository storyContentRepository;

    private final RoomRepository roomRepository;

    private final String apiKey = EnvUtil.getEnv("API_KEY_CHAT_GPT");

    public HttpEntity<ChatGptRequest> buildHttpEntity(ChatGptRequest chatGptRequest){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
        return new HttpEntity<>(chatGptRequest, httpHeaders);
    }

    public ChatGptResponse getResponse(HttpEntity<ChatGptRequest> chatGptRequestHttpEntity){
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(60000); // 1분
        requestFactory.setReadTimeout(60000); // 1분
        restTemplate.setRequestFactory(requestFactory);

        ResponseEntity<ChatGptResponse> responseEntity = restTemplate.postForEntity(
                ChatGptConfig.CHAT_URL,
                chatGptRequestHttpEntity,
                ChatGptResponse.class
        );

        return responseEntity.getBody();
    }

    public ChatGptResponse askToGptGenerateNextStory(GenerateStoryRequest generateStoryRequest, String roomId, StoryContent storyContent) {
        List<ChatGptRequestMessage> messages = new ArrayList<>();
        SRoom sRoom = roomRepository.findById(roomId).orElse(null);

        UserStats userStats = sRoom.getUserStats();
        GameSettings gameSettings = sRoom.getSettings();

        // 모든 유저 스탯 필드 가져오기
        StringJoiner userStatsJoiner = new StringJoiner("\n");
        for (Field field : userStats.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                userStatsJoiner.add(field.getName() + ": " + field.get(userStats));
            } catch (IllegalAccessException e) {
                System.out.println(e.getMessage());
            }
        }


        String systemMessageContent = String.format(
                "(한국어) RPG Story Manager :: <<Strong affect>> 주제, 키워드, 현재까지의 스토리, 스탯\n" +
                        "<<Weak affect>> 아이템, 점수\n" +
                        "- 주어진 주제와 키워드에 맞는 이야기를 맥락에 맞게 생성해야 합니다.\n" +
                        "- 이야기 생성 후 사용자의 선택에 따라 다음 이야기와 사용자의 행동 3개 생성해야 합니다.\n" +
                        "- 사용자의 선택에 따라 점수를 부여해야 합니다.\n" +
                        "- 사용자의 선택에 따라 스탯이 변화할 수도 있습니다. 스탯 변화는 전달된 필드들에서만 발생해야 합니다.\n" +
                        "- 아이템을 획득할 수 있습니다.\n\n" +
                        "### Health 스탯은 생명력입니다. 0이 될 시 게임 오버가 되어야 합니다. ###\n\n" +
                        "현재 스탯:\n%s\n\n" +
                        "게임 주제: %s\n" +
                        "키워드: %s",
                userStatsJoiner,
                gameSettings.getTheme(),
                String.join(", ", gameSettings.getKeywords())
        );

        messages.add(ChatGptRequestMessage.builder()
                .role("system")
                .content(systemMessageContent)
                .build());

        System.out.println(StoryContentService.formatStoryContent(storyContent)
                + "<Now 사용자 행동> : "+generateStoryRequest.getSelectedOption());

        messages.add(ChatGptRequestMessage.builder()
                .role(ChatGptConfig.ROLE)
                .content(StoryContentService.formatStoryContent(storyContent)
                        + "<Now 사용자 행동> : "+generateStoryRequest.getSelectedOption())
                .build());



        // tools 데이터 설정
        List<Map<String, Object>> tools = List.of(
                createCustomTool()
        );



        ChatGptRequest chatGptRequest = ChatGptRequest.builder()
                .model(ChatGptConfig.CHAT_MODEL)
                .temperature(ChatGptConfig.TEMPERATURE)
                .stream(ChatGptConfig.STREAM)
                .messages(messages)
                .tools(tools)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        ChatGptResponse chatGptResponse = this.getResponse(this.buildHttpEntity(chatGptRequest));

        try {
            System.out.printf("chatGptResponse: %s\n", objectMapper.writeValueAsString(chatGptResponse));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return chatGptResponse;
    }

    private Map<String, Object> createCustomTool() {
        return Map.of(
                "type", "function",
                "function", Map.of(
                        "name", "generate_story",
                        "description", "A function that creates the next story and user actions based on the information given in the prompt.",
                        "parameters", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "nextContent", Map.of(
                                                "type", "string",
                                                "description", "Create the next story based on the story so far"
                                        ),
                                        "option1", Map.of(
                                                "type", "string",
                                                "description", "The next options are created based on the topic and keywords in the created story. 1"
                                        ),
                                        "option2", Map.of(
                                                "type", "string",
                                                "description", "The next options are created based on the topic and keywords in the created story. 2"
                                        ),
                                        "option3", Map.of(
                                                "type", "string",
                                                "description", "The next options are created based on the topic and keywords in the created story. 3"
                                        ),
                                        "changeOfStat", Map.of(
                                                "type", "array",
                                                "items", Map.of(
                                                        "type", "object",
                                                        "properties", Map.of(
                                                                "statName", Map.of(
                                                                        "type", "string",
                                                                        "description", "Based on pre-given user stat information, stats change according to the story."
                                                                ),
                                                                "changeValue", Map.of(
                                                                        "type", "integer",
                                                                        "description", "Change value (changes conservatively)"
                                                                )
                                                        )
                                                )
                                        ),
                                        "newItem", Map.of(
                                                "type", "array",
                                                "items", Map.of(
                                                        "type", "object",
                                                        "properties", Map.of(
                                                                "id", Map.of(
                                                                        "type", "string",
                                                                        "description", "Item ID (automatic generate)"
                                                                ),
                                                                "itemName", Map.of(
                                                                        "type", "string",
                                                                        "description", "Item name"
                                                                ),
                                                                "itemType", Map.of(
                                                                        "type", "string",
                                                                        "description", "Item type"
                                                                ),
                                                                "value", Map.of(
                                                                        "type", "integer",
                                                                        "description", "Item value (gold)"
                                                                ),
                                                                "changeOfStat", Map.of(
                                                                        "type", "array",
                                                                        "items", Map.of(
                                                                                "type", "object",
                                                                                "properties", Map.of(
                                                                                        "statName", Map.of(
                                                                                                "type", "string",
                                                                                                "description", "Based on pre-given user stat information, stats change according to the item."
                                                                                        ),
                                                                                        "changeValue", Map.of(
                                                                                                "type", "integer",
                                                                                                "description", "Change value (item stat)"
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        ),
                                        "score", Map.of(
                                                "type", "integer",
                                                "description", "Give scores for the options selected by the user (default: 10)"
                                        )
                                )
                        ),
                        "required", List.of("nextContent", "option1", "option2", "option3", "score")
                )
        );
    }



}
