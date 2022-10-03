package fun.gbr.options;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Controls Logger settings
 *
 */
public class LoggerHandler {
	private LoggerHandler() {}
	
	/** Add a new FileHandler to root logger so that logs are written to file at path
	 * @param path
	 */
	public static void addLogFile(String path) {
		addLogFile(path, null);
	}
	
	/** Add a new FileHandler to logger so that logs are made to path
	 * @param path
	 * @param logger
	 */
	public static void addLogFile(String path, Logger logger) {
		if(logger == null) {
			logger = Logger.getLogger("");
		}
		try {
			FileHandler fh = new FileHandler(path);
			fh.setFormatter(new SimpleFormatter());
			logger.addHandler(fh);
		} catch(SecurityException | IOException e) {
			Logger.getLogger(LoggerHandler.class.getCanonicalName()).log(Level.SEVERE, e, () -> "Failed to add log file!");
		}
	}
	
	/** Set root logger level
	 * @param levelStr
	 */
	public static void setLevel(String levelStr) {
		setLevel(levelStr, null);
	}
	
	/** Set logger level
	 * @param levelStr
	 * @param logger
	 */
	public static void setLevel(String levelStr, Logger logger) {
		if(logger == null) {
			logger = Logger.getLogger("");
		}
		logger.setLevel(Level.parse(levelStr));
	}
}
