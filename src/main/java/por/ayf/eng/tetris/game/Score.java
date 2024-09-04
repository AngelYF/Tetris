package por.ayf.eng.tetris.game;

/**
 * Class will define the score of the player.
 *
 * @author: Ángel Yagüe Flor
 * @version: 1.0 - Stable.
 * @version: 2.1 - Refactor the project.
 */

public class Score {
    private String name;
    private int score;
    private int lines;

    public Score(String name, int score, int lines) {
        this.name = name;
        this.score = score;
        this.lines = lines;
    }

    /**
     * Method will return the name of the player.
     *
     * @return Name of the player.
     */

    public String getName() {
        return name;
    }

    /**
     * Method will set the new name to the player.
     *
     * @param name New name of the player.
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method will return the score of the player.
     *
     * @return Score of the player.
     */

    public int getScore() {
        return score;
    }

    /**
     * Method will set the new score to the player.
     *
     * @param score New score of the player.
     */

    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Method will return the lines of the player.
     *
     * @return Lines of the player.
     */

    public int getLines() {
        return lines;
    }

    /**
     * Method will set the new lines to the player.
     *
     * @param lines New Lines of the player.
     */

    public void setLines(int lines) {
        this.lines = lines;
    }

    /**
     * Method will return the size of the registry of the Score in bytes.
     *
     * @return Size of the registry of the Score.
     */

    public int sizeRegistry() {
        return (this.name.length() * 2) + 4 + 4; // The type int ocupan 4 bytes cada uno.
    }
}
