package com.momentum.momentum.repository;

import com.momentum.momentum.entity.GameSettings;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameSettingsRepository extends MongoRepository<GameSettings, String> {
    GameSettings findByTheme(String theme);
}
