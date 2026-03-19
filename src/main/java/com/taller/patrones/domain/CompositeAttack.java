package com.taller.patrones.domain;

import java.util.List;

public class CompositeAttack extends  Attack {
    private final List<Attack> attacks;

    public CompositeAttack(String name, AttackType type, List<Attack> attacks) {
        super(name, 0, type);
        this.attacks = attacks;
    }

    public List<Attack> getAttacks() {
        return attacks;
    }
}
