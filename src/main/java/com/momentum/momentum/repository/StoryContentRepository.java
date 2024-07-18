package com.momentum.momentum.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.momentum.momentum.entity.StoryContent;

public interface StoryContentRepository extends MongoRepository<StoryContent, String> {
    StoryContent findByRoomId(String roomId);
}
