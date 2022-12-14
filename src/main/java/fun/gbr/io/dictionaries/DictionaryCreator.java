package fun.gbr.io.dictionaries;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import fun.gbr.Utils;
import fun.gbr.options.Options;
import fun.gbr.tools.SubstitutionDictionaryMaker;

/**
 * Generates a substitution dictionary and writes it to a file 
 *
 */
public class DictionaryCreator implements DictionaryLoader {
	
	private Map<String, String> dictionary;
	private Path path;
	private Pair keyPair;
	private Pair valuePair;

	public DictionaryCreator(Path path) throws IOException {
		this.path = path;
		this.keyPair = parseRange(Options.get().property(KEY_RANGE_KEY));
		this.valuePair = parseRange(Options.get().property(VALUE_RANGE_KEY));	
		this.dictionary = new HashMap<>(keyPair.max()-keyPair.min());
		generate();
	}
	
	/** Parses a user-input range
	 * @param range
	 * @return
	 */
	private static Pair parseRange(String range) {
		if(range != null) {
			Matcher matcher = RANGE_PATTERN.matcher(range);
			if(matcher.matches()) {
				int min = Integer.parseInt(matcher.group(1));
				int max = Integer.parseInt(matcher.group(2));
				if(min>max) {
					int temp = min;
					min = max;
					max = temp;
				}
				return new Pair(min, max);
			}
		}
		throw new IllegalArgumentException("Invalid range specification: " + range);
	}
	
	/** Generate a dictionary file
	 * @throws IOException
	 */
	private void generate() throws IOException {
		
		// Create dictionary
		
		Files.createFile(path);
		
		// Fill dictionary
		
		List<Integer> values = IntStream.rangeClosed(valuePair.min(), valuePair.max())
								.boxed()
								.collect(Collectors.toList());
		for(int i=keyPair.min(); i<keyPair.max()+1; i++) {
			int nextVal = values.remove(Utils.getRandom().nextInt(0, values.size()));
			this.dictionary.put(String.valueOf((char) i), String.valueOf((char) nextVal));
		}
		
		// Write
		
		SubstitutionDictionaryMaker.write(dictionary, path);
	}

	@Override
	public Map<String, String> get() throws IOException {
		return this.dictionary;
	}
	
	private static final String KEY_RANGE_KEY = "sub/rand_key_range";
	private static final String VALUE_RANGE_KEY = "sub/rand_value_range";
	
	private static final Pattern RANGE_PATTERN = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)");
	
	private static record Pair(int min, int max) {}

}
