package com.momentum.momentum.entity.stats;

import com.momentum.momentum.entity.UserStats;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MedievalUserStats extends UserStats {
    private int magic;
    private int swordsmanship;
    private int dexterity;
    private int constitution;
    private int charisma;
    private int riding;
}
