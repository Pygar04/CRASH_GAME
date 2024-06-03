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
    private Menu menu;

    public CrashGame() {
        setupGame();
    }

    private void setupGame() {
        frame = new JFrame("Crash Game");
        menu = new Menu(this);
        frame.add(menu);
        frame.setSize(map.getWidthMap(), map.getHeighMap());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void startGame() {
        frame.remove(menu);
        board = new GameBoard(map);
        frame.add(board);
        frame.revalidate();
        frame.repaint();

        board.requestFocusInWindow();
        executor = Executors.newSingleThreadExecutor();
        executor.execute(this);
    }

    @Override
    public void run() {
        while (running) {
            if (board != null) {
                board.updateGame();
            }
            try {
                Thread.sleep(16); // Approx. 60fps
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Interrupt the current thread
                running = false;
            }
        }
        stopGame();
    }

    public void stopGame() {
        if (board != null && board.stopGame() && !executor.isShutdown()) {
            executor.shutdownNow(); // Stop all running tasks
        }
    }
}
