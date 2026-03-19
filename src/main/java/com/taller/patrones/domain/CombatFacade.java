package com.taller.patrones.domain;

import com.taller.patrones.application.BattleService;
import com.taller.patrones.application.DamageEventPublisher;

public class CombatFacade {

    private final BattleService battleService;

    public CombatFacade() {
        this.battleService = new BattleService();
    }

    public BattleStartDto startBattle(String playerName, String enemyName) {
        var result = battleService.startBattle(playerName, enemyName);
        return new BattleStartDto(result.battleId(), result.battle());
    }

    public BattleStartDto startBattleFromExternal(String fighter1Name, int fighter1Hp, int fighter1Atk,
                                                  String fighter2Name, int fighter2Hp, int fighter2Atk) {
        var result = battleService.startBattleFromExternal(
                fighter1Name, fighter1Hp, fighter1Atk,
                fighter2Name, fighter2Hp, fighter2Atk
        );
        return new BattleStartDto(result.battleId(), result.battle());
    }

    public Battle executePlayerAttack(String battleId, String attackName) {
        battleService.executePlayerAttack(battleId, attackName);
        return battleService.getBattle(battleId);
    }

    public Battle executeEnemyAttack(String battleId, String attackName) {
        battleService.executeEnemyAttack(battleId, attackName);
        return battleService.getBattle(battleId);
    }

    public Battle enemyAutoTurn(String battleId) {
        Battle battle = battleService.getBattle(battleId);
        if (battle == null || battle.isFinished() || battle.isPlayerTurn()) {
            return battle;
        }
        String attack = BattleService.ENEMY_ATTACKS.get(
                (int) (Math.random() * BattleService.ENEMY_ATTACKS.size())
        );
        battleService.executeEnemyAttack(battleId, attack);
        return battleService.getBattle(battleId);
    }

    public Battle undoLast(String battleId) {
        battleService.undoLastCommand();
        return battleService.getBattle(battleId);
    }

    public Battle getBattle(String battleId) {
        return battleService.getBattle(battleId);
    }

    public DamageEventPublisher getDamagePublisher() {
        return battleService.getDamagePublisher();
    }

    public record BattleStartDto(String battleId, Battle battle) {}
}
