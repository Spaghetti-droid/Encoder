package fun.gbr.io.dictionaries;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import fun.gbr.tools.SubstitutionDictionaryMaker;

public class RandomisingDictionaryLoader implements DictionaryLoader {
	
	private static Random rand = new Random();
	private Map<String, String> dictionary;
	private Pair keyPair;
	private Pair valuePair;

	public RandomisingDictionaryLoader() {
		this.keyPair = parseRange(System.getProperty(KEY_RANGE_KEY));
		this.valuePair = parseRange(System.getProperty(VALUE_RANGE_KEY));	
		this.dictionary = new HashMap<>(keyPair.max()-keyPair.min());
	}
	
	private static Pair parseRange(String range) {
		if(range != null) {
			Matcher matcher = RANGE_PATTERN.matcher(range);
			if(matcher.matches()) {
				int min = Integer.valueOf(matcher.group(1));
				int max = Integer.valueOf(matcher.group(2));
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
	
	@Override
	public DictionaryLoader load() throws IOException {
		
		// Get and check dictionary path
		
		Path dictionaryPath = getDictionaryPath();
		
		// Fill dictionary
		
		List<Integer> values = IntStream.rangeClosed(valuePair.min(), valuePair.max())
								.boxed()
								.collect(Collectors.toList());
		for(int i=keyPair.min(); i<keyPair.max()+1; i++) {
			int nextVal = values.remove(rand.nextInt(0, values.size()));
			this.dictionary.put(String.valueOf((char) i), String.valueOf((char) nextVal));
		}
		
		// Write
		
		SubstitutionDictionaryMaker.write(dictionary, dictionaryPath);
		return this;
	}
	
	@Override
	public Path getDictionaryPath() {
		Path path = DictionaryLoader.super.getDictionaryPath();
		if(Files.exists(path) && !Files.isWritable(path)) {
			throw new IllegalArgumentException("Dictionary not writable: " + path.toAbsolutePath());
		}
		return path;
	}

	@Override
	public Map<String, String> get() throws IOException {
		return this.dictionary;
	}
	
	private static final String KEY_RANGE_KEY = "rand_key_range";
	private static final String VALUE_RANGE_KEY = "rand_value_range";
	
	private static final Pattern RANGE_PATTERN = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)");
	
	private static record Pair(int min, int max) {}

}
