package com.momentum.momentum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.momentum.momentum.entity.StoryContent;
import com.momentum.momentum.repository.StoryContentRepository;

@Service
public class StoryContentService {

    @Autowired
    private StoryContentRepository storyContentRepository;

    public StoryContent getStoryContentByRoomId(String roomId) {
        return storyContentRepository.findByRoomId(roomId);
    }

    public StoryContent saveStoryContent(StoryContent storyContent) {
        return storyContentRepository.save(storyContent);
    }

    public static String formatStoryContent(StoryContent storyContent) {
        StringBuilder formattedStory = new StringBuilder("<현재까지의 스토리>\n");

        for (int i = 0; i < storyContent.getStories().size(); i++) {
            StoryContent.StoryModel storyModel = storyContent.getStories().get(i);
            formattedStory.append(i)
                    .append(" : ")
                    .append(storyModel.getPreviousUserSelectOption())
                    .append(" -> ")
                    .append(storyModel.getContent())
                    .append("\n");
        }
        return formattedStory.toString();
    }

}
