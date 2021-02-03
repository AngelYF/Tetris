package por.ayf.eng.tetris.util;

import org.apache.log4j.Logger;

import javax.swing.JOptionPane;

/**
 *  Class Util with some util methods.
 * 
 *  @author: Ángel Yagüe Flor.
 *  @version: 1.0.
 */

public class Util {
	public static final String LEVEL_TRACE	= "TRACE";
	public static final String LEVEL_DEBUG	= "DEBUG";
	public static final String LEVEL_INFO	= "INFO";
	public static final String LEVEL_WARN	= "WARN";
	public static final String LEVEL_ERROR	= "ERROR";
	public static final String LEVEL_FATAL	= "FATAL";
	
	public static void logMessage(String level, String message, Class<?> clasz, Exception e) {
		Logger rootLogger = Logger.getRootLogger();
		
		switch(level) {
			case "TRACE":
				rootLogger.trace("CLASS: " + clasz.getName() + " - " + message);
				break;
			case "DEBUG":
				rootLogger.debug("CLASS: " + clasz.getName() + " - " + message);
				break;
			case "INFO":
				rootLogger.info("CLASS: " + clasz.getName() + " - " + message);
				break;
			case "WARN":
				rootLogger.warn("CLASS: " + clasz.getName() + " - " + message);
				break;
			case "ERROR":
				rootLogger.error("CLASS: " + clasz.getName() + " - " + message + "\n\nExcepción: ");
				e.printStackTrace();
				break;
			case "FATAL":
				rootLogger.fatal("CLASS: " + clasz.getName() + " - " + message + "\n\nExcepción: ");
				e.printStackTrace();
				break;
		}
		
	}
	
	public static void showMessage(Class<?> clasz, String message, int typeMessage, Exception e) {
		switch(typeMessage) {
			case 0:	// ERROR
				Util.logMessage(LEVEL_ERROR, message, clasz, e);
				
				JOptionPane.showMessageDialog(null,
						message,
						"Error", 
						JOptionPane.ERROR_MESSAGE);
				break;
			case 1:	// INFORMATION
				JOptionPane.showMessageDialog(null,
						message,
						"Información", 
						JOptionPane.INFORMATION_MESSAGE);
				break;
			case 2:	// WARNING
				JOptionPane.showMessageDialog(null,
						message,
						"Advertencia", 
						JOptionPane.WARNING_MESSAGE);
				break;
			case 3:	// QUESTION
				JOptionPane.showMessageDialog(null,
						message,
						"Pregunta", 
						JOptionPane.QUESTION_MESSAGE);
				break;
			default:
				break;
		}	
	}
}
