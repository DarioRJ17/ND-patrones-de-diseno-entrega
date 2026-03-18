package com.taller.patrones.infrastructure.combat;

import com.taller.patrones.domain.Attack;

public class DefaultAttackFactory implements AttackFactory {
    @Override
    public Attack create(String name) {
        String n = name != null ? name.toUpperCase() : "";
        return switch (n) {
            case "TACKLE"       -> new Attack("Tackle", 40, Attack.AttackType.NORMAL);
            case "SLASH"        -> new Attack("Slash", 55, Attack.AttackType.NORMAL);
            case "FIREBALL"     -> new Attack("Fireball", 80, Attack.AttackType.SPECIAL);
            case "ICE_BEAM"     -> new Attack("Ice Beam", 70, Attack.AttackType.SPECIAL);
            case "POISON_STING" -> new Attack("Poison Sting", 20, Attack.AttackType.STATUS);
            case "THUNDER"      -> new Attack("Thunder", 90, Attack.AttackType.SPECIAL);
            // añadido ataque meteoro
            case "METEORO"      -> new Attack("Meteoro", 120, Attack.AttackType.SPECIAL);
            default             -> new Attack("Golpe", 30, Attack.AttackType.NORMAL);
        };
    }
}
