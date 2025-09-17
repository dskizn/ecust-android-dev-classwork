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

        // 设置自定义工具栏
        CustomToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("数字拼图游戏");
        toolbar.showBackButton(true);
        toolbar.setBackButtonClickListener(() -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        puzzleGrid = findViewById(R.id.puzzle_grid);
        movesTextView = findViewById(R.id.moves_text);
        timerTextView = findViewById(R.id.timer_text);
        gameStatusText = findViewById(R.id.game_status_text);
        startButton = findViewById(R.id.start_button);
        restartButton = findViewById(R.id.restart_button);

        // 初始化计时器
        timerHandler = new Handler(Looper.getMainLooper());

        // 初始化游戏（显示完成状态）
        initializeCompletedPuzzle();

        startButton.setOnClickListener(v -> {
            startGame();
        });

        restartButton.setOnClickListener(v -> {
            resetGame();
        });
    }

    private void initializeCompletedPuzzle() {
        numbers = new ArrayList<>();
        for (int i = 1; i <= GRID_SIZE * GRID_SIZE - 1; i++) {
            numbers.add(i);
        }
        numbers.add(0); // 0 表示空位
        emptyPosition = GRID_SIZE * GRID_SIZE - 1;

        updateGrid();
        disableAllButtons();
    }

    private void startGame() {
        // 打乱拼图（确保可解）
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
        int attempts = 0;
        int maxAttempts = 1000; // 防止无限循环

        do {
            // 从完成状态开始，通过随机移动来打乱
            initializeCompletedPuzzle();

            // 随机移动100-200步来打乱
            int shuffleMoves = 100 + (int)(Math.random() * 101);
            for (int i = 0; i < shuffleMoves; i++) {
                makeRandomMove();
            }

            attempts++;
            if (attempts > maxAttempts) {
                // 如果尝试次数过多，使用备用方法
                useAlternativeShuffle();
                break;
            }
        } while (isPuzzleSolved()); // 确保不是已经完成的状态
    }

    private void makeRandomMove() {
        int emptyRow = emptyPosition / GRID_SIZE;
        int emptyCol = emptyPosition % GRID_SIZE;

        List<Integer> possibleMoves = new ArrayList<>();

        // 检查上方的方块
        if (emptyRow > 0) {
            possibleMoves.add((emptyRow - 1) * GRID_SIZE + emptyCol);
        }
        // 检查下方的方块
        if (emptyRow < GRID_SIZE - 1) {
            possibleMoves.add((emptyRow + 1) * GRID_SIZE + emptyCol);
        }
        // 检查左方的方块
        if (emptyCol > 0) {
            possibleMoves.add(emptyRow * GRID_SIZE + (emptyCol - 1));
        }
        // 检查右方的方块
        if (emptyCol < GRID_SIZE - 1) {
            possibleMoves.add(emptyRow * GRID_SIZE + (emptyCol + 1));
        }

        if (!possibleMoves.isEmpty()) {
            // 随机选择一个可移动的方块
            int randomMove = possibleMoves.get((int)(Math.random() * possibleMoves.size()));
            Collections.swap(numbers, emptyPosition, randomMove);
            emptyPosition = randomMove;
        }
    }

    private void useAlternativeShuffle() {
        // 备用方法：使用经典的可解性检查
        do {
            numbers = new ArrayList<>();
            for (int i = 1; i <= GRID_SIZE * GRID_SIZE - 1; i++) {
                numbers.add(i);
            }
            numbers.add(0);
            Collections.shuffle(numbers);
            emptyPosition = numbers.indexOf(0);
        } while (!isSolvable() || isPuzzleSolved());
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
                    numberButton.setOnClickListener(v -> {
                        moveNumber(position);
                    });
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

        // 检查是否与空位相邻
        int row = position / GRID_SIZE;
        int col = position % GRID_SIZE;
        int emptyRow = emptyPosition / GRID_SIZE;
        int emptyCol = emptyPosition % GRID_SIZE;

        if ((Math.abs(row - emptyRow) == 1 && col == emptyCol) ||
                (Math.abs(col - emptyCol) == 1 && row == emptyRow)) {

            // 交换数字
            Collections.swap(numbers, position, emptyPosition);
            emptyPosition = position;
            moveCount++;
            updateMovesText();
            updateGrid();

            // 检查是否完成
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
            if (button.getText().length() > 0) { // 不是空位
                final int position = i;
                button.setOnClickListener(v -> {
                    moveNumber(position);
                });
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
        stopTimer(); // 确保之前的计时器停止

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

    private boolean isSolvable() {
        int inversionCount = 0;
        List<Integer> numbersWithoutZero = new ArrayList<>();

        // 移除0并计算逆序数
        for (int num : numbers) {
            if (num != 0) {
                numbersWithoutZero.add(num);
            }
        }

        // 计算逆序数
        for (int i = 0; i < numbersWithoutZero.size() - 1; i++) {
            for (int j = i + 1; j < numbersWithoutZero.size(); j++) {
                if (numbersWithoutZero.get(i) > numbersWithoutZero.get(j)) {
                    inversionCount++;
                }
            }
        }

        // 对于4x4拼图（偶数网格）
        int emptyRowFromBottom = GRID_SIZE - (emptyPosition / GRID_SIZE);

        if (emptyRowFromBottom % 2 == 0) { // 空位在偶数行（从底部数）
            return inversionCount % 2 == 1;
        } else { // 空位在奇数行（从底部数）
            return inversionCount % 2 == 0;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}