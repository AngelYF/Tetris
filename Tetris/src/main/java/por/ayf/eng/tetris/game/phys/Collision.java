package por.ayf.eng.tetris.game.phys;

import por.ayf.eng.tetris.game.Game;

/**
 *  Class will check the collision between matrix.
 * 
 *  @author: Ángel Yagüe Flor
 *  @version: 1.0 - Stable.
 *  @version: 2.1 - Refactor the project.
 */

public class Collision {
	
	/**
	 *  Method will check if there is collision between the piece and the trash of the game.
	 * 
	 *  @param pieceMatrix: Matrix of the piece.
	 *  @param trashMatrix: Matrix of the trash.
	 *  @return True if there is a collision.
	 */
	
	public static boolean downCollision(int[][] pieceMatrix, int[][] trashMatrix) {
		for(int i = 0; i < Game.ROW; i++) {
			for(int j = 0; j < Game.COL; j++) {
				if(pieceMatrix[i][j] == 1) {
					if(i == Game.ROW - 1) {
						return true;
					}
					else if(trashMatrix[i + 1][j] == 1) {
						return true;
					}
				}
			}
		}		
		return false;
	}
	
	/**
	 *  Method will check if there is collision from the right side of the piece.
	 * 
	 *  @param pieceMatrix: Matrix of the piece.
	 *  @param trashMatrix: Matrix of the trash.
	 *  @return True if there is a collision.
	 */
	
	public static boolean rightCollision(int[][] pieceMatrix, int[][] trashMatrix) {
		for(int i = 0; i < Game.ROW; i++) {
			for(int j = 0; j < Game.COL; j++) {
				if(pieceMatrix[i][j] == 1) {
					if(j == Game.COL - 1) {
						return true;
					}
					else if(trashMatrix[i][j + 1] == 1) {
						return true;
					}
				}
			}
		}		
		return false;
	}

	/**
	 *  Method will check if there is collision from the left side of the piece.
	 * 
	 *  @param pieceMatrix: Matrix of the piece.
	 *  @param trashMatrix: Matrix of the trash.
	 *  @return True if there is a collision.
	 */
	
	public static boolean leftCollision(int[][] pieceMatrix, int[][] trashMatrix) {
		for(int i = 0; i < Game.ROW; i++) {
			for(int j = 0; j < Game.COL; j++) {
				if(pieceMatrix[i][j] == 1) {
					if(j == 0) {
						return true;
					}
					else if(trashMatrix[i][j - 1] == 1) {
						return true;
					}
				}
			}
		}		
		return false;
	}
}
