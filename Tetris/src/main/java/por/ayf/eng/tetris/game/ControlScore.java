package por.ayf.eng.tetris.game;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *  Class will carry the control of the scores through a binary file
 * 
 *  @author: Ángel Yagüe Flor.
 *  @version: 1.0 - Stable.
 *  @version: 1.1 - Refactor the project.
 */

public class ControlScore {
	@SuppressWarnings("unused")
	private File currentFile;
	private RandomAccessFile fes;
	private int numberRegisters;
	private int sizeRegistry = 50;
	
	public ControlScore(File file) throws IOException {
		if(file.exists() && !file.isFile())  {
	    	throw new IOException(file.getName() + " no es un fichero");
	    }
		
		currentFile = file;
	    fes = new RandomAccessFile(file, "rw");
	    numberRegisters = (int) Math.ceil((double) fes.length() / (double) sizeRegistry);
	}
	
	/**
	 *  Method will return the current number of registers. 
	 * 
	 *  @return The current number of registers.
	 */
	
	public int getLength() {
		return numberRegisters;
	}
	
	/**
	 *  Method will close the RandomAccessFile.
	 */
	
	public void close() {
		try {
			this.fes.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Score getValueIn(int i) throws IOException {
		// Check the registers are not out of bounds 
	    if(i >= 0 && i < numberRegisters) { 
	        fes.seek(i * sizeRegistry); // Set the pointer of L/E

	        // Have to read and write in the same order. In this case, name - score - lines.
	        String name = fes.readUTF();
	        int score = fes.readInt();
	        int lines = fes.readInt();
	      
	        return new Score(name, score, lines);
	    }
	    else {
	        System.out.println("Número de registro fuera de límites");
	        return null;
	    }
	}
	  
    public void addScore(Score score) throws IOException {
    	// If has been possible set the value in that position, means has been added and will add in one of the registers.
    	if(setValueInt(numberRegisters, score)) { 
    		numberRegisters++;
    	}
    }
    
    public boolean setValueInt(int i, Score score) throws IOException {
    	// Check the registers are not out of bounds
	    if(i >= 0 && i <= numberRegisters) { 
	        if (score.sizeRegistry() + 8 > sizeRegistry) {
	        	System.err.println("Tamaño del registro excedido");
	        }	
	        else {
		        fes.seek(i * sizeRegistry); // Set the pointer of L/E
		        fes.writeUTF(score.getName());
		        fes.writeInt(score.getScore());
		        fes.writeInt(score.getLines());
		        return true;
	        }
	    } 
	    else {
	    	System.out.println("Número de registro fuera de límites");
	    } 
	    return false;
    }
}

