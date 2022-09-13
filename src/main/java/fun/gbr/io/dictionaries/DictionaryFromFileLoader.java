package fun.gbr.io.dictionaries;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import fun.gbr.options.OptionManager;
import fun.gbr.options.Options.Mode;

public class DictionaryFromFileLoader implements DictionaryLoader {

	private Map<String, String> dictionary;

	@Override
	public DictionaryLoader load() throws IOException {
		loadDictionary(getDictionaryPath());
		return this;
	}
	
	@Override
	public Path getDictionaryPath() {
		Path path = DictionaryLoader.super.getDictionaryPath();
		if(!Files.isReadable(path)) {
			throw new IllegalArgumentException("Dictionary not readable: " + path.toAbsolutePath());
		}
		return path;
	}

	private void loadDictionary(Path dictionaryPath) throws IOException {
		List<String> lines = Files.readAllLines(dictionaryPath);
		int keyGroup;
		int valueGroup;
		if(Mode.encode.equals(OptionManager.get().getMode())) {
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
				System.err.println("Unparsed line in dictionary: " + line);
			}
		}
	}

	@Override
	public Map<String, String> get() throws IOException {
		return this.dictionary;
	}

}
