package com.taller.patrones.infrastructure.combat;

import com.taller.patrones.domain.Attack;
import com.taller.patrones.domain.Character;

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

    public CombatEngine(AttackFactory attackFactory) {
        this.attackFactory = attackFactory;
    }

    public Attack createAttack(String name) {
        return attackFactory.create(name);
    }

    /**
     * Calcula el daño según el tipo de ataque.
     * Cada fórmula nueva (ej. crítico, veneno con tiempo) requiere modificar este switch.
     */
    public int calculateDamage(Character attacker, Character defender, Attack attack) {
        return switch (attack.getType()) {
            case NORMAL -> {
                int raw = attacker.getAttack() * attack.getBasePower() / 100;
                yield Math.max(1, raw - defender.getDefense());
            }
            case SPECIAL -> {
                int raw = attacker.getAttack() * attack.getBasePower() / 100;
                int effectiveDef = defender.getDefense() / 2;
                yield Math.max(1, raw - effectiveDef);
            }
            case STATUS -> attacker.getAttack(); // Los de estado no hacen daño directo... ¿o sí?
            default -> 0;
        };
    }
}
