package com.taller.patrones.domain;

import com.taller.patrones.infrastructure.combat.CombatEngine;

public class AttackCommand implements BattleCommand {

    private final Battle battle;
    private final Character attacker;
    private final Character defender;
    private final Attack attack;
    private final CombatEngine combatEngine;

    // Campo para deshacer
    private int previousHp;

    public AttackCommand(Battle battle,
                         Character attacker,
                         Character defender,
                         Attack attack,
                         CombatEngine combatEngine) {
        this.battle = battle;
        this.attacker = attacker;
        this.defender = defender;
        this.attack = attack;
        this.combatEngine = combatEngine;
    }

    @Override
    public void execute() {
        // guardar HP antes del ataque (para undo)
        previousHp = defender.getCurrentHp();

        // delega toda la lógica de daño en el engine, que ya sabe de CompositeAttack
        combatEngine.applyAttack(battle, attacker, defender, attack);
    }


    @Override
    public void undo() {
        // restaurar HP anterior
        defender.setCurrentHp(previousHp);

        battle.log("Se deshace el ataque " + attack.getName()
                + " de " + attacker.getName());

        // revertir el cambio de turno
        battle.switchTurn();
    }
}
