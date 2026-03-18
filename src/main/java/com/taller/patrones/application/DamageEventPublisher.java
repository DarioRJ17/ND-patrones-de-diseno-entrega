package com.taller.patrones.application;

import com.taller.patrones.domain.DamageEvent;
import com.taller.patrones.domain.DamageObserver;

import java.util.ArrayList;
import java.util.List;

public class DamageEventPublisher {
    private final List<DamageObserver> observers = new ArrayList<>();

    public void subscribe(DamageObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(DamageObserver observer) {
        observers.remove(observer);
    }

    public void publish(DamageEvent event) {
        for (DamageObserver o : observers) {
            o.onDamage(event);
        }
    }
}
