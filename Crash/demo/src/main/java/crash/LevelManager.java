package crash;

public class LevelManager {
    private int currentLevel;
    private GameBoard gameBoard;
    private final int MAX_LEVEL = 3;

    public LevelManager(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.currentLevel = 1;
    }    
}
