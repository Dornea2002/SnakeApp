package com.example.sankeapp.screens.fragments;

import static com.example.sankeapp.utils.Constants.DEFAULTSNAKESIZE;
import static com.example.sankeapp.utils.Constants.DEFAULTSNAKESPEED;
import static com.example.sankeapp.utils.Constants.POINTSIZE;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import com.example.sankeapp.models.MovingPositions;
import com.example.sankeapp.utils.Food;
import com.example.sankeapp.utils.SnakeLogic;

import java.util.ArrayList;
import java.util.List;
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
    private ImageButton playPauseButton;
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
    private boolean isPaused = false;


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
        playPauseButton = binding.playPauseButton;
    }

    private void setListeners() {
        surfaceView.getHolder().addCallback(this);
        upButton.setOnClickListener(getListener(MovingPositions.TOP, MovingPositions.DOWN));
        downButton.setOnClickListener(getListener(MovingPositions.DOWN, MovingPositions.TOP));
        leftButton.setOnClickListener(getListener(MovingPositions.LEFT, MovingPositions.RIGHT));
        rightButton.setOnClickListener(getListener(MovingPositions.RIGHT, MovingPositions.LEFT));
        playPauseButton.setOnClickListener(v -> togglePlayPause());
    }

    private void initPaints() {
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

        Coordinates randomCoordinates = Food.getRandomFood(surfaceWidth, surfaceHeight);

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

                SnakeLogic.moveHead(snakeCompozitionList, headCoordinates, movingPosition);


                headCoordinates.setPositionX(snakeCompozitionList.get(0).getPositionX());
                headCoordinates.setPositionY(snakeCompozitionList.get(0).getPositionY());

                if (headCoordinates.getPositionX() == pointCoordinates.getPositionX() &&
                        headCoordinates.getPositionY() == pointCoordinates.getPositionY()) {
                    growSnake();
                    placeRandomPoint();
                    Log.d("here", "GROW");
                }

                if (SnakeLogic.isCollided(snakeCompozitionList, surfaceView.getWidth(), surfaceView.getHeight())) {
                    timer.purge();
                    timer.cancel();

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setMessage("You score " + score.getText() + " points!");
                    builder.setTitle("GAME OVER");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Start Again", (dialog, which) -> initializeSurface());

                    requireActivity().runOnUiThread(builder::show);
                } else {
                    canvas = surfaceHolder.lockCanvas();
                    canvas.drawColor(ContextCompat.getColor(requireContext(), R.color.color_on_primary));

                    /// Move body parts: from tail → previous segment
                    SnakeLogic.moveBody(snakeCompozitionList);

                    /// food draw
                    canvas.drawCircle(
                            pointCoordinates.getPositionX(),
                            pointCoordinates.getPositionY(),
                            POINTSIZE,
                            foodPaint
                    );

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

                    /// head draw
                    canvas.drawCircle(
                            snakeCompozitionList.get(0).getPositionX(),
                            snakeCompozitionList.get(0).getPositionY(),
                            POINTSIZE,
                            headPaint
                    );

                    surfaceHolder.unlockCanvasAndPost(canvas);

                }

            }
        }, 1000 - DEFAULTSNAKESPEED, 1000 - DEFAULTSNAKESPEED);
    }

    private void growSnake() {
        SnakeLogic.growSnake(snakeCompozitionList);
        computingScore+=10;
        requireActivity().runOnUiThread(() -> score.setText(String.valueOf(computingScore)));
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
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void togglePlayPause() {
        if (isPaused) {
            isPaused = false;
            playPauseButton.setImageResource(R.drawable.icon_pause);
            binding.pauseText.setVisibility(View.GONE);
            resumeGame();
        } else {
            isPaused = true;
            playPauseButton.setImageResource(R.drawable.icon_play);
            binding.pauseText.setVisibility(View.VISIBLE);
            pauseGame();
        }
    }

    private void pauseGame() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void resumeGame() {
        moveSnake();
    }
}
