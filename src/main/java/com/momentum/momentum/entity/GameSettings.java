package com.momentum.momentum.entity;

import java.util.List;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
public class GameSettings {
    private String theme;
    private List<String> keywords;

    // Getters and Setters
}
