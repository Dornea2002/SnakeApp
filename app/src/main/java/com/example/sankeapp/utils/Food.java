package com.example.sankeapp.utils;

import static com.example.sankeapp.utils.Constants.POINTSIZE;

import java.util.Random;

public class Food {

    public static Coordinates getRandomFood(int surfaceWidth, int surfaceHeight) {
        Coordinates randomCoordinates = new Coordinates();
        randomCoordinates.setPositionX(new Random().nextInt(surfaceWidth / POINTSIZE));
        randomCoordinates.setPositionY(new Random().nextInt(surfaceHeight / POINTSIZE));

        if (randomCoordinates.getPositionX() % 2 != 0) {
            randomCoordinates.increaseCoordinateX();
        }
        if (randomCoordinates.getPositionY() % 2 != 0) {
            randomCoordinates.increaseCoordinateY();
        }
        return randomCoordinates;
    }
}
