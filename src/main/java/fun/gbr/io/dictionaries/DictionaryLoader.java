package fun.gbr.io.dictionaries;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Pattern;

import fun.gbr.options.Options;
import fun.gbr.options.Options.Mode;

/**
 * Classes implementing this interface must be able to return a dictionary.
 * The interface itself is in charge of choosing which class to use (TODO separate out into LoaderBuilder?)
 *
 */
public interface DictionaryLoader {
	public Map<String, String> get() throws IOException;
	
	/**
	 * @return A loader containing a dictionary map
	 * @throws IOException
	 */
	public static DictionaryLoader initialise() throws IOException {
		Path path = getDictionaryPath();
		boolean dictionaryExists = Files.exists(path);
		boolean generateRandom = Mode.decode.equals(Options.get().getMode()) 
				|| (!dictionaryExists && DO_RANDOMISE_VALUE.equals(System.getProperty(RANDOMISE_KEY)));
		if(!generateRandom) {
			System.out.println("Reading dictionary from \"" + path.toAbsolutePath() + "\"");
			return new DictionaryReader(path);
		}
		System.out.println("Writing new dictionary to \"" + path.toAbsolutePath() + "\"");
		return new DictionaryCreator(path);
	}
	
	/**
	 * @return The path for the dictionary set by the user
	 */
	private static Path getDictionaryPath() {
		String dic = System.getProperty(DICTIONARY_PATH_KEY);
		if(dic == null) {
			throw new IllegalArgumentException("Dictionary path not specified");
		}

		Path dictionaryPath = Path.of(dic);
		return dictionaryPath;
	}
	
	public static final String DICTIONARY_PATH_KEY = "dictionary_path";
	public static final String RANDOMISE_KEY = "generate_dictionary_if_absent";
	public static final String DO_RANDOMISE_VALUE = "true";
	public static final Pattern DICTIONARY_ENTRY_PATTERN = Pattern.compile("\"(.+)\"\\s+\"(.*)\"");
}
