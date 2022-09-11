package fun.gbr;

import fun.gbr.io.FetcherFactory;
import fun.gbr.io.ReturnerFactory;
import fun.gbr.logic.EncoderFactory;
import fun.gbr.options.OptionManager;

/**
 * A minimalist encoder
 * 
 * TODO
 * - Get dictionaries set up (symbol to unicode or ascii, see below)
 * - logs
 * - more encoders
 * - Separate encoder options as from-to
 * - create moduled encoder which generates conversion map on the fly based on Unicode equivalent
 *
 */
public class Launcher {

	public static void main(String[] args) {
		
		// Read input
		
		String text = FetcherFactory.build().getInput();
		System.out.println("Input: " + text);
		
		// Call encoder
		
		String encoded = EncoderFactory.build().convert(text);
		System.out.println("Encoded as: " + encoded);
		
		// Output encoded

		System.out.println("Writing to " + OptionManager.get().getOutput().toAbsolutePath());
		
		ReturnerFactory.build().writeOut(OptionManager.get().getEncoderKey() + " : " + encoded + '\n');
		
		System.out.println("Done");
	}

}
