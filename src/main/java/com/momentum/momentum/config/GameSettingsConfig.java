package com.momentum.momentum.config;

import com.momentum.momentum.entity.GameSettings;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "game-settings")
public class GameSettingsConfig {

    private Map<String, GameSettings> themes;

    @PostConstruct
    public void init() {
        System.out.println("GameSettingsConfig initialized with themes: " + themes);
    }
}