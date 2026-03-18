package com.taller.patrones.domain;

public class StatusDamageStrategy implements DamageStrategy {
    @Override
    public int calculate(Character attacker, Character defender, Attack attack) {
        // STATUS no hace daño directo
        return 0;
    }
}
