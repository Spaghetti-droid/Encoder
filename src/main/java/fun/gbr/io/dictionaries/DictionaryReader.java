package fun.gbr.io.dictionaries;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import fun.gbr.options.Options;
import fun.gbr.options.Options.Mode;

/**
 * Reads dictionary file and loads its data
 *
 */
public class DictionaryReader implements DictionaryLoader {

	private Map<String, String> dictionary;
	private Path path;
	
	public DictionaryReader(Path path) throws IOException {
		this.path = path;
		if(!Files.isReadable(path)) {
			throw new IllegalArgumentException("Dictionary not readable: " + path.toAbsolutePath());
		}
		loadDictionary();
	}

	/** Read and parse the dictionary from file
	 * @throws IOException
	 */
	private void loadDictionary() throws IOException {
		List<String> lines = Files.readAllLines(path);
		int keyGroup;
		int valueGroup;
		if(Mode.encode.equals(Options.get().getMode())) {
			keyGroup = 1;
			valueGroup = 2;
		} else {
			keyGroup = 2;
			valueGroup = 1;
		}
		this.dictionary = new HashMap<>(lines.size());
		for(String line : lines) {
			Matcher matcher = DICTIONARY_ENTRY_PATTERN.matcher(line);
			if(matcher.matches()) {
				this.dictionary.put(matcher.group(keyGroup), matcher.group(valueGroup));
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).warning(() -> "Unparsed line in dictionary: " + line);
			}
		}
	}

	@Override
	public Map<String, String> get() throws IOException {
		return this.dictionary;
	}

}
