package com.momentum.momentum.model;

import com.momentum.momentum.entity.Item;
import com.momentum.momentum.entity.UserStats;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CreateRoomRequest {
    private String room;
    private String theme;
    private List<String> keywords;
    private Map<String, Integer> userStats;
    private List<Item> items;
}
