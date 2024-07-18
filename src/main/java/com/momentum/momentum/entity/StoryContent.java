package com.momentum.momentum.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "storyContent")
public class StoryContent {

    @Id
    private String id;

    private String roomId;

    @Field("stories")
    private List<StoryModel> stories;

    public StoryContent(String roomId, List<StoryModel> stories) {
        this.roomId = roomId;
        this.stories = stories != null ? stories : new ArrayList<>();
    }

    @Data
    public static class StoryModel {

        private int storyIndex;
        private String previousUserSelectOption;
        private String content;

        private String option1;
        private String option2;
        private String option3;
        // Getters and setters
    }
}

