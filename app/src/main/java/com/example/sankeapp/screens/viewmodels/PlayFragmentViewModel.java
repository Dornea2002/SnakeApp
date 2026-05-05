package com.example.sankeapp.screens.viewmodels;

import static com.example.sankeapp.utils.Constants.INCREASE_SCORE_VALUE;
import static com.example.sankeapp.utils.Constants.INITIAL_SCORE_VALUE;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sankeapp.models.MovingPositions;
import com.example.sankeapp.utils.Coordinates;
import com.example.sankeapp.utils.Food;
import com.example.sankeapp.utils.SnakeLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PlayFragmentViewModel extends ViewModel {

    private final MutableLiveData<Integer> computingScoreMutableLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<MovingPositions> movingPositionMutableLiveData = new MutableLiveData<>(MovingPositions.RIGHT);
    private final MutableLiveData<List<Coordinates>> snakeCompositionListLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Coordinates> foodLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> gameOverLiveData = new MutableLiveData<>(false);
    private Timer timer;

    public LiveData<Integer> getComputingScoreMutableLiveData() {
        return computingScoreMutableLiveData;
    }

    public LiveData<List<Coordinates>> getSnakeCompositionList() {
        return snakeCompositionListLiveData;
    }

    public LiveData<Coordinates> getFood() {
        return foodLiveData;
    }

    public LiveData<Boolean> getGameOver() {
        return gameOverLiveData;
    }

    public void startGame(int witdh, int height) {
        stop();

        computingScoreMutableLiveData.setValue(INITIAL_SCORE_VALUE);
        movingPositionMutableLiveData.setValue(MovingPositions.RIGHT);

        snakeCompositionListLiveData.setValue(SnakeLogic.createInitialSnake());
        foodLiveData.setValue(Food.getRandomFood(witdh, height));

        startLoop(witdh, height);
    }

    private void startLoop(int width, int height) {
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateMovement(width, height);
            }
        }, 0, 180);
    }

    private void updateMovement(int width, int height) {
        List<Coordinates> current = snakeCompositionListLiveData.getValue();
        if (current == null || current.isEmpty()) return;

        MovingPositions direction = movingPositionMutableLiveData.getValue();
        if (direction == null)
            return;

        List<Coordinates> moved = SnakeLogic.move(current, direction);

        Coordinates head = moved.get(0);
        Coordinates foodPosition = foodLiveData.getValue();
        if (foodPosition == null)
            return;

        if (SnakeLogic.isEating(head, foodPosition)) {
            moved = SnakeLogic.growSnake(moved);
            increaseComputingScore();
            foodLiveData.postValue(Food.getRandomFood(width, height));
        }

        if (SnakeLogic.isCollision(moved, width, height)) {
            gameOverLiveData.postValue(true);
            stop();
            return;
        }

        snakeCompositionListLiveData.postValue(moved);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void increaseComputingScore() {
        if (computingScoreMutableLiveData.getValue() != null)
            computingScoreMutableLiveData.postValue(computingScoreMutableLiveData.getValue() + INCREASE_SCORE_VALUE);
    }

    public void setMovingPosition(MovingPositions newMovingPosition) {
        MovingPositions currentDirection = movingPositionMutableLiveData.getValue();
        if(currentDirection == null)
            return;

        switch(newMovingPosition) {
            case TOP:
                if(currentDirection == MovingPositions.DOWN)
                    return;
                break;
            case RIGHT:
                if(currentDirection == MovingPositions.LEFT)
                    return;
                break;
            case DOWN:
                if(currentDirection == MovingPositions.TOP)
                    return;
                break;
            case LEFT:
                if(currentDirection == MovingPositions.RIGHT)
                    return;
                break;
        }
        movingPositionMutableLiveData.postValue(newMovingPosition);
    }

    public void pauseGame(){
        stop();
    }

    public void resumeGame(int width, int height){
        startLoop(width, height);
    }
}
