package por.ayf.eng.tetris.game;

import java.awt.Color;

/**
 *  Class will define a piece of the Tetris.
 * 
 *  @author: Ángel Yagüe Flor
 *  @version: 1.0 - Stable.
 *  @version: 2.1 - Refactor the project.
 */

public class Piece {
	private Color colorPiece;	
	private int typePiece;		
	private int state;			
	private int xPosition;			
	private int yPosition;			
	
	public Piece() {
		this.colorPiece = null;
		this.typePiece = -1;
		this.state = 1;
	}
		
	/**
	 *  Method will return the color of the piece.
	 * 
	 *  @return Color of the piece.
	 */
	
	public Color getColorPiece() {
		return colorPiece;
	}

	/** 
	 *  Method will set a new color to the piece.
	 * 
	 *  @param New color of the piece.
	 */
	
	public void setColorPiece(Color colorPiece) {
		this.colorPiece = colorPiece;
	}

	/**
	 *  Method will return the type of the piece.
	 * 
	 *  @return Type of the piece.
	 */
	
	public int getTypePiece() {
		return typePiece;
	}

	/**
	 *  Method will set a new type to the piece.
	 * 
	 *  @param New type of the piece.
	 */
	
	public void setTypePiece(int typePiece) {
		this.typePiece = typePiece;
	}

	/**
	 *  Method will return the state of the piece.
	 * 
	 *  @return The state of the piece.
	 */
	
	public int getState() {
		return state;
	}

	/** 
	 *  Method will set a new state to the piece.
	 * 
	 *  @param New state of the piece.
	 */
	
	public void setState(int state) {
		this.state = state;
	}

	/**
	 *  Method will return the X position of the piece.
	 * 
	 *  @return X position of the piece.
	 */
	
	public int getXPosition() {
		return xPosition;
	}

	/**
	 *  Method will set a new X position to the piece.
	 * 
	 *  @param New X position of the piece.
	 */
	
	public void setXPosition(int xPosition) {
		this.xPosition = xPosition;
	}

	/** 
	 *  Method will return the Y position of the piece.
	 * 
	 *  @return Y position of the piece.
	 */
	
	public int getYPosition() {
		return yPosition;
	}

	/**
	 *  Method will set a new Y position to the piece.
	 * 
	 *  @param New Y position of the piece.
	 */
	
	public void setYPosition(int yPosition) {
		this.yPosition = yPosition;
	}
}
