package fun.gbr;

import java.nio.file.Path;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import fun.gbr.options.Options;
import fun.gbr.options.Options.LoggerOptionKeys;

public class Utils {
	
	private Utils() {}
	
	private static Random rand = new Random();
	public static Random getRandom() {
		return rand;
	}
	
	/**
	 * @return The path for the dictionary set by the user
	 */
	public static Path getDictionaryPath(String key) {
		String path = Options.get().property(key);
		if(path == null) {
			throw new IllegalArgumentException(key + " not specified");
		}

		return Path.of(path);
	}
	
	/** Converts system property to path if not null
	 * @param propertyName
	 * @return Path equivalent of property
	 */
	public static Path toNonNullPath(String propertyName) {
		String pathString = Options.get().property(propertyName);
		if(pathString == null) {
			throw new IllegalArgumentException(propertyName + " must be specified.");
		}
		return Path.of(pathString);
	}
	
	/** Initialise prerequisites for execution
	 * @return true if initialisation was successful, false otherwise
	 */
	public static boolean initProgram() {
		return initProgram(null);
	}
	
	/** Initialise prerequisites for execution
	 * @param lok Options to use for logger settings
	 * @return true if initialisation was successful, false otherwise
	 */
	public static boolean initProgram(LoggerOptionKeys lok) {
		try {
			Logger.getLogger("").setLevel(Level.WARNING); // Use warning as default log level
			Options.init(lok);
		} catch(Exception e) {
			// Log exception if possible
			Logger.getLogger(Utils.class.getCanonicalName()).log(Level.SEVERE, e, () -> "Error during initialisation!");
			return false;
		}		
		return true;
	}
}
