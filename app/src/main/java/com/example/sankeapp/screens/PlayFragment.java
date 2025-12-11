package com.example.sankeapp.screens;

import static com.example.sankeapp.utils.Constants.DEFAULTSNAKESIZE;
import static com.example.sankeapp.utils.Constants.DEFAULTSNAKESPEED;
import static com.example.sankeapp.utils.Constants.POINTSIZE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.sankeapp.R;
import com.example.sankeapp.databinding.FragmentPlayBinding;
import com.example.sankeapp.utils.Coordinates;
import com.example.sankeapp.utils.MovingPositions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
    private List<Coordinates> snakeCompozitionList = new ArrayList<>();
    private int computingScore;
    private Coordinates pointCoordinates;
    private Coordinates headCoordinates;
    private Timer timer;
    private Canvas canvas = null;
    private Paint headPaint;
    private Paint bodyPaint;
    private Paint foodPaint;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlayBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initiateUI();
        initPaints();
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

    private void setListeners() {
        surfaceView.getHolder().addCallback(this);
        upButton.setOnClickListener(getListener(MovingPositions.TOP, MovingPositions.DOWN));
        downButton.setOnClickListener(getListener(MovingPositions.DOWN, MovingPositions.TOP));
        leftButton.setOnClickListener(getListener(MovingPositions.LEFT, MovingPositions.RIGHT));
        rightButton.setOnClickListener(getListener(MovingPositions.RIGHT, MovingPositions.LEFT));
    }

    private void initPaints(){
        headPaint = new Paint();
        headPaint.setStyle(Paint.Style.FILL);
        headPaint.setAntiAlias(true);
        headPaint.setColor(ContextCompat.getColor(requireContext(), R.color.color_on_tertiary));

        bodyPaint = new Paint();
        bodyPaint.setStyle(Paint.Style.FILL);
        bodyPaint.setAntiAlias(true);
        bodyPaint.setColor(ContextCompat.getColor(requireContext(), R.color.color_on_secondary));

        foodPaint = new Paint();
        foodPaint.setStyle(Paint.Style.FILL);
        foodPaint.setAntiAlias(true);
        foodPaint.setColor(ContextCompat.getColor(requireContext(), R.color.color_on_quartery));
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
            Coordinates snakeCoordinates = new Coordinates(startPositionX, POINTSIZE);
            snakeCompozitionList.add(snakeCoordinates);

            startPositionX -= (POINTSIZE * 2);
        }

        pointCoordinates = new Coordinates();
        headCoordinates = new Coordinates();

        placeRandomPoint();

        moveSnake();
    }

    private void placeRandomPoint() {
        int surfaceWidth = surfaceView.getWidth() - (POINTSIZE * 2);
        int surfaceHeight = surfaceView.getHeight() - (POINTSIZE * 2);

        Coordinates randomCoordinates = new Coordinates();
        randomCoordinates.setPositionX(new Random().nextInt(surfaceWidth / POINTSIZE));
        randomCoordinates.setPositionY(new Random().nextInt(surfaceHeight / POINTSIZE));

        if (randomCoordinates.getPositionX() % 2 != 0) {
            randomCoordinates.increaseCoordinateX();
        }
        if (randomCoordinates.getPositionY() % 2 != 0) {
            randomCoordinates.increaseCoordinateY();
        }

        pointCoordinates.setPositionX((randomCoordinates.getPositionX() + 1) * POINTSIZE);
        pointCoordinates.setPositionY((randomCoordinates.getPositionY() + 1) * POINTSIZE);

    }

    private void moveSnake() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                headCoordinates.setPositionX(snakeCompozitionList.get(0).getPositionX());
                headCoordinates.setPositionY(snakeCompozitionList.get(0).getPositionY());

                switch (movingPosition) {
                    case RIGHT:
                        snakeCompozitionList.get(0).setPositionX(headCoordinates.getPositionX() + (POINTSIZE * 2));
                        snakeCompozitionList.get(0).setPositionY(headCoordinates.getPositionY());
                        break;
                    case LEFT:
                        snakeCompozitionList.get(0).setPositionX(headCoordinates.getPositionX() - (POINTSIZE * 2));
                        snakeCompozitionList.get(0).setPositionY(headCoordinates.getPositionY());
                        break;
                    case TOP:
                        snakeCompozitionList.get(0).setPositionX(headCoordinates.getPositionX());
                        snakeCompozitionList.get(0).setPositionY(headCoordinates.getPositionY() - (POINTSIZE * 2));
                        break;
                    case DOWN:
                        snakeCompozitionList.get(0).setPositionX(headCoordinates.getPositionX());
                        snakeCompozitionList.get(0).setPositionY(headCoordinates.getPositionY() + (POINTSIZE * 2));
                        break;
                }

                headCoordinates.setPositionX(snakeCompozitionList.get(0).getPositionX());
                headCoordinates.setPositionY(snakeCompozitionList.get(0).getPositionY());

                if (headCoordinates.getPositionX() == pointCoordinates.getPositionX() &&
                        headCoordinates.getPositionY() == pointCoordinates.getPositionY()) {
                    growSnake();
                    placeRandomPoint();
                    Log.d("here", "GROW");
                }

                if (gameOverCheck()) {
                    timer.purge();
                    timer.cancel();

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setMessage("You score " + score.getText() + " points!");
                    builder.setTitle("GAME OVER");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Start Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            initializeSurface();
                        }
                    });

                    requireActivity().runOnUiThread(builder::show);
                } else {
                    canvas = surfaceHolder.lockCanvas();
                    canvas.drawColor(ContextCompat.getColor(requireContext(), R.color.color_on_primary));
                    /// snake draw
                    canvas.drawCircle(
                            snakeCompozitionList.get(0).getPositionX(),
                            snakeCompozitionList.get(0).getPositionY(),
                            POINTSIZE,
                            headPaint
                    );

                    /// food draw
                    canvas.drawCircle(
                            pointCoordinates.getPositionX(),
                            pointCoordinates.getPositionY(),
                            POINTSIZE,
                            foodPaint
                    );

                    /// Move body parts: from tail → previous segment
                    for (int i = snakeCompozitionList.size() - 1; i > 0; i--) {
                        snakeCompozitionList.get(i).setPositionX(snakeCompozitionList.get(i - 1).getPositionX());
                        snakeCompozitionList.get(i).setPositionY(snakeCompozitionList.get(i - 1).getPositionY());
                    }

                    /// Draw body
                    for (int i = 1; i < snakeCompozitionList.size(); i++) {
                        Coordinates coordinates = snakeCompozitionList.get(i);
                        canvas.drawCircle(
                                coordinates.getPositionX(),
                                coordinates.getPositionY(),
                                POINTSIZE,
                                bodyPaint
                        );
                    }


                    surfaceHolder.unlockCanvasAndPost(canvas);

                }

            }
        }, 1000 - DEFAULTSNAKESPEED, 1000 - DEFAULTSNAKESPEED);
    }

    private void growSnake() {
        Coordinates tail = snakeCompozitionList.get(snakeCompozitionList.size() - 1);
        Coordinates newPart = new Coordinates(tail.getPositionX(), tail.getPositionY());
        snakeCompozitionList.add(newPart);
        computingScore++;
        requireActivity().runOnUiThread(() -> score.setText(String.valueOf(computingScore)));
    }

    private boolean gameOverCheck() {
        boolean gameOver = false;

        if (snakeCompozitionList.get(0).getPositionX() < 0 ||
                snakeCompozitionList.get(0).getPositionY() < 0 ||
                snakeCompozitionList.get(0).getPositionX() >= surfaceView.getWidth() ||
                snakeCompozitionList.get(0).getPositionY() >= surfaceView.getHeight()) {
            gameOver = true;
        } else {
            for (int i = 1; i < snakeCompozitionList.size(); i++) {
                if (snakeCompozitionList.get(0).getPositionX() == snakeCompozitionList.get(i).getPositionX() &&
                        snakeCompozitionList.get(0).getPositionY() == snakeCompozitionList.get(i).getPositionY()) {
                    gameOver = true;
                    break;
                }
            }
        }


        return gameOver;
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
