package com.proyectointermodular.backend.gameservice.model;


import io.netty.util.HashedWheelTimer;
import jakarta.persistence.Timeout;

import java.util.concurrent.TimeUnit;

/**
 * Clase que define el reloj central del juego teniendo en cuenta el bucket de eventos
 */
public class TimingWheel {
    private static final int TICKS_MS = 100;
    private static final int WHEEL_SIZE = 50;

    public static HashedWheelTimer getNewWheel(){
        return new HashedWheelTimer(
                TICKS_MS,
                TimeUnit.MILLISECONDS,
                WHEEL_SIZE
        );
    }
}
