package fun.gbr.encoders;

import java.util.logging.Logger;

import fun.gbr.options.Options;
import fun.gbr.options.Options.Mode;

/**
 * A shift encoder. Shifts all characters around the alphabet by an equal amount
 *
 */
public class ShiftEncoder implements Encoder {

	private int shift = 1;
	private char minChar = 0;
	private char maxChar = (char) Integer.MAX_VALUE;

	public ShiftEncoder() {
		if (Options.get().property(AMOUNT_KEY) != null) {
			shift = Integer.valueOf(Options.get().property(AMOUNT_KEY));
		}
		
		if(Mode.decode.equals(Options.get().mode())) {
			shift = -shift;
		}

		if (Options.get().property(MIN_CHAR_KEY) != null) {
			minChar = Options.get().property(MIN_CHAR_KEY).charAt(0);
		}

		if (Options.get().property(MAX_CHAR_KEY) != null) {
			maxChar = Options.get().property(MAX_CHAR_KEY).charAt(0);
		}
		
		if(minChar>maxChar) {
			Logger.getLogger(this.getClass().getCanonicalName()).fine("Max char below min char. Swapping them!");
			char temp = minChar;
			minChar = maxChar;
			maxChar = temp;
		}		
	}
	
	@Override
	public byte[] convert(byte[] bytes) throws Exception {		
		String text = new String(bytes, Options.get().charset());
		StringBuilder builder = new StringBuilder(text.length());
		text.codePoints().map(this::shiftChar).forEach(builder::appendCodePoint);
		return builder.toString().getBytes(Options.get().charset());
	}
	
	/** Shift a code point
	 * @param c
	 * @return
	 */
	private int shiftChar(int c) {
		c = c + shift;
		if(c < minChar || c > maxChar) {
			if( c > 0 ) {
				c = minChar + Math.floorMod(c, maxChar-minChar);
			} else {
				c = maxChar + Math.floorMod(c, maxChar-minChar);
			}
		}
		return c;
	}
	

	@Override
	public String getName() {
		return "SHIFT";
	}

	public static final String AMOUNT_KEY = "shift/amount";
	public static final String MIN_CHAR_KEY = "shift/min_char";
	public static final String MAX_CHAR_KEY = "shift/max_char";
}
