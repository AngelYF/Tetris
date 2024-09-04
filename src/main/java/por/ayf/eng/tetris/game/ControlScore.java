package por.ayf.eng.tetris.game;

import por.ayf.eng.tetris.util.Util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Class will carry the control of the scores through a binary file
 *
 * @author: Ángel Yagüe Flor.
 * @version: 1.0 - Stable.
 * @version: 1.1 - Refactor the project.
 */

public class ControlScore {
    @SuppressWarnings("unused")
    private final File currentFile;
    private final RandomAccessFile fes;
    private final int sizeRegistry = 50;
    private int numberRegisters;

    public ControlScore(File file) throws IOException {
        if (file.exists() && !file.isFile()) {
            throw new IOException(file.getName() + " no es un fichero");
        }

        currentFile = file;
        fes = new RandomAccessFile(file, "rw");
        numberRegisters = (int) Math.ceil((double) fes.length() / (double) sizeRegistry);
    }

    /**
     * Method will return the current number of registers.
     *
     * @return The current number of registers.
     */

    public int getLength() {
        return numberRegisters;
    }

    /**
     * Method will close the RandomAccessFile.
     */

    public void close() {
        try {
            this.fes.close();
        } catch (IOException ex) {
            Util.logMessage(Util.LEVEL_ERROR, "Ha ocurrido un error al cerrar el fichero.", ControlScore.class, ex);
        }
    }

    public Score getValueIn(int i) throws IOException {
        // Check the registers are not out of bounds
        if (i >= 0 && i < numberRegisters) {
            fes.seek((long) i * sizeRegistry); // Set the pointer of L/E

            // Have to read and write in the same order. In this case, name - score - lines.
            String name = fes.readUTF();
            int score = fes.readInt();
            int lines = fes.readInt();

            return new Score(name, score, lines);
        } else {
            Util.logMessage(Util.LEVEL_WARN, "Número de registro fuera de límites.", ControlScore.class, null);
            return null;
        }
    }

    public void addScore(Score score) throws IOException {
        // If has been possible set the value in that position, means has been added and will add in one of the registers.
        if (setValueInt(numberRegisters, score)) {
            numberRegisters++;
        }
    }

    public boolean setValueInt(int i, Score score) throws IOException {
        // Check the registers are not out of bounds
        if (i >= 0 && i <= numberRegisters) {
            if (score.sizeRegistry() + 8 > sizeRegistry) {
                Util.logMessage(Util.LEVEL_WARN, "Tamaño del registro excedido.", ControlScore.class, null);
            } else {
                fes.seek((long) i * sizeRegistry); // Set the pointer of L/E
                fes.writeUTF(score.getName());
                fes.writeInt(score.getScore());
                fes.writeInt(score.getLines());
                return true;
            }
        } else {
            Util.logMessage(Util.LEVEL_WARN, "Número de registro fuera de límites.", ControlScore.class, null);
        }
        return false;
    }
}

