package com.proyectointermodular.backend.gameservice.model;

public class Troop {
    private String name;
    private String attack;
    private int deployTime;
    private int attackTime;

    public Troop() {
    }

    public Troop(String name, String attack, int deployTime, int attackTime) {
        this.name = name;
        this.attack = attack;
        this.deployTime = deployTime;
        this.attackTime = attackTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttack() {
        return attack;
    }

    public void setAttack(String attack) {
        this.attack = attack;
    }

    public int getDeployTime() {
        return deployTime;
    }

    public void setDeployTime(int deployTime) {
        this.deployTime = deployTime;
    }

    public int getAttackTime() {
        return attackTime;
    }

    public void setAttackTime(int attackTime) {
        this.attackTime = attackTime;
    }
}
