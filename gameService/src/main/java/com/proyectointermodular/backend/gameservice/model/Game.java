package com.proyectointermodular.backend.gameservice.model;

import io.netty.util.HashedWheelTimer;

public class Game {
    private final HashedWheelTimer timerWheel = TimingWheel.getNewWheel();
    private Room [] rooms;

    public void createRoom(Player [] players, String roomName){

    }

}
