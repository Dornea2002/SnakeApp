package com.example.sankeapp.utils;

import static com.example.sankeapp.utils.Constants.CELL_SIZE;
import static com.example.sankeapp.utils.Constants.DEFAULTSNAKESIZE;

import com.example.sankeapp.models.MovingPositions;

import java.util.ArrayList;
import java.util.List;

public class SnakeLogic {

    public static List<Coordinates> move(List<Coordinates> snake, MovingPositions direction) {
        List<Coordinates> newSnake = new ArrayList<>();

        Coordinates head = snake.get(0);
        Coordinates newHead = new Coordinates(
                head.getPositionX(),
                head.getPositionY()
        );

        switch (direction) {
            case RIGHT:
                newHead.setPositionX(head.getPositionX() + CELL_SIZE);
                break;
            case LEFT:
                newHead.setPositionX(head.getPositionX() - CELL_SIZE);
                break;
            case TOP:
                newHead.setPositionY(head.getPositionY() - CELL_SIZE);
                break;
            case DOWN:
                newHead.setPositionY(head.getPositionY() + CELL_SIZE);
                break;
        }
        newSnake.add(newHead);

        for (int i = 0; i < snake.size() - 1; i++) {
            Coordinates c = snake.get(i);
            newSnake.add(new Coordinates(c.getPositionX(), c.getPositionY()));
        }
        return newSnake;
    }

    public static boolean isCollision(List<Coordinates> snake, int w, int h) {
        Coordinates head = snake.get(0);
        if (head.getPositionX() < 0 || head.getPositionY() < 0 ||
                head.getPositionX() >=  w || head.getPositionY() >= h) {
            return true;
        }

        for (int i = 1; i < snake.size(); i++) {
            if (head.getPositionX() == snake.get(i).getPositionX() &&
                    head.getPositionY() == snake.get(i).getPositionY()) {
                return true;
            }
        }

        return false;
    }

    public static boolean isEating(Coordinates head, Coordinates food) {
        return head.getPositionX() == food.getPositionX() &&
                head.getPositionY() == food.getPositionY();
    }

    public static List<Coordinates> growSnake(List<Coordinates> snakeCompozitionList) {
        Coordinates tail = snakeCompozitionList.get(snakeCompozitionList.size() - 1);

        List<Coordinates> snakeList = new ArrayList<>(snakeCompozitionList);
        snakeList.add(new Coordinates(tail.getPositionX(), tail.getPositionY()));
        return snakeList;
    }

    public static List<Coordinates> createInitialSnake() {
        List<Coordinates> snakeInitialCoordinates = new ArrayList<>();
        int startPositionX = CELL_SIZE * DEFAULTSNAKESIZE;

        for (int i = 0; i <= DEFAULTSNAKESIZE; i++) {
            Coordinates snakeCoordinates = new Coordinates(startPositionX, CELL_SIZE);
            snakeInitialCoordinates.add(snakeCoordinates);
            startPositionX -= (CELL_SIZE);
        }
        return snakeInitialCoordinates;
    }
}
