package fun.gbr.encoders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SubstitutionEncoder implements Encoder {

	private String def;
	private Map<String, String> dictionary;
	
	public SubstitutionEncoder() throws IOException {
		loadDictionary(getDictionaryPath());
		initDefaultString();
	}
	
	private static Path getDictionaryPath() {
		String dic = System.getProperty(DICTIONARY_PATH_KEY);
		if(dic == null) {
			throw new IllegalArgumentException("Dictionary path not specified");
		}
		
		Path dictionaryPath = Path.of(dic);
		
		if(!Files.isReadable(dictionaryPath)) {
			throw new IllegalArgumentException("Dictionary not readable: " + dictionaryPath.toAbsolutePath());
		}
		
		return dictionaryPath;
	}
	
	private void loadDictionary(Path dictionaryPath) throws IOException {
		List<String> lines = Files.readAllLines(dictionaryPath);
		this.dictionary = new HashMap<>(lines.size());
		for(String line : lines) {
			Matcher matcher = DICTIONARY_ENTRY_PATTERN.matcher(line);
			if(matcher.matches()) {
				this.dictionary.put(matcher.group(1), matcher.group(2));
			} else {
				System.err.println("Unparsed line in dictionary: " + line);
			}
		}
	}
	
	private void initDefaultString() {
		Matcher matcher = DEF_KEY_PATTERN.matcher(System.getProperty(DEFAULT_KEY, ""));
		if(matcher.matches()) {
			this.def = matcher.group(1);
		}
	}
	
	@Override
	public String convert(String text) {
		var subH = new SubstitutionHandler(text);
		var dicIter = this.dictionary.entrySet().iterator();
		while(!subH.isDone() && dicIter.hasNext()) {
			var entry = dicIter.next();
			subH.substitute(entry.getKey(), entry.getValue());
		}
		
		return subH.getEncoded(def);
	}
	
	private static final String DICTIONARY_PATH_KEY = "dictionary_path";
	private static final String DEFAULT_KEY = "default_if_unknown";
	private static final Pattern DEF_KEY_PATTERN = Pattern.compile("\"(.+)\"");
	private static final Pattern DICTIONARY_ENTRY_PATTERN = Pattern.compile("\"(.+)\"\\s+\"(.*)\"");
	
	private static class SubstitutionHandler{
		
		private String orig;
		private List<Integer> positions;
		private SortedMap<Integer, String> positionVsTo;
		public SubstitutionHandler(String orig) {
			super();
			this.orig = orig;
			this.positions = IntStream.rangeClosed(0, orig.length()-1)
								.boxed().collect(Collectors.toList());
			this.positionVsTo = new TreeMap<>();
		}
		
		public boolean isDone() {
			return orig.isEmpty();
		}
		
		public void substitute(String from, String to) {
			int start;
			while((start = orig.indexOf(from)) != -1) {				
				positionVsTo.put(positions.get(start), to);
				for(int i = start+from.length()-1; i>=start; --i) { //backwards to avoid out of bounds
					positions.remove(i);
				}
				orig = orig.replaceFirst(Pattern.quote(from), "");
			}
		}
		
		public String getEncoded(String def) {
			if(!orig.isEmpty()) {
				if(def != null) {
					handleUnknowns(def);
				} else {
					handleUnknowns();
				}				
			}
			
			StringBuilder encoded = new StringBuilder();
			positionVsTo.forEach((k,v) -> encoded.append(v));
			
			return encoded.toString();
		}
		
		private void handleUnknowns() {
			for(int i=0; i<orig.length(); i++) {
				positionVsTo.put(positions.get(i), String.valueOf(orig.charAt(i)));
			}
		}
		
		private void handleUnknowns(String def) {
			for(int i=0; i<orig.length(); i++) {
				positionVsTo.put(positions.get(i), def);
			}
		}
		
	}

}
