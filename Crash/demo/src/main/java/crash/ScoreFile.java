package crash;

import java.io.*;

public class ScoreFile {
    private String TOP_SCORE_FILE;

    public ScoreFile(String path) {
        this.TOP_SCORE_FILE = path;
        // Crea il file se non esiste
        File file = new File(TOP_SCORE_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void saveTopScore(int score) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TOP_SCORE_FILE))) {
            writer.println(score);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int loadTopScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TOP_SCORE_FILE))) {
            String line = reader.readLine();
            if (line != null) {
                return Integer.parseInt(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0; // Ritorna 0 se il file non esiste o non può essere letto
    }
}