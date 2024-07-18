package com.momentum.momentum.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.momentum.momentum.entity.Item;
import com.momentum.momentum.entity.SRoom;
import com.momentum.momentum.entity.StatChange;
import com.momentum.momentum.model.GenerateStoryRequest;
import com.momentum.momentum.model.GenerateStoryResponse;
import com.momentum.momentum.model.gptModel.ChatGptMessage;
import com.momentum.momentum.model.gptModel.ChatGptResponse;
import com.momentum.momentum.service.ChatGptService;
import com.momentum.momentum.service.SinglePlayRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.momentum.momentum.entity.StoryContent;
import com.momentum.momentum.service.StoryContentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/generate")
public class StoryContentController {

    private final StoryContentService storyContentService;
    private final SinglePlayRoomService singlePlayRoomService;
    private final ChatGptService chatGptService;

    public StoryContentController(StoryContentService storyContentService, SinglePlayRoomService singlePlayRoomService, ChatGptService chatGptService) {
        this.storyContentService = storyContentService;
        this.singlePlayRoomService = singlePlayRoomService;
        this.chatGptService = chatGptService;
    }

    @GetMapping("/{roomId}/last")
    public ResponseEntity<StoryContent.StoryModel> getLastStoryContentByRoomId(@PathVariable String roomId) {
        StoryContent storyContent = storyContentService.getStoryContentByRoomId(roomId);
        if (storyContent != null) {
            StoryContent.StoryModel lastStory = storyContent.getStories().getLast();
            return ResponseEntity.ok(lastStory);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<StoryContent> getStoryContentByRoomId(@PathVariable String roomId) {
        StoryContent storyContent = storyContentService.getStoryContentByRoomId(roomId);
        if (storyContent != null) {
            return ResponseEntity.ok(storyContent);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{roomId}")
    public ResponseEntity<GenerateStoryResponse> createOrUpdateStoryContent(@RequestBody GenerateStoryRequest generateStoryRequest, @PathVariable String roomId) {
        StoryContent storyContent = storyContentService.getStoryContentByRoomId(roomId);
        SRoom sRoom = singlePlayRoomService.getRoomById(roomId).orElse(null);

        if(sRoom != null && storyContent == null){
            storyContent = new StoryContent(roomId, new ArrayList<>());
            storyContentService.saveStoryContent(storyContent);
        }

        if (storyContent == null || sRoom == null) {
            return ResponseEntity.notFound().build();
        }

        //  만약 Heath stat이 0 이하가 되었다면 더이상 진행하지 않고 게임을 종료합니다.
        if (sRoom.getUserStats().getHealth() <= 0) {
            GenerateStoryResponse generateStoryResponse = new GenerateStoryResponse();
            generateStoryResponse.setNextContent("당신은 죽었습니다. 게임이 종료됩니다.\n최종 스코어 : " + sRoom.getScore());
            generateStoryResponse.setOption1("Go to main");
            generateStoryResponse.setOption2("Go to main");
            generateStoryResponse.setOption3("Go to main");
            return ResponseEntity.ok(generateStoryResponse);
        }

        ChatGptResponse chatGptResponse;
        try {
            // GPT 요청 로직
            chatGptResponse = chatGptService.askToGptGenerateNextStory(generateStoryRequest, roomId, storyContent);
        } catch (Exception e) {
            System.out.println("Failed to ask GPT to generate next story: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }

        // Function call에서 argument 추출 및 매핑
        GenerateStoryResponse generateStoryResponse = new GenerateStoryResponse();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Function call에서 argument 추출
            // Function call에서 argument 추출
            ChatGptMessage.ToolCall toolCall = chatGptResponse.getChoices().get(0).getMessage().getToolCalls().get(0);
            String arguments = toolCall.getFunction().getArguments();
            Map<String, Object> argumentMap = objectMapper.readValue(arguments, Map.class);
            System.out.println("argumentMap: " + argumentMap);

// GenerateStoryResponse에 매핑
            generateStoryResponse.setNextContent((String) argumentMap.get("nextContent"));
            generateStoryResponse.setOption1((String) argumentMap.get("option1"));
            generateStoryResponse.setOption2((String) argumentMap.get("option2"));
            generateStoryResponse.setOption3((String) argumentMap.get("option3"));

            if (argumentMap.containsKey("changeOfStat")) {
                List<Map<String, Object>> statChanges = (List<Map<String, Object>>) argumentMap.get("changeOfStat");
                List<StatChange> changeOfStat = objectMapper.convertValue(statChanges, new TypeReference<List<StatChange>>() {});
                generateStoryResponse.setChangeOfStat(changeOfStat);

                singlePlayRoomService.updateUserStatsWithStatChange(sRoom.getUserStats(), changeOfStat, roomId);
            }

            if (argumentMap.containsKey("newItem")) {
                List<Map<String, Object>> newItems = (List<Map<String, Object>>) argumentMap.get("newItem");
                List<Item> newItem = objectMapper.convertValue(newItems, new TypeReference<List<Item>>() {});
                generateStoryResponse.setNewItem(newItem);

                // sRoom의 현재 아이템 리스트에 newItem 리스트를 추가합니다.
                List<Item> updatedItems = new ArrayList<>(sRoom.getItems());
                updatedItems.addAll(newItem);

                // 업데이트된 아이템 리스트를 사용하여 서비스 메서드를 호출합니다.
                singlePlayRoomService.updateItems(roomId, updatedItems);
            }

// 스코어가 없는 경우 기본값 10점으로 설정
            if (argumentMap.containsKey("score")) {
                generateStoryResponse.setScore((Integer) argumentMap.get("score"));
                sRoom.setScore(sRoom.getScore() + (Integer) argumentMap.get("score"));
            } else {
                generateStoryResponse.setScore(10);
                sRoom.setScore(sRoom.getScore() + 10);
            }
            singlePlayRoomService.updateScore(roomId, sRoom.getScore());

            // 새로운 스토리 항목 생성
            StoryContent.StoryModel newStory = new StoryContent.StoryModel();
            newStory.setStoryIndex(storyContent.getStories().size());
            newStory.setPreviousUserSelectOption(generateStoryRequest.getSelectedOption());
            newStory.setContent(generateStoryResponse.getNextContent());
            newStory.setOption1(generateStoryResponse.getOption1());
            newStory.setOption2(generateStoryResponse.getOption2());
            newStory.setOption3(generateStoryResponse.getOption3());

            // 스토리 콘텐츠에 새로운 스토리 항목 추가
            storyContent.getStories().add(newStory);
            storyContentService.saveStoryContent(storyContent);

        } catch (Exception e) {
            System.out.println("Failed to parse GPT response: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(generateStoryResponse);
    }
}
