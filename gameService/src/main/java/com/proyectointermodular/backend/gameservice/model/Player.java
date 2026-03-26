package com.proyectointermodular.backend.gameservice.model;

import java.util.List;

public class Player {

    private long id;


    private String nick;

    private String avatarUrl;

    private int score;

    private int health;

    List<Troop> troops;

    public Player() {
    }

    public Player( String nick, long id, String avatarUrl) {
        this.nick = nick;
        this.id = id;
        this.avatarUrl = avatarUrl;
    }

    //getters
    public String getNick() {
        return nick;
    }

    public long getId() {
        return id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public int getScore() {
        return score;
    }

    public int getHealth() {
        return health;
    }

    public List<Troop> getTroops() {
        return troops;
    }

    //setters
    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setTroops(List<Troop> troops) {
        this.troops = troops;
    }
}
