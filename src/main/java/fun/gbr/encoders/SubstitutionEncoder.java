package fun.gbr.encoders;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import fun.gbr.io.dictionaries.DictionaryLoader;

public class SubstitutionEncoder implements Encoder {

	private String def;
	private Map<String, String> dictionary;
	
	public SubstitutionEncoder() throws IOException {
		this.dictionary = DictionaryLoader.getLoader().load().get();
		initDefaultString();
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

	private static final String DEFAULT_KEY = "default_if_unknown";
	private static final Pattern DEF_KEY_PATTERN = Pattern.compile("\"(.+)\"");
	
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
