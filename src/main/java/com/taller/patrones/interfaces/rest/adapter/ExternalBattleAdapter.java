package com.taller.patrones.interfaces.rest.adapter;

import com.taller.patrones.interfaces.rest.dto.ExternalBattleRequest;

public class ExternalBattleAdapter {
    public ExternalBattleCommand toCommand(ExternalBattleRequest req) {
        String fighter1Name = req.fighter1_name != null ? req.fighter1_name : "Héroe";
        int fighter1Hp  = req.fighter1_hp  != null ? req.fighter1_hp  : 150;
        int fighter1Atk = req.fighter1_atk != null ? req.fighter1_atk : 25;

        String fighter2Name = req.fighter2_name != null ? req.fighter2_name : "Dragón";
        int fighter2Hp  = req.fighter2_hp  != null ? req.fighter2_hp  : 120;
        int fighter2Atk = req.fighter2_atk != null ? req.fighter2_atk : 30;

        return new ExternalBattleCommand(
                fighter1Name, fighter1Hp, fighter1Atk,
                fighter2Name, fighter2Hp, fighter2Atk
        );
    }

    public record ExternalBattleCommand(
            String fighter1Name, int fighter1Hp, int fighter1Atk,
            String fighter2Name, int fighter2Hp, int fighter2Atk
    ) {}
}
