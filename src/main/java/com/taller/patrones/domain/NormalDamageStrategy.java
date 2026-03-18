package com.taller.patrones.domain;

public class NormalDamageStrategy implements DamageStrategy {
    @Override
    public int calculate(Character attacker, Character defender, Attack attack) {
        int raw = attacker.getAttack() * attack.getBasePower() / 100;
        return Math.max(1, raw - defender.getDefense());
    }
}
