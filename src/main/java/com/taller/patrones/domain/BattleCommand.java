package com.taller.patrones.domain;

public interface BattleCommand {
    void execute();
    void undo();
}
