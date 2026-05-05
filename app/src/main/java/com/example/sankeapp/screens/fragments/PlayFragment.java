package com.example.sankeapp.screens.fragments;

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
import androidx.lifecycle.ViewModelProvider;

import com.example.sankeapp.R;
import com.example.sankeapp.databinding.FragmentPlayBinding;
import com.example.sankeapp.screens.viewmodels.PlayFragmentViewModel;
import com.example.sankeapp.utils.Coordinates;
import com.example.sankeapp.models.MovingPositions;

import java.util.List;
import java.util.Timer;

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
    private Timer timer;
    private Paint headPaint;
    private Paint bodyPaint;
    private Paint foodPaint;
    private boolean isPaused = false;
    private PlayFragmentViewModel playFragmentViewModel;
    private static final String TAG = PlayFragment.class.getCanonicalName();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlayBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playFragmentViewModel = new ViewModelProvider(this).get(PlayFragmentViewModel.class);

        initiateUI();
        initPaints();
        setListeners();
        setObservers();
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
        upButton.setOnClickListener(getListener(MovingPositions.TOP));
        downButton.setOnClickListener(getListener(MovingPositions.DOWN));
        leftButton.setOnClickListener(getListener(MovingPositions.LEFT));
        rightButton.setOnClickListener(getListener(MovingPositions.RIGHT));
        playPauseButton.setOnClickListener(v -> togglePlayPause());
    }

    private void setObservers() {
        playFragmentViewModel.getComputingScoreMutableLiveData().observe(getViewLifecycleOwner(),
                integer -> requireActivity().runOnUiThread(() -> score.setText(String.valueOf(integer)))
        );

        playFragmentViewModel.getSnakeCompositionList().observe(getViewLifecycleOwner(),
                newSnakeCompositionList -> draw());

        playFragmentViewModel.getFood().observe(getViewLifecycleOwner(),
                food -> draw());

        playFragmentViewModel.getGameOver().observe(getViewLifecycleOwner(),
                isGameOver -> {
                    if (isGameOver)
                        showGameOverDialog();
                });
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

    private void draw() {
        List<Coordinates> snake = playFragmentViewModel.getSnakeCompositionList().getValue();
        Coordinates food = playFragmentViewModel.getFood().getValue();

        if (snake == null || snake.isEmpty() || food == null || surfaceHolder == null) {
            return;
        }

        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas == null)
            return;
        canvas.drawColor(ContextCompat.getColor(requireContext(), R.color.color_on_primary));

        ///FOOD
        canvas.drawCircle(
                food.getPositionX(),
                food.getPositionY(),
                POINTSIZE,
                foodPaint
        );

        /// BODY
        for (int i = 1; i < snake.size(); i++) {
            Coordinates cell = snake.get(i);
            canvas.drawCircle(
                    cell.getPositionX(),
                    cell.getPositionY(),
                    POINTSIZE,
                    bodyPaint
            );
        }

        ///HEAD
        Coordinates head = snake.get(0);
        canvas.drawCircle(
                head.getPositionX(),
                head.getPositionY(),
                POINTSIZE,
                headPaint
        );

        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    private void showGameOverDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("GAME OVER")
                .setMessage("Score: " + score.getText())
                .setCancelable(false)
                .setPositiveButton("Restart", (d, w) -> playFragmentViewModel.startGame(
                        surfaceView.getWidth(),
                        surfaceView.getHeight()
                ))
                .show();
    }

    private View.OnClickListener getListener(MovingPositions desiredPosition) {
        return view -> playFragmentViewModel.setMovingPosition(desiredPosition);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;

        playFragmentViewModel.startGame(
                surfaceView.getWidth(),
                surfaceView.getHeight()
        );
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
        Log.d(TAG, "Here in toggle");
        if (isPaused) {
            isPaused = false;
            playPauseButton.setImageResource(R.drawable.icon_pause);
            binding.pauseText.setVisibility(View.GONE);
            playFragmentViewModel.resumeGame(
                    surfaceView.getWidth(),
                    surfaceView.getHeight()
            );
        } else {
            isPaused = true;
            playPauseButton.setImageResource(R.drawable.icon_play);
            binding.pauseText.setVisibility(View.VISIBLE);
            playFragmentViewModel.pauseGame();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        playFragmentViewModel.stop();
    }
}
