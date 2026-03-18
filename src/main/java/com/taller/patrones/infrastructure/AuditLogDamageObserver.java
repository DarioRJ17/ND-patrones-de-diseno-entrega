package com.taller.patrones.infrastructure;

import com.taller.patrones.domain.DamageEvent;
import com.taller.patrones.domain.DamageObserver;

public class AuditLogDamageObserver implements DamageObserver {
    @Override
    public void onDamage(DamageEvent event) {
        System.out.println("[Audit] " + event.attacker().getName()
                + " -> " + event.defender().getName()
                + " con " + event.attack().getName()
                + " (" + event.damage() + " daño)");
    }
}
