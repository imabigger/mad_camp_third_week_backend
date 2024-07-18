package com.momentum.momentum.model;

import com.momentum.momentum.entity.Item;
import com.momentum.momentum.entity.StatChange;
import lombok.Data;

import java.util.List;

@Data
public class GenerateStoryResponse {
    private String nextContent;
    private String Option1;
    private String Option2;
    private String Option3;
    private List<StatChange> changeOfStat;
    private List<Item> newItem;
    private int score;
}
