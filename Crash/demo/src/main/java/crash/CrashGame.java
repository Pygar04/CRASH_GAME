package crash;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CrashGame implements Runnable {
    private GameBoard board;
    private JFrame frame;
    private ExecutorService executor;
    private boolean running = true;
    private final Map map = new Map();

    public CrashGame() {
        setupGame();
    }

    private void setupGame() {
        frame = new JFrame("Crash Game");
        board = new GameBoard(map);
        frame.add(board);
        frame.setSize(map.getWidthMap(), map.getHeighMap());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        executor = Executors.newSingleThreadExecutor();
        executor.execute(this);
    }

    @Override
    public void run() {
        while (running) {
            board.updateGame();
            try {
                Thread.sleep(16); // Approx. 60fps
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Interrompe il thread corrente
                running = false;
            }
        }
        stopGame();
    }

    public void stopGame() {
        if (board.stopGame() && !executor.isShutdown()) {
            executor.shutdownNow(); // Stop all running tasks
        }
    }
}
