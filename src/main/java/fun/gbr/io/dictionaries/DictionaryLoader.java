package fun.gbr.io.dictionaries;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Pattern;

import fun.gbr.options.OptionManager;
import fun.gbr.options.Options.Mode;

public interface DictionaryLoader {
	public DictionaryLoader load() throws IOException;
	public Map<String, String> get() throws IOException;
	
	public static DictionaryLoader getLoader() {
		boolean generateRandom = DO_RANDOMISE_VALUE.equals(System.getProperty(RANDOMISE_KEY));
		if(!generateRandom || Mode.decode.equals(OptionManager.get().getMode())) {
			return new DictionaryFromFileLoader();
		}
		
		return new RandomisingDictionaryLoader();
	}
	
	default Path getDictionaryPath() {
		String dic = System.getProperty(DICTIONARY_PATH_KEY);
		if(dic == null) {
			throw new IllegalArgumentException("Dictionary path not specified");
		}

		Path dictionaryPath = Path.of(dic);
		return dictionaryPath;
	}
	
	public static final String DICTIONARY_PATH_KEY = "dictionary_path";
	public static final String RANDOMISE_KEY = "generate_random_dictionary";
	public static final String DO_RANDOMISE_VALUE = "true";
	public static final Pattern DICTIONARY_ENTRY_PATTERN = Pattern.compile("\"(.+)\"\\s+\"(.*)\"");
}
