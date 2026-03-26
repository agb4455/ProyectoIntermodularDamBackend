package com.proyectointermodular.backend.gameservice.model;

public class Room {
    private long roomId;
    private Player [] players;

    public Room(long roomId, Player [] players) {
        this.roomId = roomId;
        this.players = players;
    }
}
