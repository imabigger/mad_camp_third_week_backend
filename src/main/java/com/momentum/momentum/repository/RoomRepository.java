package com.momentum.momentum.repository;

import com.momentum.momentum.entity.Item;
import com.momentum.momentum.entity.SRoom;
import com.momentum.momentum.entity.UserStats;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;

public interface RoomRepository extends MongoRepository<SRoom, String> {
    SRoom findByRoom(String room);
    List<SRoom> findByUserId(String userId);

    @Modifying
    @Query("{ '_id': ?0 }")
    @Update("{ '$set': { 'userStats': ?1 } }")
    void updateUserStats(String roomId, UserStats userStats);

    @Modifying
    @Query("{ '_id': ?0 }")
    @Update("{ '$set': { 'items': ?1 } }")
    void updateItems(String roomId, List<Item> items);

    @Modifying
    @Query("{ '_id': ?0 }")
    @Update("{ '$set': { 'score': ?1 } }")
    void updateScore(String roomId, int score);
}
