package fun.gbr.encoders;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToMorseEncoder implements Encoder {

	@Override
	public String convert(String text) {
		StringBuilder builder = new StringBuilder();
		for(char c : text.toCharArray()) {
			builder.append(TEXT_VS_MORSE.get(c));
		}
		
		return builder.toString();
	}

	private static final Path DICTIONARY_PATH = Path.of("dictionaries", "morse");
	private static final Pattern DICTIONARY_ENTRY_PATTERN = Pattern.compile("(\\w) ([.-]+)");
	private static final SortedMap<Character, String> TEXT_VS_MORSE = new TreeMap<>();
	static {
		try {
			if (Files.isReadable(DICTIONARY_PATH)) {
				List<String> lines = Files.lines(DICTIONARY_PATH).toList();
				for (String line : lines) {
					Matcher matcher = DICTIONARY_ENTRY_PATTERN.matcher(line);
					if (matcher.matches()) {
						TEXT_VS_MORSE.put( matcher.group(1).charAt(0), matcher.group(2));
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to read dictionary at path: " + DICTIONARY_ENTRY_PATTERN, e);
		}
	}

}
