package fun.gbr.encoders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fun.gbr.options.Options;
import fun.gbr.options.Options.Mode;

/**
 * Encode to and from binary
 *
 */
public class BinaryEncoder implements Encoder {

	@Override
	public String convert(String text) throws IOException {
		if(Mode.decode.equals(Options.get().getMode())) {
			return decode(text);
		}
		return encode(text);
	}
	
	private static String decode(String text) {
		
		// Check if binary
		
		Matcher matcher = BINARY_PATTERN.matcher(text);
		if(!matcher.matches()) {
			throw new IllegalArgumentException("Input text is not binary: " + text);
		}
		
		// Remove leading and trailing white space
		
		text = matcher.group(1); 
		
		// Convert to BitSet
		
		BitSet bits = new BitSet(text.length());
		for(int i=0; i<text.length(); i++) {
			if(text.charAt(i) == '1') {
				bits.set(i);
			}
		}
		
		// Convert BitSet to bytes and then String
		
		byte[] bytes = bits.toByteArray();
		return new String(bytes, StandardCharsets.UTF_8);		
	}
	
	private static String encode(String text) {
		
		// Convert to BitSet
		
		BitSet bits = BitSet.valueOf(text.getBytes(StandardCharsets.UTF_8));
		
		// Write BitSet as String
		
		StringBuilder builder = new StringBuilder(bits.length());
		for(int i=0; i<bits.length(); i++) {
			builder.append(bits.get(i) ? 1 : 0);
		}	
		return builder.toString();
	}
	
	private static final Pattern BINARY_PATTERN = Pattern.compile("\\s*([01]+)\\s*"); 
}
