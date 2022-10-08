package fun.gbr.encoders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import fun.gbr.io.dictionaries.DictionaryLoader;
import fun.gbr.options.Options;

/**
 * Encodes using substitution
 *
 */
public class SubstitutionEncoder implements Encoder {

	/**
	 * The value to use by default if a key isn't in the dictionary
	 * null if no substitution should be made in that case 
	 */
	private CodePointList defaultSub;
	private int maxKeySize;
	private int minKeySize;
	private Map<CodePointList, CodePointList> dictionary;

	public SubstitutionEncoder() throws IOException {
		makeCPDictionary();		
		initDefaultString();
	}

	/** Load the dictionary to use
	 * @throws IOException
	 */
	private void makeCPDictionary() throws IOException{
		Map<String, String> textDic = DictionaryLoader.initialise().get();
		this.dictionary = new HashMap<>(textDic.size());
		this.minKeySize = 1;
		this.maxKeySize = 1;
		for(Entry<String, String> entry : textDic.entrySet()) {
			
			// Make code point version of dictionary
			
			var dicKey = new CodePointList(entry.getKey());
			this.dictionary.put(dicKey, new CodePointList(entry.getValue()));
			
			// Record max and min key size
			
			int keyLength = dicKey.size();
			if(keyLength > this.maxKeySize) {
				this.maxKeySize = keyLength;
			} else if(keyLength < this.minKeySize) {
				this.minKeySize = keyLength;
			}
		}
	}

	/**
	 * Get the default value from properties
	 */
	private void initDefaultString() {
		Matcher matcher = DEF_KEY_PATTERN.matcher(Options.get().property(DEFAULT_CHAR_KEY, ""));
		if(matcher.matches()) {
			this.defaultSub = new CodePointList(matcher.group(1));
		}
	}

	@Override
	public byte[] convert(byte[] bytes) throws Exception {
		String text = new String(bytes, Options.get().charset());
		List<Integer> converted = consumeAndConvert( text.codePoints().boxed().collect(Collectors.toList()) );
		StringBuilder builder = new StringBuilder(converted.size());
		converted.stream().forEach(builder::appendCodePoint);
		return builder.toString().getBytes(Options.get().charset());		
	}
	
	/** Consumes codePoints to output a converted equivalent
	 * @param codePoints WARNING! Will be emptied!
	 * @return The converted code points
	 */
	private List<Integer> consumeAndConvert(List<Integer> codePoints){		
		List<Integer> converted = new ArrayList<>(codePoints.size()); 
		while(convertNextPattern(codePoints, converted));
		return converted;		
	}
	
	/** Removes the next key in source, converts it, and appends it to converted
	 * @param mutableSource WARNING! Will be consumed by this method!
	 * @param converted	Array to append to
	 * @return true if conversion happened, false if source is empty
	 */
	private boolean convertNextPattern(List<Integer> mutableSource, List<Integer> converted) {
		int srcSize = mutableSource.size();
		if(srcSize == 0) {
			return false;
		}

		int keySize = (srcSize < maxKeySize) ? srcSize : maxKeySize;	
		CodePointList sub = null;
		for(; keySize>=minKeySize; keySize--) {
			sub = this.dictionary.get(new CodePointList(mutableSource.subList(0, keySize)));
			if(sub != null) {
				for(int i=keySize-1; i>=0; i--) {
					mutableSource.remove(i);
				}
				converted.addAll(sub.getCodePoints());
				return true;
			}
		}

		Integer nextInt = mutableSource.remove(0);
		if(defaultSub == null) {
			converted.add(nextInt);
		} else {
			converted.addAll(defaultSub.getCodePoints());
		}
		return true;
	}
	

	@Override
	public String getName() {
		return "SUB";
	}

	private static final String DEFAULT_CHAR_KEY = "sub/default_character";
	private static final Pattern DEF_KEY_PATTERN = Pattern.compile("\"(.+)\"");

	/**
	 * Holds a code point list for mapping and comparison purposes
	 *
	 */
	private static class CodePointList{
		List<Integer> codePoints;
		public CodePointList(String text) {
			if(text == null) {
				text = "";
			}

			this.codePoints = text.codePoints().boxed().toList();
		}
		
		public CodePointList(List<Integer> codePoints) {
			super();
			this.codePoints = codePoints;
		}

		private int size() {
			return codePoints.size();
		}

		public List<Integer> getCodePoints() {
			return codePoints;
		}

		@Override
		public int hashCode() {
			return Objects.hash(codePoints);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CodePointList other = (CodePointList) obj;
			return Objects.equals(codePoints, other.codePoints);
		}
	}
}
	
