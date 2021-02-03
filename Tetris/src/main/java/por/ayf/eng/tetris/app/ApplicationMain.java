package por.ayf.eng.tetris.app;

import javax.swing.UIManager;

import por.ayf.eng.tetris.app.ApplicationMain;
import por.ayf.eng.tetris.util.Util;
import por.ayf.eng.tetris.view.ViewMainWindow;

/**
 *  Main class will execute the Tetris.
 * 
 *  @author: Ángel Yagüe Flor
 *  @version: 1.0 - Stable.
 *  @version: 2.0 - Visual improve of the menus.
 *  @version: 2.1 - Refactor the project.
 */

public class ApplicationMain {
	public static void main(String[] args) {	
		try { // This try-catch will change the regular aparence of JFrame of Java.
			
			// UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel"); 				Other type of view.
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); 		Other.
			
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 					// By defect.
           	new ViewMainWindow(); 	
        } 
		catch (Exception ex) {
			Util.logMessage(Util.LEVEL_ERROR, "Ha ocurrido un error al iniciar la aplicación.", ApplicationMain.class, ex);
        } 
	}
}
