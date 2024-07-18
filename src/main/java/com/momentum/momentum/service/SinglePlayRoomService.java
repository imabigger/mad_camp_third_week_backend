package com.momentum.momentum.service;

import com.momentum.momentum.config.GameSettingsConfig;
import com.momentum.momentum.entity.*;
import com.momentum.momentum.entity.stats.GhostUserStats;
import com.momentum.momentum.entity.stats.MedievalUserStats;
import com.momentum.momentum.entity.stats.FuturisticUserStats;
import com.momentum.momentum.entity.stats.SpaceUserStats;
import com.momentum.momentum.repository.GameSettingsRepository;
import com.momentum.momentum.repository.RoomRepository;
import com.momentum.momentum.repository.StoryContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class SinglePlayRoomService {
    private final RoomRepository roomRepository;

    private final GameSettingsRepository gameSettingsRepository;

    private final GameSettingsConfig gameSettingsConfig;

    private final StoryContentRepository storyContentRepository;


    @Autowired
    public SinglePlayRoomService(RoomRepository roomRepository, GameSettingsRepository gameSettingsRepository, GameSettingsConfig gameSettingsConfig, StoryContentRepository storyContentRepository) {
        this.roomRepository = roomRepository;
        this.gameSettingsRepository = gameSettingsRepository;
        this.gameSettingsConfig = gameSettingsConfig;
        this.storyContentRepository = storyContentRepository;
    }

    public List<SRoom> getAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<SRoom> getRoomById(String id) {
        return roomRepository.findById(id);
    }

    public List<SRoom> getRoomsByUserId(String userId) {
        return roomRepository.findByUserId(userId);
    }

    public SRoom createRoom(SRoom SRoom, Map<String, Integer> userStatsRequest) {
        System.out.println("[SinglePlayRoomService] createRoom start");

        GameSettings settings = SRoom.getSettings();
        String themeName = settings.getTheme();
        GameSettings gameSettings = gameSettingsConfig.getThemes().get(themeName); // GameSettingsConfig에서 테마 가져오기 없으면 null이 됨.

        if (gameSettings == null) {
            throw new IllegalArgumentException("Invalid theme: " + themeName);
        }

        // SRoom의 키워드가 유효한지 검증
        for (String keyword : settings.getKeywords()) {
            if (!gameSettings.getKeywords().contains(keyword)) {
                throw new IllegalArgumentException("Invalid keyword: " + keyword + " for theme: " + themeName);
            }
        }

        UserStats userStats = createUserStatsForTheme(themeName, userStatsRequest, settings.getKeywords());
        SRoom.setUserStats(userStats);

        SRoom.setCreatedAt(new Date());
        SRoom.setUpdatedAt(new Date());

        return roomRepository.save(SRoom);
    }

    public SRoom updateRoom(String id, SRoom SRoom) {
        Optional<SRoom> existingRoom = roomRepository.findById(id);
        if (existingRoom.isPresent()) {
            SRoom.setId(id);
            SRoom.setUpdatedAt(new Date());
            return roomRepository.save(SRoom);
        } else {
            throw new RuntimeException("Room not found");
        }
    }

    public void updateItems(String roomId, List<Item> items) {
        roomRepository.updateItems(roomId, items);
    }

    public void updateUserStats(String roomId, UserStats userStats) {
        roomRepository.updateUserStats(roomId, userStats);
    }

    public void updateScore(String roomId, int score) {
        roomRepository.updateScore(roomId, score);
    }

    public void deleteRoom(String id) {
        roomRepository.deleteById(id);
    }

    private UserStats createUserStatsForTheme(String themeName, Map<String, Integer> userStatsRequest, List<String> keywords) {
        UserStats userStats;

        switch (themeName.toLowerCase()) {
            case "medieval":
                userStats = new MedievalUserStats();
                break;
            case "ghost":
                userStats = new GhostUserStats();
                break;
            case "futuristic":
                userStats = new FuturisticUserStats();
                break;
            case "space":
                userStats = new SpaceUserStats();
                break;
            default:
                throw new IllegalArgumentException("Unknown theme: " + themeName);
        }

        // 공통 스탯 설정
        userStats.setAttack(userStatsRequest.getOrDefault("attack", 0));
        userStats.setDefense(userStatsRequest.getOrDefault("defense", 0));
        userStats.setHealth(userStatsRequest.getOrDefault("health", 10));
        userStats.setSpeed(userStatsRequest.getOrDefault("speed", 0));
        userStats.setIntelligence(userStatsRequest.getOrDefault("intelligence", 0));
        userStats.setLuck(userStatsRequest.getOrDefault("luck", 0));

        // 특정 테마 스탯 설정
        setSpecificStats(userStats, userStatsRequest);

        // 키워드에 따른 스탯 적용 (필요에 따라 주석 해제)
        // for (String keyword : keywords) {
        //     keywordStatsMapping.applyStatsForKeywords(userStats, keyword);
        // }

        return userStats;
    }

    private void setSpecificStats(UserStats userStats, Map<String, Integer> userStatsRequest) {
        if (userStats instanceof MedievalUserStats medievalStats) {
            medievalStats.setMagic(userStatsRequest.getOrDefault("magic", 0));
            medievalStats.setSwordsmanship(userStatsRequest.getOrDefault("swordsmanship", 0));
            medievalStats.setDexterity(userStatsRequest.getOrDefault("dexterity", 0));
            medievalStats.setConstitution(userStatsRequest.getOrDefault("constitution", 0));
            medievalStats.setCharisma(userStatsRequest.getOrDefault("charisma", 0));
            medievalStats.setRiding(userStatsRequest.getOrDefault("riding", 0));
        } else if (userStats instanceof GhostUserStats ghostStats) {
            ghostStats.setInvisibility(userStatsRequest.getOrDefault("invisibility", 0));
            ghostStats.setPossession(userStatsRequest.getOrDefault("possession", 0));
            ghostStats.setFearResistance(userStatsRequest.getOrDefault("fearResistance", 0));
        } else if (userStats instanceof FuturisticUserStats futuristicStats) {
            futuristicStats.setTechnology(userStatsRequest.getOrDefault("technology", 0));
            futuristicStats.setHacking(userStatsRequest.getOrDefault("hacking", 0));
            futuristicStats.setStealth(userStatsRequest.getOrDefault("stealth", 0));
            futuristicStats.setPiloting(userStatsRequest.getOrDefault("piloting", 0));
        } else if (userStats instanceof SpaceUserStats spaceStats) {
            spaceStats.setSpaceshipPiloting(userStatsRequest.getOrDefault("spaceshipPiloting", 0));
            spaceStats.setSpaceshipEngineering(userStatsRequest.getOrDefault("spaceshipEngineering", 0));
            spaceStats.setSpaceshipCombat(userStatsRequest.getOrDefault("spaceshipCombat", 0));
            spaceStats.setSpaceshipStealth(userStatsRequest.getOrDefault("spaceshipStealth", 0));
            spaceStats.setSpaceshipNavigation(userStatsRequest.getOrDefault("spaceshipNavigation", 0));
            spaceStats.setSpaceshipRepair(userStatsRequest.getOrDefault("spaceshipRepair", 0));
        }
    }

    private void applyItemStatChanges(List<Item> items, UserStats userStats) {
        for (Item item : items) {
            for (StatChange statChange : item.getChangeOfStat()) {
                applyStatChange(userStats, statChange);
            }
        }
    }

    private void applyStatChange(UserStats userStats, StatChange statChange) {
        try {
            Field field = userStats.getClass().getDeclaredField(statChange.getStatName());
            field.setAccessible(true);
            int currentValue = (int) field.get(userStats);
            field.set(userStats, currentValue + statChange.getChangeValue());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // 매핑되지 않는 스탯 이름은 무시하거나 로그를 남길 수 있음
            System.out.println("Invalid stat name: " + statChange.getStatName());
        }
    }

    public void updateUserStatsWithStatChange(UserStats userStats, List<StatChange> statChanges, String roomId) {
        for (StatChange statChange : statChanges) {
            applyStatChange(userStats, statChange);
        }

        this.updateUserStats(roomId, userStats);
    }
}
