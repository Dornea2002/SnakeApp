package com.example.sankeapp.screens;

import static com.example.sankeapp.utils.Constants.DEFAULTSNAKESIZE;
import static com.example.sankeapp.utils.Constants.POINTSIZE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sankeapp.databinding.FragmentPlayBinding;
import com.example.sankeapp.utils.Coordinates;
import com.example.sankeapp.utils.MovingPositions;
import com.example.sankeapp.utils.SnakeCompozition;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayFragment extends Fragment implements SurfaceHolder.Callback {

    private FragmentPlayBinding binding;
    private TextView score;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private ImageButton upButton;
    private ImageButton downButton;
    private ImageButton leftButton;
    private ImageButton rightButton;
    private MovingPositions movingPosition = MovingPositions.RIGHT;
    private List<SnakeCompozition> snakeCompozitionList = new ArrayList<>();
    private int computingScore;
    private Coordinates coordinates;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlayBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initiateUI();
        setListeners();
    }

    private void initiateUI() {
        score = binding.score;
        surfaceView = binding.snakeSurface;
        upButton = binding.top;
        downButton = binding.bottom;
        leftButton = binding.left;
        rightButton = binding.right;
    }

    /// TO DO
    private void setListeners() {
        surfaceView.getHolder().addCallback(null);
        upButton.setOnClickListener(getListener(MovingPositions.TOP, MovingPositions.DOWN));
        downButton.setOnClickListener(getListener(MovingPositions.DOWN, MovingPositions.TOP));
        leftButton.setOnClickListener(getListener(MovingPositions.LEFT, MovingPositions.RIGHT));
        rightButton.setOnClickListener(getListener(MovingPositions.RIGHT, MovingPositions.LEFT));
    }

    private View.OnClickListener getListener(MovingPositions desiredPosition, MovingPositions forbiddenPosition) {
        return view -> {
            if (!movingPosition.equals(forbiddenPosition)) {
                movingPosition = desiredPosition;
            }
        };
    }

    private void initializeSurface() {
        snakeCompozitionList.clear();
        score.setText("0");
        computingScore = 0;
        movingPosition = MovingPositions.RIGHT;

        int startPositionX = POINTSIZE * DEFAULTSNAKESIZE;
        for (int i = 0; i <= DEFAULTSNAKESIZE; i++) {
            SnakeCompozition snakeCompozition = new SnakeCompozition(startPositionX, POINTSIZE);
            snakeCompozitionList.add(snakeCompozition);

            startPositionX -= (POINTSIZE * 2);
        }

        placeRandomPoint();

        moveSnake();
    }

    private void placeRandomPoint() {
        int surfaceWidth = surfaceView.getWidth() - (POINTSIZE * 2);
        int surfaceHeight = surfaceView.getHeight() - (POINTSIZE * 2);

        Coordinates randomCoordinates = new Coordinates();
        randomCoordinates.positionX = new Random().nextInt(surfaceWidth / POINTSIZE);
        randomCoordinates.positionY = new Random().nextInt(surfaceHeight / POINTSIZE);

        if (randomCoordinates.positionX % 2 != 0) {
            randomCoordinates.positionX++;
        }
        if (randomCoordinates.positionY % 2 != 0) {
            randomCoordinates.positionY++;
        }

        coordinates.positionX = (randomCoordinates.positionX + 1) * POINTSIZE;
        coordinates.positionY = (randomCoordinates.positionY + 1) * POINTSIZE;

    }

    private void moveSnake() {

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;

        initializeSurface();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }
}
