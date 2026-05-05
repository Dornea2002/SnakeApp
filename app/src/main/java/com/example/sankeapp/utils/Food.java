package com.example.sankeapp.utils;

import static com.example.sankeapp.utils.Constants.CELL_SIZE;

import java.util.Random;

public class Food {

    public static Coordinates getRandomFood(int surfaceWidth, int surfaceHeight) {
        Coordinates randomCoordinates = new Coordinates();
        int x = new Random().nextInt(surfaceWidth / CELL_SIZE) * CELL_SIZE;
        int y = new Random().nextInt(surfaceHeight / CELL_SIZE) * CELL_SIZE;

        randomCoordinates.setPositionX(x);
        randomCoordinates.setPositionY(y);
        return randomCoordinates;
    }
}
