package com.example.sankeapp.screens.viewmodels;

import static com.example.sankeapp.utils.Constants.INITIAL_SCORE_VALUE;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sankeapp.models.MovingPositions;
import com.example.sankeapp.utils.Coordinates;
import com.example.sankeapp.utils.Food;
import com.example.sankeapp.utils.SnakeLogic;
import com.example.sankeapp.utils.Time;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PlayFragmentViewModel extends ViewModel {

    private final MutableLiveData<Time> chronometerMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> eatenFoodCounterMutableLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> computingScoreMutableLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<MovingPositions> movingPositionMutableLiveData = new MutableLiveData<>(MovingPositions.RIGHT);
    private final MutableLiveData<List<Coordinates>> snakeCompositionListLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Coordinates> foodLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> gameOverLiveData = new MutableLiveData<>(false);
    private Timer timer;
    private long startTime;
    private long elapsedTime;

    public LiveData<Time> getChronometerLiveData() {
        return chronometerMutableLiveData;
    }

    public LiveData<Integer> getEatenFoodCounterMutableLiveData() {
        return eatenFoodCounterMutableLiveData;

    }

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

    public long getElapsedTime() {
        if (timer == null) {
            return elapsedTime;
        } else return elapsedTime + (System.currentTimeMillis() - startTime);
    }

    public void startGame(int width, int height) {
        stop();

        startTime = System.currentTimeMillis();
        elapsedTime = 0;

        eatenFoodCounterMutableLiveData.setValue(INITIAL_SCORE_VALUE);
        computingScoreMutableLiveData.setValue(INITIAL_SCORE_VALUE);
        movingPositionMutableLiveData.setValue(MovingPositions.RIGHT);

        snakeCompositionListLiveData.setValue(SnakeLogic.createInitialSnake());
        foodLiveData.setValue(Food.getRandomFood(width, height));

        startLoop(width, height);
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
        updateTime();

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
            increaseFoodEatenCounter();
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

    private void updateTime() {
        Time time = new Time();
        long milliseconds = getElapsedTime() / 1000;
        time.setMinutes(milliseconds / 60);
        time.setSeconds(milliseconds % 60);
        chronometerMutableLiveData.postValue(time);
    }

    public void increaseFoodEatenCounter() {
        if (eatenFoodCounterMutableLiveData.getValue() != null)
            eatenFoodCounterMutableLiveData.postValue(eatenFoodCounterMutableLiveData.getValue() + 1);
    }

    public void increaseComputingScore() {

        if (computingScoreMutableLiveData.getValue() != null)
            computingScoreMutableLiveData.postValue(
                    eatenFoodCounterMutableLiveData == null ||
                            eatenFoodCounterMutableLiveData.getValue() == null ||
                            chronometerMutableLiveData.getValue() == null ||
                            chronometerMutableLiveData.getValue().getMinutes() == null ||
                            chronometerMutableLiveData.getValue().getSeconds() == null
                            ? 0 :
                            (int) ((eatenFoodCounterMutableLiveData.getValue() * 50) +
                                    chronometerMutableLiveData.getValue().getMinutes() * 100 +
                                    chronometerMutableLiveData.getValue().getSeconds() * 75)
            );
    }

    public void setMovingPosition(MovingPositions newMovingPosition) {
        MovingPositions currentDirection = movingPositionMutableLiveData.getValue();
        if (currentDirection == null)
            return;

        switch (newMovingPosition) {
            case TOP:
                if (currentDirection == MovingPositions.DOWN)
                    return;
                break;
            case RIGHT:
                if (currentDirection == MovingPositions.LEFT)
                    return;
                break;
            case DOWN:
                if (currentDirection == MovingPositions.TOP)
                    return;
                break;
            case LEFT:
                if (currentDirection == MovingPositions.RIGHT)
                    return;
                break;
        }
        movingPositionMutableLiveData.postValue(newMovingPosition);
    }

    public void pauseGame() {

        stop();
        elapsedTime += System.currentTimeMillis() - startTime;
    }

    public void resumeGame(int width, int height) {
        startTime = System.currentTimeMillis();
        startLoop(width, height);
    }
}
