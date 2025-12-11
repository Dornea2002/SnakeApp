package com.example.sankeapp.utils;

public class Coordinates {
    private int positionX;
    private int positionY;

    public Coordinates(int positionX, int positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public Coordinates() {
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public void increaseCoordinateX(){
        this.positionX ++;
    }

    public void increaseCoordinateY(){
        this.positionY ++;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return positionX == that.positionX && positionY == that.positionY;
    }
}
