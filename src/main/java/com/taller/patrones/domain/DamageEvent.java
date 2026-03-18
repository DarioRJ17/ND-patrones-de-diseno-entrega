package com.taller.patrones.domain;

public record DamageEvent(
        Battle battle,
        Character attacker,
        Character defender,
        Attack attack,
        int damage
) {
}
