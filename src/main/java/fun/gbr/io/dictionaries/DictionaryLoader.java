package fun.gbr.io.dictionaries;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import fun.gbr.Utils;
import fun.gbr.options.Options;

/**
 * Classes implementing this interface must be able to return a dictionary.
 * The interface itself is in charge of choosing which class to use.
 *
 */
public interface DictionaryLoader {
	public Map<String, String> get() throws IOException;
	
	/**
	 * @return A loader containing a dictionary map
	 * @throws IOException
	 */
	public static DictionaryLoader initialise() throws IOException {
		Path path = Utils.getDictionaryPath(DICTIONARY_PATH_KEY);
		boolean generateRandom = Options.get().doEncode()
				&& DO_RANDOMISE_VALUE.equals(Options.get().property(RANDOMISE_KEY))
				&& !Files.exists(path);
		if(generateRandom) {
			Logger.getLogger(DictionaryLoader.class.getCanonicalName()).config(() -> "Writing new dictionary to \"" + path.toAbsolutePath() + "\"");
			return new DictionaryCreator(path);
		}		
		Logger.getLogger(DictionaryLoader.class.getCanonicalName()).config(() -> "Reading dictionary from \"" + path.toAbsolutePath() + "\"");
		return new DictionaryReader(path, Options.get().mode());
	}
	
	public static final String DICTIONARY_PATH_KEY = "sub/dictionary_path";
	public static final String RANDOMISE_KEY = "sub/generate_dictionary_if_absent";
	public static final String DO_RANDOMISE_VALUE = "true";
	public static final Pattern DICTIONARY_ENTRY_PATTERN = Pattern.compile("\"(.+)\"\\s+\"(.*)\"");
}
