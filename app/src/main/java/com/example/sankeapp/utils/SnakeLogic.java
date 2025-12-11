package com.example.sankeapp.utils;

import static com.example.sankeapp.utils.Constants.POINTSIZE;

import com.example.sankeapp.models.MovingPositions;

import java.util.List;

public class SnakeLogic {

    public static void moveHead(List<Coordinates> snakeCompozitionList, Coordinates headCoordinates, MovingPositions movingPosition) {
        switch (movingPosition) {
            case RIGHT:
                snakeCompozitionList.get(0).setPositionX(headCoordinates.getPositionX() + (POINTSIZE * 2));
                break;
            case LEFT:
                snakeCompozitionList.get(0).setPositionX(headCoordinates.getPositionX() - (POINTSIZE * 2));
                break;
            case TOP:
                snakeCompozitionList.get(0).setPositionY(headCoordinates.getPositionY() - (POINTSIZE * 2));
                break;
            case DOWN:
                snakeCompozitionList.get(0).setPositionY(headCoordinates.getPositionY() + (POINTSIZE * 2));
                break;
        }
    }

    public static void moveBody(List<Coordinates> snakeCompozitionList) {
        for (int i = snakeCompozitionList.size() - 1; i > 0; i--) {
            snakeCompozitionList.get(i).setPositionX(snakeCompozitionList.get(i - 1).getPositionX());
            snakeCompozitionList.get(i).setPositionY(snakeCompozitionList.get(i - 1).getPositionY());
        }
    }

    public static void growSnake(List<Coordinates> snakeCompozitionList) {
        Coordinates tail = snakeCompozitionList.get(snakeCompozitionList.size() - 1);
        snakeCompozitionList.add(new Coordinates(tail.getPositionX(), tail.getPositionY()));
    }

    public static boolean isCollided(List<Coordinates> snakeCompozitionList, int surfaceWidth, int surfaceHeight) {

        if (snakeCompozitionList.get(0).getPositionX() < 0 ||
                snakeCompozitionList.get(0).getPositionY() < 0 ||
                snakeCompozitionList.get(0).getPositionX() >= surfaceWidth ||
                snakeCompozitionList.get(0).getPositionY() >= surfaceHeight) {
            return true;
        } else {
            for (int i = 1; i < snakeCompozitionList.size(); i++) {
                if (snakeCompozitionList.get(0).getPositionX() == snakeCompozitionList.get(i).getPositionX() &&
                        snakeCompozitionList.get(0).getPositionY() == snakeCompozitionList.get(i).getPositionY()) {
                    return true;
                }
            }
        }
        return false;
    }
}
