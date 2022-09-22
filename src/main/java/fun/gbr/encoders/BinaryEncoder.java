package fun.gbr.encoders;

import java.nio.charset.StandardCharsets;
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
	public String convert(String text){
		if(Mode.decode.equals(Options.get().getMode())) {
			return decode(text);
		}
		return encode(text);
	}
	
	private static String decode(String text) {
		
		// Check if binary
		
		Matcher matcher = BINARY_PATTERN.matcher(text);
		if(!matcher.matches()) {
			throw new IllegalArgumentException("Input text is not binary!");
		}
		
		// Remove leading and trailing white space
		
		text = matcher.group(1); 
		
		// Convert to BitSet
		
		boolean[] bits = new boolean[text.length()];
		for(int i=0; i<text.length(); i++) {
			if(text.charAt(i) == '1') {
				bits[i] = true;
			}
		}
		
		// Convert BitSet to bytes and then String
		
		byte[] bytes = bitsToBytes(bits);
		return new String(bytes, StandardCharsets.UTF_8);		
	}
	
	private static String encode(String text) {
		
		// Convert to BitSet
		
		boolean[] bits = bytesToBits(text.getBytes(StandardCharsets.UTF_8));
		
		// Write BitSet as String
		
		StringBuilder builder = new StringBuilder(bits.length);
		for(int i=0; i<bits.length; i++) {
			builder.append(bits[i] ? 1 : 0);
		}	
		return builder.toString();
	}
	
	/** Turn array of bytes into array of booleans, each representing 1 bit
	 * byte representation is Big-endian
	 * @param bytes
	 * @return array of booleans representing the bytes' bits
	 */
	private static boolean[] bytesToBits(byte[] bytes) {
		int nBits = bytes.length *8;
		boolean[] bits = new boolean[nBits];
		for(int i=0; i<nBits; i++) {
			bits[i] = ((bytes[i/8] & (0b10000000>>(i%8))) != 0);
		}
		return bits;
	}
	
	/** Converts a big-endian bit array into bytes
	 * @param bits
	 * @return the byte array equivalent to the input bits
	 */
	private static byte[] bitsToBytes(boolean[] bits) {
		byte[] bytes = new byte[bits.length/8];
		for(int i=0; i<bits.length; i++) {
			if(bits[i]) {
				bytes[i/8] += Math.pow(2, 7-i%8);
			}
		}
		
		return bytes;
	}
	
	private static final Pattern BINARY_PATTERN = Pattern.compile("\\s*([01]+)\\s*"); 
	
}
