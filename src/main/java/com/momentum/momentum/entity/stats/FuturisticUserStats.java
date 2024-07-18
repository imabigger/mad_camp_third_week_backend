package com.momentum.momentum.entity.stats;

import com.momentum.momentum.entity.UserStats;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FuturisticUserStats extends UserStats {
    private int technology;
    private int hacking;
    private int stealth;
    private int piloting;
}
