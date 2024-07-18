package com.momentum.momentum.entity.stats;

import com.momentum.momentum.entity.UserStats;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GhostUserStats extends UserStats {
    private int invisibility;
    private int possession;
    private int fearResistance;
}
