package com.taller.patrones.infrastructure.combat;

import com.taller.patrones.domain.*;
import com.taller.patrones.domain.Character;

import java.util.EnumMap;
import java.util.Map;

/**
 * Motor de combate. Calcula daño y crea ataques.
 * <p>
 * Nota: Esta clase crece cada vez que añadimos un ataque nuevo o un tipo de daño distinto.
 */
public class CombatEngine {

    /**
     * Crea un ataque a partir de su nombre.
     * Cada ataque nuevo se implementará en la nueva factoría de ataque
     */
    private final AttackFactory attackFactory;
    private final Map<Attack.AttackType, DamageStrategy> damageStrategies = new EnumMap<>(Attack.AttackType.class);

    public CombatEngine(AttackFactory attackFactory) {
        this.attackFactory = attackFactory;
        damageStrategies.put(Attack.AttackType.NORMAL,  new NormalDamageStrategy());
        damageStrategies.put(Attack.AttackType.SPECIAL, new SpecialDamageStrategy());
        damageStrategies.put(Attack.AttackType.STATUS,  new StatusDamageStrategy());
        damageStrategies.put(Attack.AttackType.CRITICAL, new CriticalDamageStrategy());
    }

    public Attack createAttack(String name) {
        return attackFactory.create(name);
    }

    /**
     * Calcula el daño según el tipo de ataque.
     * Cada fórmula nueva (ej. crítico, veneno con tiempo) requiere modificar este switch.
     */
    public int calculateDamage(Character attacker, Character defender, Attack attack) {
        DamageStrategy strategy = damageStrategies.get(attack.getType());
        if (strategy == null) {
            // fallback por si falta alguna estrategia
            strategy = new NormalDamageStrategy();
        }
        return strategy.calculate(attacker, defender, attack);
    }
}
