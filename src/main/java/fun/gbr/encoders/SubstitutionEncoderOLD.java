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
import fun.gbr.options.Options;

/**
 * Encodes using substitution
 *
 */
public class SubstitutionEncoderOLD implements Encoder {

	/**
	 * The value to use by default if a key isn't in the dictionary
	 * null if no substitution should be made in that case 
	 */
	private String defaultSub;
	private Map<String, String> dictionary;
	
	public SubstitutionEncoderOLD() throws IOException {
		this.dictionary = DictionaryLoader.initialise().get();
		initDefaultString();
	}
	
	/**
	 * Get the default value from properties
	 */
	private void initDefaultString() {
		Matcher matcher = DEF_KEY_PATTERN.matcher(Options.get().property(DEFAULT_CHAR_KEY, ""));
		if(matcher.matches()) {
			this.defaultSub = matcher.group(1);
		}
	}
	
//	@Override
//	public String convert(String text) {
//		var subH = new SubstitutionHandler(text);
//		var dicIter = this.dictionary.entrySet().iterator();
//		while(!subH.isDone() && dicIter.hasNext()) {
//			var entry = dicIter.next();
//			subH.substitute(entry.getKey(), entry.getValue());
//		}
//		
//		return subH.getEncoded(defaultSub);
//	}
	
	@Override
	public byte[] convert(byte[] bytes) throws Exception {
		
		// Use stream to convert Map<String, String> to Map<byte[], byte[]> ( but slightly more complicated, because the byte arrays need to be compared)
		// iterate through byte array, checking blocks of bytes vs map (biggest key to smallest). If bloc not found advance 2 bytes (1 char), keeping those 2.
		
		throw new IllegalStateException("Not implemented");
	}
	
	@Override
	public String getName() {
		return "SUB";
	}

	private static final String DEFAULT_CHAR_KEY = "sub/default_character";
	private static final Pattern DEF_KEY_PATTERN = Pattern.compile("\"(.+)\"");
	
	/**
	 * Handles and stores substitutions
	 *
	 */
	private static class SubstitutionHandler{
		
		private String text;
		private List<Integer> positions;
		private SortedMap<Integer, String> positionVsNewValue;
		/**
		 * @param text The String to be transformed
		 */
		public SubstitutionHandler(String text) {
			super();
			this.text = text;
			this.positions = IntStream.rangeClosed(0, text.length()-1)
								.boxed().collect(Collectors.toList());
			this.positionVsNewValue = new TreeMap<>();
		}
		
		/**
		 * @return true if all text has been transformed
		 */
		public boolean isDone() {
			return text.isEmpty();
		}
		
		/** Substitute all instances of <from> in the text with <to>
		 * TODO WARNING If <from> has more than 1 character, 
		 *  there is a risk of substitutions happening on the overlap of 2 keys!
		 * @param from
		 * @param to
		 */
		public void substitute(String from, String to) {
			int start;
			while((start = text.indexOf(from)) != -1) {				
				positionVsNewValue.put(positions.get(start), to);
				for(int i = start+from.length()-1; i>=start; --i) { //backwards to avoid out of bounds
					positions.remove(i);
				}
				text = text.replaceFirst(Pattern.quote(from), "");
			}
		}
		
		/** Return the transformed version of the initial text
		 * Note calling this before isDone() == true will result in an incomplete transformation
		 * @param def
		 * @return
		 */
		public String getEncoded(String def) {
			// forOut is positionVsNewValue with unprocessed text added in
			SortedMap<Integer, String> forOut = makeOutputMap(def);			
			// Each component of the new string is associated to a position in text
			// In the end, all we need to do is stick everything together in order
			StringBuilder encoded = new StringBuilder();
			forOut.forEach((k,v) -> encoded.append(v));
			
			return encoded.toString();
		}
		
		/**
		 * Adds unprocessed text to textPosToTransformed
		 */
		private void handleUnknowns(SortedMap<Integer, String> textPosToTransformed) {
			for(int i=0; i<text.length(); i++) {
				textPosToTransformed.put(positions.get(i), String.valueOf(text.charAt(i)));
			}
		}
		
		/** Adds def as a value for all positions which were not transformed to textPosToTransformed
		 * @param def
		 * @param textPosToTransformed
		 */
		private void handleUnknowns(String def, SortedMap<Integer, String> textPosToTransformed) {
			for(int i=0; i<text.length(); i++) {
				textPosToTransformed.put(positions.get(i), def);
			}
		}
		
		/**
		 * @param def
		 * @return a copy of positionVsNewValue containing extra elements for any unprocessed text
		 */
		private SortedMap<Integer, String> makeOutputMap(String def){
			SortedMap<Integer, String> forOut = new TreeMap<>(positionVsNewValue);
			if(text.isEmpty()) {
				return forOut;
			}
			if(def == null) {
				handleUnknowns(forOut);
			} else {
				handleUnknowns(def, forOut);			
			}
			
			return forOut;
		}		
	}
}
