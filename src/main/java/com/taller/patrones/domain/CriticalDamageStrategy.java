package com.taller.patrones.domain;

public class CriticalDamageStrategy implements DamageStrategy {
    @Override
    public int calculate(Character attacker, Character defender, Attack attack) {
        int base = attacker.getAttack() * attack.getBasePower() / 100;
        int normal = Math.max(1, base - defender.getDefense());
        // 20% probabilidad de crítico: daño * 1.5
        boolean isCritical = Math.random() < 0.2;
        return isCritical ? (int) Math.round(normal * 1.5) : normal;
    }
}
