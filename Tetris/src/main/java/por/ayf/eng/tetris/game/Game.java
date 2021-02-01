package por.ayf.eng.tetris.game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JOptionPane;

import por.ayf.eng.tetris.game.phys.Collision;
import por.ayf.eng.tetris.view.ViewMainWindow;

/**
 *  Canvas where the game happens.
 * 
 *  @author: Ángel Yagüe Flor
 *  @version: 1.0 - Stable.
 *  @version: 2.0 - Add the part of reservation of the game and correction of sizes and sites of elements.
 *  @version: 2.1 - Refactor the project.
 */

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	private ViewMainWindow window;
	
	public static final int COL = 10; 	// Number of columns.
	public static final int ROW = 20; 	// Number of rows.
	public static final int SIZE = 30; 	// Size of a square.
		
	public static final int HIGH_MENU = 20;										
	public static final int BORDER = 5;											
	public static final int HIGH_PANEL = ROW * SIZE;								
	public static final int WIDTH_PANEL = COL * SIZE;							
	public static final int HIGH_SUBPANEL = ROW * SIZE; 							
	public static final int WIDTH_SUBPANEL = ((COL + 2) * SIZE) / 2;				
	private final int DISTANCE_SUBPANEL_X = WIDTH_PANEL + SIZE; 				
	
	private int xPositionCanvas; 	
	private int yPositionCanvas;	
	
	private Thread thread;				
	private boolean playing = true; 		
	private int lines;						
	private int level;						
	private int score;						
	private boolean inPause = false;	
	private String dificult = "normal";	
	
	private boolean pieceInGame = false;
	private Piece piece;					
	private boolean newPiece = true;				
	private Piece nextPiece;				
	
	private boolean firstReservation = false; 	
	private boolean reservation = false;			
	private int numberOfChanges = 0;					
	private Piece reservationPiece;	
	
	private int[][] pieceMatrix = new int[ROW][COL];		
	private int[][] nextMatrix = new int[ROW][COL];	
	private int[][] reservationMatrix = new int[ROW][COL];		
	private int[][] trashMatrix = new int[ROW][COL];		
	private Color[][] colorMatrix = new Color[ROW][COL];
	
	private boolean soundActivated;
	private Clip backgroundMusic;		
	private Clip soundPause;			
	private Clip soundGameOver;		
	private Clip soundMove;			
	private Clip soundFall;			
	private Clip soundLine;			
	private Clip soundLevel;			
	private Clip soundChange;		
	
	private boolean controlScore = false;
	private boolean controlLevel = false;
	private boolean controlLines = false; 
	
	public Game(int xPosition, int yPosition, ViewMainWindow window) {
		this.xPositionCanvas = xPosition;
		this.yPositionCanvas = yPosition;
		this.window = window;
		this.piece = new Piece();
		this.nextPiece = new Piece();
		this.reservationPiece = new Piece();
		this.lines = 0;
		this.level = 1;
		this.score = 0;
		
		this.setBounds(this.xPositionCanvas, 
					   this.yPositionCanvas + HIGH_MENU, 
					   WIDTH_PANEL + SIZE + BORDER * 4 + WIDTH_SUBPANEL, 
					   HIGH_PANEL + BORDER * 2 + 1);
		
		for(int i = 0; i < ROW; i++) {
			for(int j = 0; j < COL; j++) {
				this.pieceMatrix[i][j] = 0;
				this.reservationMatrix[i][j] = 0;
				this.nextMatrix[i][j] = 0;
				this.trashMatrix[i][j] = 0;
				this.colorMatrix[i][j] = null;
			}
		}
		
		// Sound:
		
		this.soundActivated = true;
		
		try {
			this.backgroundMusic = AudioSystem.getClip();
			this.backgroundMusic.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Tetris.wav")));
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		this.backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
		
		try {
			this.soundGameOver = AudioSystem.getClip();
			this.soundGameOver.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/GameOver.wav")));
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// Thread:
		
		this.thread = new Thread(this);
		this.thread.start();
	}
	
	/** 
	 *  Method will draw a square.
	 *  
	 *  @param x: Position will set the square in the X axis.
	 *  @param y: Position will set the square in the Y axis.
	 *  @param color: Color of the square.
	 *  @param graphics: Graphics with we will draw.
	 */
	
	private void square(int x, int y, Color color, Graphics graphics) {
		// Color:
		graphics.setColor(color.darker()); 
		graphics.fillRect(x * SIZE + BORDER + 4, 
						 y * SIZE + BORDER + 4, 
						 SIZE - 7, 
						 SIZE - 7);
		
		for(int i = 1; i < 4; i++) {
			// Light lines:
			graphics.setColor(color); // Horizontal
			graphics.drawLine(x * SIZE + BORDER + i, 
							 y * SIZE + BORDER + i, 
							 x * SIZE + BORDER + SIZE - i, 
							 y * SIZE + BORDER + i);
			
			graphics.setColor(color); // Vertical
			graphics.drawLine(x * SIZE + BORDER + i, 
							 y * SIZE + BORDER + i, 
							 x * SIZE + BORDER + i, 
							 y * SIZE + BORDER + SIZE - i);
			
			// Dark lines:
			graphics.setColor(color.darker().darker()); // Horizontal
			graphics.drawLine(x * SIZE + BORDER + i, 
							 y * SIZE + BORDER + SIZE - i, 
							 x * SIZE + BORDER + SIZE - i, 
							 y * SIZE + BORDER + SIZE - i);
			
			graphics.setColor(color.darker().darker()); // Vertical
			graphics.drawLine(x * SIZE + BORDER + SIZE - i, 
							 y * SIZE + BORDER + i, 
							 x * SIZE + BORDER + SIZE - i, 
							 y * SIZE + BORDER + SIZE - i);
		}
	}
	
	/** 
	 *  Method that creates a new piece in the game. This will be placed in the first subpanel and after will pass to the game when it takes.
	 */
	
	private void newPiece() {
		int colorPiece = (int) Math.floor(Math.random() * 7 + 1);
			
		switch(colorPiece) { 
			case 1:
				this.nextPiece.setColorPiece(Color.RED);
				break;
			case 2:
				this.nextPiece.setColorPiece(Color.BLUE);
				break;
			case 3:
				this.nextPiece.setColorPiece(Color.GREEN);
				break;
			case 4:
				this.nextPiece.setColorPiece(Color.YELLOW);
				break;
			case 5:
				this.nextPiece.setColorPiece(Color.ORANGE);
				break;
			case 6: 
				this.nextPiece.setColorPiece(Color.CYAN);
				break;
			case 7:
				this.nextPiece.setColorPiece(Color.MAGENTA);
				break;
			default:
				break;
		}
		
		this.nextPiece.setTypePiece((int) Math.floor(Math.random() * 7 + 1)); 
		this.nextPiece.setState(1);
		
		switch(this.nextPiece.getTypePiece()) { 
			// In all cases, I set a fixed square about which the piece will take the references for rotate and else.
			case 1: // Stick
				nextMatrix[0][3] = 1;
				nextMatrix[0][4] = 1;
				nextMatrix[0][5] = 1;
				nextMatrix[0][6] = 1;
				
				this.nextPiece.setXPosition(5);
				this.nextPiece.setYPosition(0);
				break;
			case 2: // Square
				nextMatrix[0][4] = 1;
				nextMatrix[0][5] = 1;
				nextMatrix[1][4] = 1;
				nextMatrix[1][5] = 1;
				
				this.nextPiece.setXPosition(4);
				this.nextPiece.setYPosition(1);
				break;
			case 3: // L Right
				nextMatrix[0][3] = 1;
				nextMatrix[0][4] = 1;
				nextMatrix[0][5] = 1;
				nextMatrix[1][5] = 1;
				
				this.nextPiece.setXPosition(4);
				this.nextPiece.setYPosition(0);
				break;
			case 4: // L Left
				nextMatrix[0][3] = 1;
				nextMatrix[0][4] = 1;
				nextMatrix[0][5] = 1;
				nextMatrix[1][3] = 1;
				
				this.nextPiece.setXPosition(4);
				this.nextPiece.setYPosition(0);
				break;
			case 5: // Z Right
				nextMatrix[0][4] = 1;
				nextMatrix[0][5] = 1;
				nextMatrix[1][5] = 1;
				nextMatrix[1][6] = 1;
				
				this.nextPiece.setXPosition(5);
				this.nextPiece.setYPosition(0);
				break;
			case 6: // Z Left
				nextMatrix[0][4] = 1;
				nextMatrix[0][5] = 1;
				nextMatrix[1][4] = 1;
				nextMatrix[1][3] = 1;
				
				this.nextPiece.setXPosition(4);
				this.nextPiece.setYPosition(0);
				break;
			case 7: // T
				nextMatrix[0][3] = 1;
				nextMatrix[0][4] = 1;
				nextMatrix[0][5] = 1;
				nextMatrix[1][4] = 1;
				
				this.nextPiece.setXPosition(4);
				this.nextPiece.setYPosition(0);
				break;
			default:
				break;
		}
		
		this.newPiece = true;
	}
	
	/**
	 *  Method for which the piece move by the game matrix.
	 *  
	 *  @param direction: Direction to move.
	 */
	
	public void move(int direction) {
		// You can't move in Game Over or Pause
		if(this.pieceInGame == false || this.inPause == true) {
			return;
		}
		
		switch(direction) {
			case 1: // MOVE DOWN
				// Before move me, check if I can move down, if I can't, check if there is a line or Game Over and add it in the trash matrix.
				if(Collision.downCollision(pieceMatrix, trashMatrix)) {  
					for(int i = 0; i < ROW; i++) {
						for(int j = 0; j < COL; j++) {
							if(pieceMatrix[i][j] == 1) {
								trashMatrix[i][j] = 1;
								colorMatrix[i][j] = this.piece.getColorPiece();
								
								pieceMatrix[i][j] = 0;
							}
						}
					}
					
					if(this.soundActivated == true) {
						try {
							this.soundFall = AudioSystem.getClip();
							this.soundFall.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Fall.wav")));
						} 
						catch (Exception e) {
							e.printStackTrace();
						}
						this.soundFall.start();
					}
					
					this.pieceInGame = false;
					this.piece.setState(-1);
					this.piece.setTypePiece(-1);
					this.piece.setColorPiece(null);
					this.numberOfChanges = 0;
				
					clearLine();
					gameOver();
					
					this.thread.interrupt();
				}
				else {
					for(int i = ROW - 1; i >= 0; i--) {
						for(int j = COL - 1; j >= 0; j--) {
							if(pieceMatrix[i][j] == 1) {
								pieceMatrix[i + 1][j] = 1;
								pieceMatrix[i][j] = 0;
							}						
						}
					}
					this.repaint();
					this.piece.setYPosition(this.piece.getYPosition() + 1);
				}	
				break;
			case 2: // MOVE LEFT 
				// If there isn't collision to the left, I move to the left.
				if(!Collision.leftCollision(pieceMatrix, trashMatrix)) { 
					for(int i = 0; i < ROW; i++) {
						for(int j = 0; j < COL; j++) {
							if(pieceMatrix[i][j] == 1) {
								pieceMatrix[i][j - 1] = 1;
								pieceMatrix[i][j] = 0;
							}
						}
					}
					this.repaint();
					this.piece.setXPosition(this.piece.getXPosition() - 1);
					
					if(this.soundActivated == true) {
						try {
							this.soundMove = AudioSystem.getClip();
							this.soundMove.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Move.wav")));
						} 
						catch (Exception e) {
							e.printStackTrace();
						}
						this.soundMove.start();
					}
				}
				break;
			case 3: // MOVE RIGHT
				// If there isn't collision to the right, I move to the right.
				if(!Collision.rightCollision(pieceMatrix, trashMatrix)) { 
					for(int i = ROW - 1; i >= 0; i--) {
						for(int j = COL - 1; j >= 0; j--) {
							if(pieceMatrix[i][j] == 1) {
								pieceMatrix[i][j + 1] = 1;
								pieceMatrix[i][j] = 0;
							}
						}
					}
					this.repaint();
					this.piece.setXPosition(this.piece.getXPosition() + 1);
					
					if(this.soundActivated == true) {
						try {
							this.soundMove = AudioSystem.getClip();
							this.soundMove.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Move.wav")));
						} 
						catch (Exception e) {
							e.printStackTrace();
						}
						this.soundMove.start();
					}
				}
				break;
			default:
				break;
		}	
	}
	
	/**	
	 * 	Method will let fall the piece.
	 */
	
	public void freeFall() {
		// You can't move in Game Over or Pause
		if(this.pieceInGame == false || this.inPause == true) {
			return;
		}
		
		// Down the piece until collides with the trash
		while(!Collision.downCollision(pieceMatrix, trashMatrix)) {  
			for(int i = ROW - 1; i >= 0; i--) {
				for(int j = COL - 1; j >= 0; j--) {
					if(pieceMatrix[i][j] == 1) {
						pieceMatrix[i + 1][j] = 1;
						pieceMatrix[i][j] = 0;
					}						
				}
			}
			this.piece.setYPosition(this.piece.getYPosition() + 1); 
		}
		
		// Add the piece to the trash, because always collides.
		for(int i = 0; i < ROW; i++) {
			for(int j = 0; j < COL; j++) {
				if(pieceMatrix[i][j] == 1) {
					trashMatrix[i][j] = 1;
					colorMatrix[i][j] = this.piece.getColorPiece();
					pieceMatrix[i][j] = 0;
				}
			}
		}
		
		this.pieceInGame = false;
		this.piece.setState(-1);
		this.piece.setTypePiece(-1);
		this.piece.setColorPiece(null);
		this.numberOfChanges = 0;
		
		clearLine();
		gameOver();
		
		this.thread.interrupt();
	}
	
	/**
	 *  Method will reserve the piece, but won't can do more than once with the same piece.
	 */
	
	public void reservation() {
		// You can't reserve in Game Over or Pause
		if(this.inPause == true || this.playing == false) { 
			return;
		}
		
		if(this.firstReservation == false) {
			// Copy the piece.
			this.reservationPiece.setTypePiece(this.piece.getTypePiece()); 
			this.reservationPiece.setColorPiece(this.piece.getColorPiece()); 
			
			// According the type of the piece, we place the coordinates in the matrix.
			switch(this.reservationPiece.getTypePiece()) { 
				// In all cases, I set a fixed square about which the piece will take the references for rotate and else. 
				case 1: // Stick
					reservationMatrix[0][3] = 1;
					reservationMatrix[0][4] = 1;
					reservationMatrix[0][5] = 1;
					reservationMatrix[0][6] = 1;
					
					this.reservationPiece.setXPosition(5);
					this.reservationPiece.setYPosition(0);
					break;
				case 2: // Square
					reservationMatrix[0][4] = 1;
					reservationMatrix[0][5] = 1;
					reservationMatrix[1][4] = 1;
					reservationMatrix[1][5] = 1;
					
					this.reservationPiece.setXPosition(4);
					this.reservationPiece.setYPosition(1);
					break;
				case 3: // L Right
					reservationMatrix[0][3] = 1;
					reservationMatrix[0][4] = 1;
					reservationMatrix[0][5] = 1;
					reservationMatrix[1][5] = 1;
					
					this.reservationPiece.setXPosition(4);
					this.reservationPiece.setYPosition(0);
					break;
				case 4: // L Left
					reservationMatrix[0][3] = 1;
					reservationMatrix[0][4] = 1;
					reservationMatrix[0][5] = 1;
					reservationMatrix[1][3] = 1;
					
					this.reservationPiece.setXPosition(4);
					this.reservationPiece.setYPosition(0);
					break;
				case 5: // Z Right
					reservationMatrix[0][4] = 1;
					reservationMatrix[0][5] = 1;
					reservationMatrix[1][5] = 1;
					reservationMatrix[1][6] = 1;
					
					this.reservationPiece.setXPosition(5);
					this.reservationPiece.setYPosition(0);
					break;
				case 6: // Z Left
					reservationMatrix[0][4] = 1;
					reservationMatrix[0][5] = 1;
					reservationMatrix[1][4] = 1;
					reservationMatrix[1][3] = 1;
					
					this.reservationPiece.setXPosition(4);
					this.reservationPiece.setYPosition(0);
					break;
				case 7: // T
					reservationMatrix[0][3] = 1;
					reservationMatrix[0][4] = 1;
					reservationMatrix[0][5] = 1;
					reservationMatrix[1][4] = 1;
					
					this.reservationPiece.setXPosition(4);
					this.reservationPiece.setYPosition(0);
					break;
				default:
					break;
			}
			
			for(int i = 0; i < ROW; i++) {
				for(int j = 0; j < COL; j++) {
					pieceMatrix[i][j] = 0;
				}
			}
			
			this.numberOfChanges++;
			this.firstReservation = true;
			this.pieceInGame = false;
			this.repaint();
		}
		else {
			// If the number of changes that have done are 2, go out.
			if(this.numberOfChanges == 2) { 
				return;
			}
			
			// Save the color and the type of piece.
			int tipoAux = this.piece.getTypePiece();
			Color colorAux = this.piece.getColorPiece();	
			
			// First we place the reservation in the piece matrix.
			for(int i = 0; i < ROW; i++) {
				for(int j = 0; j < COL; j++) {
					pieceMatrix[i][j] = reservationMatrix[i][j];
					reservationMatrix[i][j] = 0; // And the reservation to white
				}
			}
			
			this.piece.setColorPiece(this.reservationPiece.getColorPiece());
			this.piece.setState(this.reservationPiece.getState());
			this.piece.setTypePiece(this.reservationPiece.getTypePiece());
			this.piece.setXPosition(this.reservationPiece.getXPosition());
			this.piece.setYPosition(this.reservationPiece.getYPosition());
			
			// Now we generate the reservation from what we have saved.
			this.reservationPiece.setTypePiece(tipoAux); // Copy the type of piece.
			this.reservationPiece.setColorPiece(colorAux); // Copy the color of piece.
			
			// According the type of the piece, we place the coordinates in the matrix.
			switch(this.reservationPiece.getTypePiece()) { 
				// In all cases, I set a fixed square about which the piece will take the references for rotate and else.
				case 1: // Stick
					reservationMatrix[0][3] = 1;
					reservationMatrix[0][4] = 1;
					reservationMatrix[0][5] = 1;
					reservationMatrix[0][6] = 1;
					
					this.reservationPiece.setXPosition(5);
					this.reservationPiece.setYPosition(0);
					break;
				case 2: // Square
					reservationMatrix[0][4] = 1;
					reservationMatrix[0][5] = 1;
					reservationMatrix[1][4] = 1;
					reservationMatrix[1][5] = 1;
					
					this.reservationPiece.setXPosition(4);
					this.reservationPiece.setYPosition(1);
					break;
				case 3: // L Right
					reservationMatrix[0][3] = 1;
					reservationMatrix[0][4] = 1;
					reservationMatrix[0][5] = 1;
					reservationMatrix[1][5] = 1;
					
					this.reservationPiece.setXPosition(4);
					this.reservationPiece.setYPosition(0);
					break;
				case 4: // L Left
					reservationMatrix[0][3] = 1;
					reservationMatrix[0][4] = 1;
					reservationMatrix[0][5] = 1;
					reservationMatrix[1][3] = 1;
					
					this.reservationPiece.setXPosition(4);
					this.reservationPiece.setYPosition(0);
					break;
				case 5: // Z Right
					reservationMatrix[0][4] = 1;
					reservationMatrix[0][5] = 1;
					reservationMatrix[1][5] = 1;
					reservationMatrix[1][6] = 1;
					
					this.reservationPiece.setXPosition(5);
					this.reservationPiece.setYPosition(0);
					break;
				case 6: // Z Left
					reservationMatrix[0][4] = 1;
					reservationMatrix[0][5] = 1;
					reservationMatrix[1][4] = 1;
					reservationMatrix[1][3] = 1;
					
					this.reservationPiece.setXPosition(4);
					this.reservationPiece.setYPosition(0);
					break;
				case 7: // T
					reservationMatrix[0][3] = 1;
					reservationMatrix[0][4] = 1;
					reservationMatrix[0][5] = 1;
					reservationMatrix[1][4] = 1;
					
					this.reservationPiece.setXPosition(4);
					this.reservationPiece.setYPosition(0);
					break;
				default:
					break;
			}
			
			this.numberOfChanges++;
			this.reservation = true;
			this.repaint();
		}
	}
	
	/** 
	 *  Method will rotate the piece in the game.
	 */
	
	public void rotate() {
		
		// Check if I can rotate
		if(canRotate() == false) {
			return;
		}
		
		if(this.soundActivated == true) {
			try {
				this.soundChange = AudioSystem.getClip();
				this.soundChange.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Change.wav")));
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			this.soundChange.start();
		}
		
		// Clean the matrix.
		for(int i = 0; i < ROW; i++) {
			for(int j = 0; j < COL; j++) {
				pieceMatrix[i][j] = 0;
			}
		}
		
		// Fixed square from the others will take the reference.
		pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition()] = 1;
		
		// According to the type of piece, we pass a state or other.
		switch(this.piece.getTypePiece()) {
			case 1: // Stick:
				switch(this.piece.getState()) {
					case 1:						
						pieceMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition() - 2][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] = 1;
						this.piece.setState(2);
						break;
					case 2:
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] = 1;
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 2] = 1;
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] = 1;
						this.piece.setState(3);
						this.piece.setXPosition(this.piece.getXPosition() + 1);
						break;
					case 3:
						pieceMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition() - 2][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] = 1;
						this.piece.setState(4);
						break;
					case 4:
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] = 1;
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 2] = 1;
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] = 1;
						this.piece.setState(1);
						this.piece.setXPosition(this.piece.getXPosition() - 1);
						break;
					default:
						break;
				}
				break;
			case 2: // Square: 
				// There isn't rotation.
				break;
			case 3: // L Right:
				switch(this.piece.getState()) {
					case 1:
						pieceMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition() - 1] = 1;
						this.piece.setState(2);
						break;
					case 2:
						pieceMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition() - 1] = 1;
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] = 1;
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] = 1;
						this.piece.setState(3);
						break;
					case 3:
						pieceMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition() + 1] = 1;
						this.piece.setState(4);
						break;
					case 4:
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] = 1;
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] = 1;
						pieceMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition() + 1] = 1;
						this.piece.setState(1);
						break;
					default:
						break;
				}
				break;
			case 4: // L Left:
				switch(this.piece.getState()) {
					case 1:
						pieceMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition() - 1] = 1;
						this.piece.setState(2);
						break;
					case 2:
						pieceMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition() + 1] = 1;
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] = 1;
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] = 1;
						this.piece.setState(3);
						break;
					case 3:
						pieceMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition() + 1] = 1;
						pieceMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition()] = 1;
						this.piece.setState(4);
						break;
					case 4:
						pieceMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition() - 1] = 1;
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] = 1;
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] = 1;
						this.piece.setState(1);
						break;
					default:
						break;
				}
				break;
			case 5: // Z Right:
				switch(this.piece.getState()) {
					case 1:
						pieceMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] = 1;
						pieceMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition() - 1] = 1;
						this.piece.setState(2);
						break;
					case 2:
						pieceMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition() - 1] = 1;
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] = 1;
						this.piece.setState(3);
						break;
					case 3:
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] = 1;
						pieceMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition() + 1] = 1;
						pieceMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] = 1;
						this.piece.setState(4);
						break;
					case 4:
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] = 1;
						pieceMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition() + 1] = 1;
						this.piece.setState(1);
						break;	
					default:
						break;
				}
				break;
			case 6: // Z Left:
				switch(this.piece.getState()) {
					case 1:
						pieceMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] = 1;
						pieceMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition() + 1] = 1;
						this.piece.setState(2);
						break;
					case 2:
						pieceMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition() + 1] = 1;
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] = 1;
						this.piece.setState(3);
						break;
					case 3:
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] = 1;
						pieceMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition() - 1] = 1;
						pieceMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] = 1;
						this.piece.setState(4);
						break;
					case 4:
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] = 1;
						pieceMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition() - 1] = 1;
						this.piece.setState(1);
						break;	
					default:
						break;
				}
				break;
			case 7: // T
				switch(this.piece.getState()) {
					case 1:
						pieceMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] = 1;
						this.piece.setState(2);
						break;
					case 2:
						pieceMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] = 1;
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] = 1;
						this.piece.setState(3);
						break;
					case 3:
						pieceMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] = 1;
						this.piece.setState(4);
						break;
					case 4:
						pieceMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] = 1;
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] = 1;
						pieceMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] = 1;
						this.piece.setState(1);
						break;
					default:
						break;
				}
				break;
			default:
				break;
		}
		this.repaint(); // Finally, repaint.
	}
	
	/**
	 *  Method that check if I can rotate in the position that the piece is now.
	 */
	
	private boolean canRotate() {
		boolean canRotate = true;
		boolean save = false;
		int saveX = -1;
		int saveY = -1;
		
		// If I'm in pause or Game Over, don't rotate.
		if(this.pieceInGame == false || this.inPause == true) {
			canRotate = false;
			return canRotate;
		}
		
		// Rotation by the sides
		switch(this.piece.getTypePiece()) {
			case 1: // Stick
				
				if(this.piece.getYPosition() < 2) {
					canRotate = false;
					return canRotate;
				}
				else if(this.piece.getYPosition() == ROW - 1) {
					saveX = this.piece.getXPosition();
					saveY = this.piece.getYPosition();
					this.piece.setYPosition(ROW - 2);
					save = true;
				}
				
				if(this.piece.getXPosition() < 2) {
					saveX = this.piece.getXPosition();
					saveY = this.piece.getYPosition();
					this.piece.setXPosition(1);
					save = true;
				}
				else if(this.piece.getXPosition() > COL - 3) {
					saveX = this.piece.getXPosition();
					saveY = this.piece.getYPosition();
					this.piece.setXPosition(COL - 3);
					save = true;
				}
				
				if(trashMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] == 1 && (this.piece.getState() == 1 || this.piece.getState() == 3)) {
					saveX = this.piece.getXPosition();
					saveY = this.piece.getYPosition();
					this.piece.setYPosition(this.piece.getYPosition() - 1);
					save = true;
				}
				else if(trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] == 0 && trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 2] == 1 && (this.piece.getState() == 2 || this.piece.getState() == 4)) {
					saveX = this.piece.getXPosition();
					saveY = this.piece.getYPosition();
					this.piece.setXPosition(this.piece.getXPosition() - 1);
					save = true;
				}
				else if(trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] == 1 && (this.piece.getState() == 2 || this.piece.getState() == 4)) {
					saveX = this.piece.getXPosition();
					saveY = this.piece.getYPosition();
					this.piece.setXPosition(this.piece.getXPosition() - 2);
					save = true;
				}
				else if(trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] == 1 && (this.piece.getState() == 2 || this.piece.getState() == 4)) {
					saveX = this.piece.getXPosition();
					saveY = this.piece.getYPosition();
					this.piece.setXPosition(this.piece.getXPosition() + 1);
					save = true;
				}
				
				break;
			case 3: // L Right
			case 4: // L Left
			case 5: // Z Right
			case 6: // Z Left
			case 7: // T
				
				if(this.piece.getYPosition() < 1) {
					canRotate = false;
					return canRotate;
				}
				else if(this.piece.getYPosition() == ROW - 1) {
					saveX = this.piece.getXPosition();
					saveY = this.piece.getYPosition();
					this.piece.setYPosition(ROW - 2);
					save = true;
				}
				
				if(this.piece.getXPosition() < 1) {
					saveX = this.piece.getXPosition();
					saveY = this.piece.getYPosition();
					this.piece.setXPosition(1);
					save = true;
				}
				else if(this.piece.getXPosition() > COL - 2) {
					saveX = this.piece.getXPosition();
					saveY = this.piece.getYPosition();
					this.piece.setXPosition(COL - 2);
					save = true;
				}
				
				if(trashMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] == 1 && this.piece.getState() == 3) {
					saveX = this.piece.getXPosition();
					saveY = this.piece.getYPosition();
					this.piece.setYPosition(this.piece.getYPosition() - 1);
					save = true;
				}
				else if(trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] == 1 && this.piece.getState() == 2) {
					saveX = this.piece.getXPosition();
					saveY = this.piece.getYPosition();
					this.piece.setXPosition(this.piece.getXPosition() - 1);
					save = true;
				}
				else if(trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] == 1 && this.piece.getState() == 4) {
					saveX = this.piece.getXPosition();
					saveY = this.piece.getYPosition();
					this.piece.setXPosition(this.piece.getXPosition() + 1);
					save = true;
				}
				
				break;
			default:
				break;
		}
		
		// Rotation relative to the trash matrix
		switch(this.piece.getTypePiece()) {
			case 1:
				switch(this.piece.getState()) {
					case 1:
					case 3:
						if(trashMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition()] == 1 ||
						   trashMatrix[this.piece.getYPosition() - 2][this.piece.getXPosition()] == 1 ||
						   trashMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] == 1) {
							canRotate = false;
						}
						break;
					case 2:
					case 4:
						if(trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] == 1 ||
						   trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 2] == 1 ||
						   trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] == 1) {
							canRotate = false;
						}
						break;
				}
				break;
			case 2:	
				canRotate = false;
				break;
			case 3:
				switch(this.piece.getState()) {
					case 1:
						if(trashMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition()] == 1 ||
						   trashMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] == 1 ||
						   trashMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition() - 1] == 1) {
							canRotate = false;
						}
						break;
					case 2:
						if(trashMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition() - 1] == 1 ||
						   trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] == 1 ||
						   trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] == 1) {
							canRotate = false;
						}
						break;
					case 3:
						if(trashMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition()] == 1 ||
						   trashMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition() + 1] == 1 ||
						   trashMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] == 1) {
							canRotate = false;
						}
						break;
					case 4:
						if(trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] == 1 ||
						   trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] == 1 ||
						   trashMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition() + 1] == 1) {
							canRotate = false;
						}
						break;
				}
				break;
			case 4:
				switch(this.piece.getState()) {
					case 1:
						if(trashMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition()] == 1 ||
						   trashMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition() - 1] == 1 ||
						   trashMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] == 1) {
							canRotate = false;
						}
						break;
					case 2:
						if(trashMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition() + 1] == 1 ||
						   trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] == 1 ||
						   trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] == 1) {
							canRotate = false;
						}
						break;
					case 3:
						if(trashMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition()] == 1 ||
						   trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] == 1 ||
						   trashMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition() + 1] == 1) {
							canRotate = false;
						}
						break;
					case 4:
						if(trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] == 1 ||
						   trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] == 1 ||
						   trashMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition() - 1] == 1) {
							canRotate = false;
						}
						break;
				}
				break;
			case 5:
				switch(this.piece.getState()) {
					case 1:
						if(trashMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition()] == 1 ||
						   trashMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition() - 1] == 1) {
							canRotate = false;
						}
						break;
					case 2:
						if(trashMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition() - 1] == 1 ||
						   trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] == 1) {
							canRotate = false;
						}
						break;
					case 3:
						if(trashMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition() + 1] == 1 ||
						   trashMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] == 1) {
							canRotate = false;
						}
						break;
					case 4:
						if(trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] == 1 ||
						   trashMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition() + 1] == 1) {
							canRotate = false;
						}
						break;
				}
				break;
			case 6:
				switch(this.piece.getState()) {
					case 1:
						if(trashMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition()] == 1 ||
						   trashMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition() + 1] == 1) {
							canRotate = false;
						}
						break;
					case 2:
						if(trashMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition() + 1] == 1 ||
						   trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] == 1) {
							canRotate = false;
						}
						break;
					case 3:
						if(trashMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition() - 1] == 1 ||
						   trashMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] == 1) {
							canRotate = false;
						}
						break;
					case 4:
						if(trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] == 1 ||
						   trashMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition() - 1] == 1) {
							canRotate = false;
						}
						break;
				}
				break;
			case 7:
				switch(this.piece.getState()) {
					case 1:
					case 2:
					case 3:
					case 4:
						if(trashMatrix[this.piece.getYPosition() - 1][this.piece.getXPosition()] == 1 ||
						   trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() - 1] == 1 ||
						   trashMatrix[this.piece.getYPosition()][this.piece.getXPosition() + 1] == 1 ||
						   trashMatrix[this.piece.getYPosition() + 1][this.piece.getXPosition()] == 1) {
							canRotate = false;
						}
						break;
				}
				break;
			default:
				break;
		}
		
		if(canRotate == false && save == true) {
			this.piece.setXPosition(saveX);
			this.piece.setYPosition(saveY);
		}
		
		return canRotate;
	}
	
	/**
	 *  Method that check if there is a line. If there is a line, I clear the row and move all down.
	 */
	
	private void clearLine() {
		int count = 0;
		int maxLines = 0;
		for(int i = 0; i < ROW; i++) {
			count = 0;
			for(int j = 0; j < COL; j++) {
				if(trashMatrix[i][j] == 1) { // Check if there is a row with all cell to 1.
					count++;
				}
			}
			if(count == 10) { // If there is 10, clear.
				
				int aux;
				Color auxCol;
				
				for(int j = 0; j < COL; j++) { // Clear the line and his color.
					trashMatrix[i][j] = 0;
					colorMatrix[i][j] = null;
				}
				for(int k = i; k > 0; k--) { // Down the leftovers.
					for(int j = 0; j < COL; j++) {
						aux = trashMatrix[k][j];
						trashMatrix[k][j] = trashMatrix[k - 1][j];
						trashMatrix[k - 1][j] = aux;
						
						auxCol = colorMatrix[k][j];
						colorMatrix[k][j] = colorMatrix[k - 1][j];
						colorMatrix[k - 1][j] = auxCol;
					}
				}
				
				lines++; 
				maxLines++;
				this.controlLines = true;
				this.controlScore = true;
				
				if(level != 10) { // If the level is not 10, calculate the level.
					if(level != (lines / 10) + 1) { 
						level++;
						this.controlLevel = true;
						
						if(this.soundActivated == true) {
							try {
								this.soundLevel = AudioSystem.getClip();
								this.soundLevel.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Level.wav")));
							} 
							catch (Exception e) {
								e.printStackTrace();
							}
							this.soundLevel.start();
						}
					}
				}
			}
		}
		
		if(maxLines != 0) {
			if(this.soundActivated == true) {
				try {
					this.soundLine = AudioSystem.getClip();
					this.soundLine.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Line.wav")));
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				this.soundLine.start();
			}
		}
		else {
			if(this.soundActivated == true) {
				try {
					this.soundFall = AudioSystem.getClip();
					this.soundFall.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Fall.wav")));
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				this.soundFall.start();
			}
		}
		
		updateScore(maxLines); 
		this.repaint(); 
	}
	
	/**
	 *  Method will paint the screen.
	 *  
	 *  @param graphics: Graphics will use to paint.
	 */
	
	public void paint(Graphics graphics) {
		update(graphics);
	}	
	
	/**
	 *  Method for update the screen with the content for paint.
	 *  
	 *  @param graphics: Graphics will use to paint.
	 */
	
	public void update(Graphics graphics) {
		panel(graphics); 
		subpanel(graphics);
	}
	
	/**
	 *  Method will execute the thread of the game.
	 */
	
	public void run() {
		this.inPause = false; // To avoid bugs.
		newPiece(); 
		while(playing == true) { 
			if(this.inPause == false) {
				if(pieceInGame == false) { 
					pieceInGame = true;
					bringNewPiece();
					newPiece();
					this.repaint();
					
					// Delay between pieces
					try {
						Thread.sleep((1000 - (level * 75)));
					} 
					catch (InterruptedException e) {
						if(this.dificult.equals("normal")) {
							//  Delay between pieces
							try {
								Thread.sleep((1000 - (level * 75)));
							} 
							catch (InterruptedException e1) {
							}
						}
						continue;
					}
				}
				else { // Move the piece
					try {
						move(1);	
						Thread.sleep((1000 - (level * 75)));
					} 
					catch (InterruptedException e) { // When the piece falls, it calls the interrupted for direct pass to the next one.
						if(this.dificult.equals("normal")) {
							// Delay.
							try {
								Thread.sleep((1000 - (level * 75)));
							} 
							catch (InterruptedException e1) {
							}
						}
						continue;
					}
				}	
			}	
		}
	}
	
	/**
	 *  Method will pause the game.
	 */
	
	public void pauseGame() {
		if(this.playing == false) {
			return;
		}
		
		if(this.inPause == false) {
			this.inPause = true;
			
			if(this.soundActivated == true) {
				this.backgroundMusic.stop();
			}
				
			if(this.soundActivated == true) {
				try {
					this.soundPause = AudioSystem.getClip();
					this.soundPause.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Pause.wav")));
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				this.soundPause.start();
			}	
			
			this.repaint();
		}
		else {
			this.inPause = false;
			
			if(this.soundActivated == true) {
				this.backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
			}
			
			this.repaint();
		}
	}
	
	/**
	 *  Method will update the score.
	 * 
	 *  @param lines: Number of lines done at the same time.
	 */
	
	private void updateScore(int lines) {
		if(score > 999900) {
			score = 999999;
		}
		else {
			score += (lines * 100) * lines;
		}	
	}
	
	/**
	 *  Method will check the Game Over..
	 */
	
	private void gameOver() {
		for(int j = 0; j < COL; j++) {
			if(trashMatrix[0][j] == 1) { // If some position in the first line is 1, is Game Over.
				this.playing = false;
				
				if(this.soundActivated == true) {
					this.backgroundMusic.stop();
					this.soundGameOver.start();
				}
				
				this.repaint();
				checkScore();
				return;
			}
		}
	}
	
	/**
	 *  Method will start a new game.
	 */
	
	@SuppressWarnings("deprecation")
	public void newGame() {

		this.thread.stop();
		
		// Reload the music.
		if(this.soundActivated == true) {
			this.backgroundMusic.close();
			
			try {
				this.backgroundMusic = AudioSystem.getClip();
				this.backgroundMusic.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Tetris.wav")));
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			this.backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
			
			// Reload the Game Over sound.
			try {
				this.soundGameOver = AudioSystem.getClip();
				this.soundGameOver.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/GameOver.wav")));
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}	
		
		// Reset:
		this.lines = 0;
		this.level = 1;
		this.score = 0;
		this.pieceInGame = false;
		this.playing = true;
		this.piece.setState(-1);
		this.piece.setTypePiece(-1);
		this.reservationPiece.setTypePiece(-1);
		this.piece.setColorPiece(null);
		this.reservationPiece.setColorPiece(null);
		this.firstReservation = false;
		this.reservation = false;
		this.numberOfChanges = 0;
		
		for(int i = 0; i < ROW; i++) {
			for(int j = 0; j < COL; j++) {
				pieceMatrix[i][j] = 0;
				nextMatrix[i][j] = 0;
				trashMatrix[i][j] = 0;
				reservationMatrix[i][j] = 0;
				colorMatrix[i][j] = null;
			}
		}
		
		this.thread = new Thread(this);
		this.thread.start();
		
		this.repaint();
	}
	
	/**
	 *  Method that place the next piece in the game.
	 */
	
	private void bringNewPiece() {
		for(int i = 0; i < ROW; i++) {
			for(int j = 0; j < COL; j++) {
				if(nextMatrix[i][j] == 1) {
					pieceMatrix[i][j] = nextMatrix[i][j];
					nextMatrix[i][j] = 0;
				}
			}
		}
		
		this.piece.setColorPiece(this.nextPiece.getColorPiece());
		this.piece.setState(this.nextPiece.getState());
		this.piece.setTypePiece(this.nextPiece.getTypePiece());
		this.piece.setXPosition(this.nextPiece.getXPosition());
		this.piece.setYPosition(this.nextPiece.getYPosition());
	}
	
	/**
	 * 	Method will activated o deactivated the sound.
	 * 
	 * 	@param sound: Boolean that indicate if activates or not the sound.
	 */
	
	public void setSoundActivated(boolean sound) {
		this.soundActivated = sound;
		
		if(this.soundActivated == false) {
			this.backgroundMusic.close();
		}
		else {
			if(this.playing == true && this.inPause == false) {
				try {
					this.backgroundMusic = AudioSystem.getClip();
					this.backgroundMusic.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Tetris.wav")));
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				this.backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
			}
		}
	}
	
	/** 
	 *  Method will set the dificult of the game.
	 * 
	 *  @param dificult: New dificult of the game.
	 */
	
	public void setDificult(String dificult) {
		this.dificult = dificult;
		this.repaint();
	}
	
	/**
	 *  Method that check if the score that have done in the game is better than the others in the registers. 
	 */
	
	private void checkScore() {
		File fileScore = new File("score");
		ControlScore control = null;
		
		try {
			control = new ControlScore(fileScore);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		Score aux = null;
		
		if(control.getLength() < 1) {
			String name = JOptionPane.showInputDialog("¿Cuál es su nombre?");
			
			if(name == null || name.equals("")) { 
				name = "Anónimo";
			}
			
			try {
				aux = new Score(name, this.score, this.lines);
				control.addScore(aux);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			int indice = -1;
			
			for(int i = 0; i < control.getLength(); i++) {
				try {
					aux = control.getValueIn(i);
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
				
				if(this.score >= aux.getScore()) { 
					indice = i; 
					break;
				}
			}
			
			Score aux2 = null;
			
			if(indice != -1) { 
				String nombre = JOptionPane.showInputDialog("¿Cuál es su nombre?");
				
				if(nombre == null || nombre.equals("")) { 
					nombre = "Anónimo";
				}
				
				for(int i = indice; i < control.getLength(); i++) { 
					if(i == indice) { 
						try {
							aux = control.getValueIn(indice);
							control.setValueInt(i, new Score(nombre, this.score, this.lines));
						} 
						catch (IOException e) {
							e.printStackTrace();
						}
						
						if(i == control.getLength() - 1 && control.getLength() < 3) {
							try {
								control.addScore(aux);
								break;
							} 
							catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					else if(i == control.getLength() - 1 && control.getLength() < 3) { 
						try {
							control.addScore(aux);
							break;
						} 
						catch (IOException e) {
							e.printStackTrace();
						}
					}
					else { 
						try {
							aux2 = control.getValueIn(i);
							control.setValueInt(i, aux);
							aux = aux2;
						} 
						catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		window.showStatistics(); 
	}
	
	/**
	 *  Method that paint the frame of the panel of the game.
	 * 
	 *  @param color: Color of the frame.
	 *  @param graphics: Grahpics for paint.
	 */
	
	private void frame(Color color, Graphics graphics) {
		for(int i = 0; i < BORDER; i++) {
			
			// Light lines:
			graphics.setColor(color); // Horizontal
			graphics.drawLine(0 + i, 
							 0 + i, 
							 WIDTH_PANEL + BORDER * 2 - i, 
							 0 + i);
			
			graphics.setColor(color); // Vertical
			graphics.drawLine(0 + i, 
							 0 + i, 
							 0 + i, 
							 HIGH_PANEL + BORDER * 2 - i);
			
			// Dark lines:
			graphics.setColor(color.darker().darker()); // Horizontal
			graphics.drawLine(0 + i, 
							 HIGH_PANEL + BORDER * 2 - i, 
							 WIDTH_PANEL + BORDER * 2 - i, 
							 HIGH_PANEL + BORDER * 2 - i);
			
			graphics.setColor(color.darker().darker()); // Vertical
			graphics.drawLine(WIDTH_PANEL + BORDER * 2 - i, 
							 0 + i, 
							 WIDTH_PANEL + BORDER * 2 - i, 
							 HIGH_PANEL + BORDER * 2 - i);
		}
	}
	
	/**
	 *  Method that paint the frame of the subpanel.
	 * 
	 *  @param color: Color of the frame.
	 *  @param graphics: Graphics for paint.
	 */
	
	private void frameSubpanel(Color color, Graphics graphics) {
		for(int i = 0; i < BORDER; i++) {
			
			// Light lines:
			graphics.setColor(color); // Horizontal
			graphics.drawLine(DISTANCE_SUBPANEL_X + i, 
							 0 + i, 
							 DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL + BORDER * 2 - i, 
							 0 + i);
			
			graphics.setColor(color); // Vertical
			graphics.drawLine(DISTANCE_SUBPANEL_X + i, 
							 0 + i, 
							 DISTANCE_SUBPANEL_X + i, 
							 HIGH_SUBPANEL + BORDER * 2 - i);
			
			// Dark lines:
			graphics.setColor(color.darker().darker()); // Horizontal
			graphics.drawLine(DISTANCE_SUBPANEL_X + i, 
							 HIGH_SUBPANEL + BORDER * 2 - i, 
							 DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL + BORDER * 2 - i, 
							 HIGH_SUBPANEL + BORDER * 2 - i);
			
			graphics.setColor(color.darker().darker()); // Vertical
			graphics.drawLine(DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL + BORDER * 2 - i, 
							 0 + i, 
							 DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL + BORDER * 2 - i, 
							 HIGH_SUBPANEL + BORDER * 2 - i);
		}
	}
	
	/**
	 *  Method that paint the panel of the game.
	 * 
	 *  @param graphics: Graphics for paint.
	 */
	
	public void panel(Graphics graphics) {
		
		// Frame:
		frame(Color.GRAY, graphics);
		
		// Grid:
		for(int i = 0; i < ROW; i++) {
			for(int j = 0; j < COL; j++) {
				if(this.dificult.equals("normal")) {
					graphics.setColor(Color.DARK_GRAY.darker().darker());
				}
				else if(this.dificult.equals("dificil")){
					graphics.setColor(Color.BLACK);
				}
				graphics.drawRect(j * SIZE + BORDER, i * SIZE + BORDER, SIZE, SIZE);
			}
		}
		
		// Black background for paint the new piece:
		if(this.newPiece == true || this.firstReservation == false) {
			graphics.setColor(Color.BLACK);
			graphics.fillRect(DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (2.14f * SIZE), 
							 HIGH_SUBPANEL - (int) (18.37f * SIZE), 
							 (int) (4.57f * SIZE), 
							 SIZE * 3);
			this.newPiece = false;
		}
		
		// Black background for paint the reservation piece:
		if(this.firstReservation == false || this.reservation == true) {
			graphics.setColor(Color.BLACK);
			graphics.fillRect(DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (2.14f * SIZE), 
							 HIGH_SUBPANEL - (int) (4.08f * SIZE), 
							 (int) (4.57f * SIZE), 
							 SIZE * 3);
			this.reservation = false;
		}	
		
		// Paint the pieces:
		for(int i = 0; i < ROW; i++) {
			for(int j = 0; j < COL; j++) {
				if(trashMatrix[i][j] == 0) {
					graphics.setColor(Color.BLACK); 
					graphics.fillRect(j * SIZE + BORDER + 1, 
									 i * SIZE + BORDER + 1, 
									 SIZE - 1, 
									 SIZE - 1);
				}
				if(pieceMatrix[i][j] == 1) {
					square(j, i, this.piece.getColorPiece(), graphics);
				}
				if(trashMatrix[i][j] == 1) {
					square(j, i, colorMatrix[i][j], graphics);
				}
				if(nextMatrix[i][j] == 1) {
					square(j + 9, i + 2, this.nextPiece.getColorPiece(), graphics);
				}
				if(reservationMatrix[i][j] == 1 && this.firstReservation == true) {
					square(j + 9, i + 16, this.reservationPiece.getColorPiece(), graphics);
				}
			}
		}
	}
	
	/**
	 *  Method that paint the subpanel of the game.
	 * 
	 *  @param grafico: Graphics for paint.
	 */
	
	public void subpanel(Graphics graphics) {
		
		// Frame:
		frameSubpanel(Color.GRAY, graphics);	
		
		// Next text:
		graphics.setColor(Color.WHITE);
		graphics.setFont(new Font("Verdana", Font.BOLD, (int) (0.45f * SIZE)));
		graphics.drawString("SIGUIENTE", DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (1.28f * SIZE), HIGH_SUBPANEL - (int) (18.85f * SIZE));
		
		// Grid of the new piece:
		for(int i = 1; i <= 4; i++) {
			
			// Light lines:
			graphics.setColor(Color.GRAY); // Horizontal
			graphics.drawLine(DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (2.14f * SIZE) - i, 
							 HIGH_SUBPANEL - (int) (18.37f * SIZE) - i, 
							 (DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (2.14f * SIZE) - i) + ((int) (4.57f * SIZE) + i * 2), 
							 HIGH_SUBPANEL - (int) (18.37f * SIZE) - i);
			
			graphics.setColor(Color.GRAY); // Vertical
			graphics.drawLine(DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (2.14f * SIZE) - i, 
							 HIGH_SUBPANEL - (int) (18.37f * SIZE) - i, 
							 DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (2.14f * SIZE) - i, 
							 (HIGH_SUBPANEL - (int) (18.37f * SIZE) - i) + (SIZE * 3 + i * 2));
			
			// Dark lines:
			graphics.setColor(Color.GRAY.darker().darker()); // Horizontal
			graphics.drawLine(DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (2.14f * SIZE) - i, 
							 (HIGH_SUBPANEL - (int) (18.37f * SIZE) - i) + (SIZE * 3 + i * 2), 
							 (DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (2.14f * SIZE) - i) + ((int) (4.57f * SIZE) + i * 2), 
							 (HIGH_SUBPANEL - (int) (18.37f * SIZE) - i) + (SIZE * 3 + i * 2));
			
			graphics.setColor(Color.GRAY.darker().darker()); // Vertical 
			graphics.drawLine((DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (2.14f * SIZE) - i) + ((int) (4.57f * SIZE) + i * 2), 
							 HIGH_SUBPANEL - (int) (18.37f * SIZE) - i, 
							 (DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (2.14f * SIZE) - i) + ((int) (4.57f * SIZE) + i * 2), 
							 (HIGH_SUBPANEL - (int) (18.37f * SIZE) - i) + (SIZE * 3 + i * 2));
			
		}
		
		// Information:
		if(this.controlLevel == true) {
			graphics.setColor(Color.BLACK);
			graphics.fillRect(DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2, HIGH_SUBPANEL - (int) (13.85f * SIZE), SIZE * 3, SIZE);
			this.controlLevel = false;
		}
		
		graphics.setColor(Color.WHITE);
		graphics.setFont(new Font("Verdana", Font.BOLD, (int) (0.45f * SIZE)));
		graphics.drawString("NIVEL:  " + level, DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (1.85f * SIZE), HIGH_SUBPANEL - (int) (13.14f * SIZE));
		
		if(this.controlLines == true) {
			graphics.setColor(Color.BLACK);
			graphics.fillRect(DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 + (int) (0.14f * SIZE), HIGH_SUBPANEL - (int) (12f * SIZE), SIZE * 3, SIZE);
			this.controlLines = false;
		}
		
		graphics.setColor(Color.WHITE);
		graphics.setFont(new Font("Verdana", Font.BOLD, (int) (0.45f * SIZE)));
		graphics.drawString("LÍNEAS:  " + lines, DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (1.85f * SIZE) , HIGH_SUBPANEL - (int) (11.28f * SIZE));
		
		
		if(this.controlScore == true) {
			graphics.setColor(Color.BLACK);
			graphics.fillRect(DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2, HIGH_SUBPANEL - (int) (10.14f * SIZE), SIZE * 3, SIZE);
			this.controlScore = false;
		}
		
		graphics.setColor(Color.WHITE);
		graphics.setFont(new Font("Verdana", Font.BOLD, (int) (0.45f * SIZE)));
		graphics.drawString("SCORE:  " + score, DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (1.85f * SIZE) , HIGH_SUBPANEL - (int) (9.42f * SIZE));
		
		// Pause:
		if(this.inPause == true && this.playing == true) {
			graphics.setColor(Color.WHITE);
			graphics.setFont(new Font("Verdana", Font.BOLD, (int) (0.45f * SIZE)));
			graphics.drawString("PAUSA", DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (0.85f * SIZE) , HIGH_SUBPANEL - (int) (5.71f * SIZE));
		}
		else {
			graphics.setColor(Color.BLACK);
			graphics.fillRect(DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (0.85f * SIZE) , HIGH_SUBPANEL - (int) (6.42f * SIZE), SIZE * 2, SIZE);
		}
		
		if(this.playing == false) {
			graphics.setColor(Color.WHITE);
			graphics.setFont(new Font("Verdana", Font.BOLD, (int) (0.45f * SIZE)));
			graphics.drawString("GAME OVER", DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (1.28f * SIZE) , HIGH_SUBPANEL - (int) (7.57f * SIZE));
		}
		else {
			graphics.setColor(Color.BLACK);
			graphics.fillRect(DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (1.42f * SIZE) , HIGH_SUBPANEL - (int) (8.42f * SIZE), SIZE * 4, SIZE);
		}

		// Reservation text:
		graphics.setColor(Color.WHITE);
		graphics.setFont(new Font("Verdana", Font.BOLD, (int) (0.45f * SIZE)));
		graphics.drawString("RESERVA", DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - SIZE, HIGH_SUBPANEL - (int) (4.57f * SIZE));
		
		// Grid of the new piece:
		for(int i = 1; i <= 4; i++) {
			
			// Light lines:
			graphics.setColor(Color.GRAY); // Horizontal
			graphics.drawLine(DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (2.14f * SIZE) - i, 
							 HIGH_SUBPANEL - (int) (4.08f * SIZE) - i, 
							 (DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (2.14f * SIZE) - i) + ((int) (4.57f * SIZE) + i * 2), 
							 HIGH_SUBPANEL - (int) (4.08f * SIZE) - i);
			
			graphics.setColor(Color.GRAY); // Vertical
			graphics.drawLine(DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (2.14f * SIZE) - i, 
							 HIGH_SUBPANEL - (int) (4.08f * SIZE) - i, 
							 DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (2.14f * SIZE) - i, 
							 (HIGH_SUBPANEL - (int) (4.08f * SIZE) - i) + (SIZE * 3 + i * 2));
			
			// Dark lines:
			graphics.setColor(Color.GRAY.darker().darker()); // Horizontal
			graphics.drawLine(DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (2.14f * SIZE) - i, 
							 (HIGH_SUBPANEL - (int) (4.08f * SIZE) - i) + (SIZE * 3 + i * 2), 
							 (DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (2.14f * SIZE) - i) + ((int) (4.57f * SIZE) + i * 2), 
							 (HIGH_SUBPANEL - (int) (4.08f * SIZE) - i) + (SIZE * 3 + i * 2));
			
			graphics.setColor(Color.GRAY.darker().darker()); // Vertical 
			graphics.drawLine((DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (2.14f * SIZE) - i) + ((int) (4.57f * SIZE) + i * 2), 
							 HIGH_SUBPANEL - (int) (4.08f * SIZE) - i, 
							 (DISTANCE_SUBPANEL_X + WIDTH_SUBPANEL/2 - (int) (2.14f * SIZE) - i) + ((int) (4.57f * SIZE) + i * 2), 
							 (HIGH_SUBPANEL - (int) (4.08f * SIZE) - i) + (SIZE * 3 + i * 2));
			
		}
	}
}