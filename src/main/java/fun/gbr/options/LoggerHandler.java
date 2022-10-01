package fun.gbr.options;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
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
	
	/**
	 * Determines how the handler should react to config attempts
	 */
	private static State state = new Uninitialised();
	
	/**
	 * Initialise root logger. Should always be initialised at the start of main.
	 */
	public static void initLogger() {
        initLogger(null);
	}
	
	/**
	 * Initialise logger. Should always be initialised at the start of main.
	 */
	public static void initLogger(Logger logger) {
		if(logger == null) {
			logger = Logger.getLogger("");
		}
        logger.setLevel(Level.WARNING);        
        logger.addHandler(new ConsoleHandler());
        state = new Initialised();
	}
	
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
			state.addLogFile(path, logger);
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
		state.setLevel(levelStr, logger);
	}
	
	/**
	 * The state of the LoggerHandler, determines how operations are treated
	 *
	 */
	private static interface State{
		public void addLogFile(String path, Logger logger) throws SecurityException, IOException;
		public void setLevel(String levelStr, Logger logger);
	}
	
	/**
	 * Represents uninitialised state, will reject all operations
	 *
	 */
	private static class Uninitialised implements State{

		private static final String ERROR = "Attempt to set log properties on uninitialised logger!";
		
		@Override
		public void addLogFile(String path, Logger logger) throws SecurityException, IOException {
			throw new IllegalStateException(ERROR);
		}

		@Override
		public void setLevel(String levelStr, Logger logger) {
			throw new IllegalStateException(ERROR);
		}		
	}
	
	/**
	 * Represents initialised state
	 *
	 */
	private static class Initialised implements State{

		@Override
		public void addLogFile(String path, Logger logger) throws SecurityException, IOException {
			FileHandler fh = new FileHandler(path);
			fh.setFormatter(new SimpleFormatter());
			logger.addHandler(fh);
		}

		@Override
		public void setLevel(String levelStr, Logger logger) {
			logger.setLevel(Level.parse(levelStr));
		}
		
	}
}
