package com.taller.patrones.domain;

public interface DamageStrategy {
    int calculate(Character attacker, Character defender, Attack attack);
}
