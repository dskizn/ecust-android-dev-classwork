package com.example.myapplication;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NumberPuzzleActivity extends AppCompatActivity {
    private GridLayout puzzleGrid;
    private TextView movesTextView;
    private TextView timerTextView;
    private TextView gameStatusText;
    private Button startButton;
    private Button restartButton;

    private int moveCount = 0;
    private long startTime;
    private boolean gameStarted = false;
    private boolean gameCompleted = false;
    private Handler timerHandler;
    private Runnable timerRunnable;

    private static final int GRID_SIZE = 4;
    private List<Integer> numbers;
    private int emptyPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_number_puzzle);

        CustomToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("数字拼图游戏");
        toolbar.showBackButton(true);
        toolbar.setBackButtonClickListener(this::finish);

        puzzleGrid = findViewById(R.id.puzzle_grid);
        movesTextView = findViewById(R.id.moves_text);
        timerTextView = findViewById(R.id.timer_text);
        gameStatusText = findViewById(R.id.game_status_text);
        startButton = findViewById(R.id.start_button);
        restartButton = findViewById(R.id.restart_button);

        timerHandler = new Handler(Looper.getMainLooper());
        initializeCompletedPuzzle();

        startButton.setOnClickListener(v -> startGame());
        restartButton.setOnClickListener(v -> resetGame());
    }

    private void initializeCompletedPuzzle() {
        numbers = new ArrayList<>();
        for (int i = 1; i <= GRID_SIZE * GRID_SIZE - 1; i++) {
            numbers.add(i);
        }
        numbers.add(0);
        emptyPosition = GRID_SIZE * GRID_SIZE - 1;
        updateGrid();
        disableAllButtons();
    }

    private void startGame() {
        shufflePuzzle();
        gameStarted = true;
        gameCompleted = false;
        moveCount = 0;
        startTime = System.currentTimeMillis();

        updateGrid();
        enableMovableButtons();
        updateMovesText();
        startTimer();

        startButton.setEnabled(false);
        restartButton.setEnabled(true);
        gameStatusText.setText("游戏进行中...");
        gameStatusText.setTextColor(getResources().getColor(android.R.color.black));
    }

    private void shufflePuzzle() {
        initializeCompletedPuzzle();
        int shuffleMoves = 100 + (int)(Math.random() * 101);
        for (int i = 0; i < shuffleMoves; i++) {
            makeRandomMove();
        }
    }

    private void makeRandomMove() {
        int emptyRow = emptyPosition / GRID_SIZE;
        int emptyCol = emptyPosition % GRID_SIZE;
        List<Integer> possibleMoves = new ArrayList<>();

        if (emptyRow > 0) possibleMoves.add((emptyRow - 1) * GRID_SIZE + emptyCol);
        if (emptyRow < GRID_SIZE - 1) possibleMoves.add((emptyRow + 1) * GRID_SIZE + emptyCol);
        if (emptyCol > 0) possibleMoves.add(emptyRow * GRID_SIZE + (emptyCol - 1));
        if (emptyCol < GRID_SIZE - 1) possibleMoves.add(emptyRow * GRID_SIZE + (emptyCol + 1));

        if (!possibleMoves.isEmpty()) {
            int randomMove = possibleMoves.get((int)(Math.random() * possibleMoves.size()));
            Collections.swap(numbers, emptyPosition, randomMove);
            emptyPosition = randomMove;
        }
    }

    private void resetGame() {
        stopTimer();
        gameStarted = false;
        gameCompleted = false;
        moveCount = 0;

        initializeCompletedPuzzle();
        updateMovesText();
        timerTextView.setText("时间: 0s");

        startButton.setEnabled(true);
        restartButton.setEnabled(false);
        gameStatusText.setText("拼图已完成！点击开始游戏");
        gameStatusText.setTextColor(getResources().getColor(R.color.green_700));
    }

    private void updateGrid() {
        puzzleGrid.removeAllViews();

        for (int i = 0; i < numbers.size(); i++) {
            Button numberButton = new Button(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 0;
            params.columnSpec = GridLayout.spec(i % GRID_SIZE, 1f);
            params.rowSpec = GridLayout.spec(i / GRID_SIZE, 1f);
            params.setMargins(2, 2, 2, 2);
            numberButton.setLayoutParams(params);

            final int number = numbers.get(i);
            final int position = i;

            if (number != 0) {
                numberButton.setText(String.valueOf(number));
                numberButton.setTextSize(20);
                numberButton.setAllCaps(false);
                numberButton.setBackgroundResource(R.drawable.puzzle_button_bg);

                if (gameStarted && !gameCompleted) {
                    numberButton.setOnClickListener(v -> moveNumber(position));
                }
            } else {
                numberButton.setText("");
                numberButton.setBackgroundResource(R.drawable.puzzle_empty_bg);
                numberButton.setEnabled(false);
            }

            puzzleGrid.addView(numberButton);
        }
    }

    private void moveNumber(int position) {
        if (gameCompleted) return;

        int row = position / GRID_SIZE;
        int col = position % GRID_SIZE;
        int emptyRow = emptyPosition / GRID_SIZE;
        int emptyCol = emptyPosition % GRID_SIZE;

        if ((Math.abs(row - emptyRow) == 1 && col == emptyCol) ||
                (Math.abs(col - emptyCol) == 1 && row == emptyRow)) {

            Collections.swap(numbers, position, emptyPosition);
            emptyPosition = position;
            moveCount++;
            updateMovesText();
            updateGrid();

            if (isPuzzleSolved()) {
                gameCompleted = true;
                stopTimer();
                long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                gameStatusText.setText("恭喜！完成拼图！\n步数: " + moveCount + " 时间: " + elapsedTime + "秒");
                gameStatusText.setTextColor(getResources().getColor(R.color.green_700));
                Toast.makeText(this, "拼图完成！", Toast.LENGTH_SHORT).show();
                disableAllButtons();
            }
        }
    }

    private void enableMovableButtons() {
        for (int i = 0; i < puzzleGrid.getChildCount(); i++) {
            Button button = (Button) puzzleGrid.getChildAt(i);
            if (button.getText().length() > 0) {
                final int position = i;
                button.setOnClickListener(v -> moveNumber(position));
            }
        }
    }

    private void disableAllButtons() {
        for (int i = 0; i < puzzleGrid.getChildCount(); i++) {
            Button button = (Button) puzzleGrid.getChildAt(i);
            button.setOnClickListener(null);
        }
    }

    private void updateMovesText() {
        movesTextView.setText("步数: " + moveCount);
    }

    private void startTimer() {
        stopTimer();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (gameStarted && !gameCompleted) {
                    long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                    timerTextView.setText("时间: " + elapsedTime + "s");
                    timerHandler.postDelayed(this, 1000);
                }
            }
        };
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    private void stopTimer() {
        if (timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    private boolean isPuzzleSolved() {
        for (int i = 0; i < numbers.size() - 1; i++) {
            if (numbers.get(i) != i + 1) {
                return false;
            }
        }
        return numbers.get(numbers.size() - 1) == 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}