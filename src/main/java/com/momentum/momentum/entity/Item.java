package com.momentum.momentum.entity;

import java.util.List;

import lombok.Data;

@Data
public class Item {
    private String id;
    private String itemName;
    private String itemType;
    private int value;
    private List<StatChange> changeOfStat;

    // Getters and Setters
}