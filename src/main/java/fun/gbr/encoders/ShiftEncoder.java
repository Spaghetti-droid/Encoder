package fun.gbr.encoders;

import fun.gbr.options.OptionManager;
import fun.gbr.options.Options.Mode;

/**
 * A 1-shift encoder
 *
 */
public class ShiftEncoder implements Encoder {

	private int shift = 1;
	private char minChar = 0;
	private char maxChar = (char) Integer.MAX_VALUE;

	public ShiftEncoder() {
		if (System.getProperty(SHIFT_KEY) != null) {
			shift = Integer.valueOf(System.getProperty(SHIFT_KEY));
		}
		
		if(Mode.decode.equals(OptionManager.get().getMode())) {
			shift = -shift;
		}

		if (System.getProperty(MIN_CHAR_KEY) != null) {
			minChar = System.getProperty(MIN_CHAR_KEY).charAt(0);
		}

		if (System.getProperty(MAX_CHAR_KEY) != null) {
			maxChar = System.getProperty(MAX_CHAR_KEY).charAt(0);
		}
		
		if(minChar>maxChar) {
			System.out.println("Min char below max char. Swapping them!");
			char temp = minChar;
			minChar = maxChar;
			maxChar = temp;
		}		
	}

	@Override
	public String convert(String text) {
		StringBuilder builder = new StringBuilder();
		text.chars().map(c -> {
			c = c + shift;
			if(c < minChar || c > maxChar) {
				if( c > 0 ) {
					c = minChar + Math.floorMod(c, maxChar-minChar);
				} else {
					c = maxChar + Math.floorMod(c, maxChar-minChar);
				}
			}
			return c;
		}).forEach(c -> builder.appendCodePoint(c));
		return builder.toString();
	}

	public static final String SHIFT_KEY = "shift";
	public static final String MIN_CHAR_KEY = "min_char";
	public static final String MAX_CHAR_KEY = "max_char";

}
