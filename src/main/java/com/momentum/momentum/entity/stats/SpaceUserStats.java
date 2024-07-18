package com.momentum.momentum.entity.stats;

import com.momentum.momentum.entity.UserStats;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceUserStats extends UserStats {
    private int spaceshipPiloting;
    private int spaceshipEngineering;
    private int spaceshipCombat;
    private int spaceshipStealth;
    private int spaceshipNavigation;
    private int spaceshipRepair;
}
