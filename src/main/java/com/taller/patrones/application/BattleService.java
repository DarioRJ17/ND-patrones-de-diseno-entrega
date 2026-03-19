package com.taller.patrones.application;

import com.taller.patrones.domain.*;
import com.taller.patrones.domain.Character;
import com.taller.patrones.infrastructure.combat.CombatEngine;
import com.taller.patrones.infrastructure.combat.DefaultAttackFactory;
import com.taller.patrones.infrastructure.persistence.BattleRepository;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.UUID;

/**
 * Caso de uso: gestionar batallas.
 * <p>
 * Nota: Crea sus propias dependencias con new. Cada vez que necesitamos
 * un CombatEngine o BattleRepository, hacemos new aquí.
 */
public class BattleService {

    private final CombatEngine combatEngine = new CombatEngine(new DefaultAttackFactory());
    private final BattleRepository battleRepository = BattleRepository.getInstance();
    private final DamageEventPublisher damagePublisher = new DamageEventPublisher();
    private final Deque<BattleCommand> history = new ArrayDeque<>();
    public static final List<String> PLAYER_ATTACKS = List.of("TACKLE", "SLASH", "FIREBALL", "ICE_BEAM", "POISON_STING", "THUNDER", "METEORO", "TRIPLE_COMBO");
    public static final List<String> ENEMY_ATTACKS = List.of("TACKLE", "SLASH", "FIREBALL");

    public DamageEventPublisher getDamagePublisher() {
        return damagePublisher;
    }

    public BattleStartResult startBattle(String playerName, String enemyName) {
        Character player = new Character.Builder()
                .name(playerName != null ? playerName : "Héroe")
                .maxHp(150)
                .attack(25)
                .defense(15)
                .speed(20)
                .build();

        Character enemy = new Character.Builder()
                .name(enemyName != null ? enemyName : "Dragón")
                .maxHp(120)
                .attack(30)
                .defense(10)
                .speed(15)
                .build();

        Battle battle = new Battle(player, enemy);
        String battleId = UUID.randomUUID().toString();
        battleRepository.save(battleId, battle);

        return new BattleStartResult(battleId, battle);
    }

    public Battle getBattle(String battleId) {
        return battleRepository.findById(battleId);
    }

    public void executePlayerAttack(String battleId, String attackName) {
        Battle battle = battleRepository.findById(battleId);
        if (battle == null || battle.isFinished() || !battle.isPlayerTurn()) return;

        Attack attack = combatEngine.createAttack(attackName);
        BattleCommand cmd = new AttackCommand(
                battle,
                battle.getPlayer(),
                battle.getEnemy(),
                attack,
                combatEngine
        );
        cmd.execute();
        history.push(cmd);
    }

    public void executeEnemyAttack(String battleId, String attackName) {
        Battle battle = battleRepository.findById(battleId);
        if (battle == null || battle.isFinished() || battle.isPlayerTurn()) return;

        Attack attack = combatEngine.createAttack(attackName != null ? attackName : "TACKLE");
        BattleCommand cmd = new AttackCommand(
                battle,
                battle.getEnemy(),
                battle.getPlayer(),
                attack,
                combatEngine
        );
        cmd.execute();
        history.push(cmd);
    }

    public void undoLastCommand() {
        if (history.isEmpty()) return;
        BattleCommand last = history.pop();
        last.undo();
    }

    public BattleStartResult startBattleFromExternal(String fighter1Name, int fighter1Hp, int fighter1Atk,
                                                     String fighter2Name, int fighter2Hp, int fighter2Atk) {
        Character player = new Character.Builder().name(fighter1Name).maxHp(fighter1Hp).attack(fighter1Atk).defense(10).speed(10).build();
        Character enemy = new Character.Builder().name(fighter2Name).maxHp(fighter2Hp).attack(fighter2Atk).defense(10).speed(10).build();
        Battle battle = new Battle(player, enemy);
        String battleId = UUID.randomUUID().toString();
        battleRepository.save(battleId, battle);
        return new BattleStartResult(battleId, battle);
    }

    public record BattleStartResult(String battleId, Battle battle) {}
}
