package fun.gbr;

import java.nio.file.Path;
import java.util.Random;

public class Utils {
	
	private static Random rand = new Random();
	public static Random getRandom() {
		return rand;
	}
	
	/**
	 * @return The path for the dictionary set by the user
	 */
	public static Path getDictionaryPath(String key) {
		String path = System.getProperty(key);
		if(path == null) {
			throw new IllegalArgumentException(key + " not specified");
		}

		Path dictionaryPath = Path.of(path);
		return dictionaryPath;
	}
	
	/** Converts system property to path if not null
	 * @param propertyName
	 * @return Path equivalent of property
	 */
	public static Path toNonNullPath(String propertyName) {
		String pathString = System.getProperty(propertyName);
		if(pathString == null) {
			throw new IllegalArgumentException(propertyName + " must be specified.");
		}
		return Path.of(pathString);
	}
}
