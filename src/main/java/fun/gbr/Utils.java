package fun.gbr;

import java.nio.file.Path;

public class Utils {
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
}
