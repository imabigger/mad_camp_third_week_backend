package com.momentum.momentum.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
@Document(collection = "rooms")
public class SRoom {
    @Id
    private String id;
    private String room;
    private String userId;
    private GameSettings settings;
    private UserStats userStats;
    private List<Item> items;
    private Date createdAt;
    private Date updatedAt;
    private int score;

}
