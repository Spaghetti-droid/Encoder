package fun.gbr;

import fun.gbr.encoders.EncoderFactory;
import fun.gbr.io.FetcherFactory;
import fun.gbr.io.ReturnerFactory;
import fun.gbr.options.OptionManager;
import fun.gbr.options.Options.Mode;

/**
 * A minimalist encoder
 * 
 * TODO
 * - Comment
 * - logs
 * - more encoders
 * - Random sub cipher?
 * - decoders
 * - Separate encoder options as from-to?
 * - create moduled encoder which generates conversion map on the fly based on Unicode equivalent?
 *
 */
public class Launcher {

	public static void main(String[] args) {
		
		// Read input
		
		String text = FetcherFactory.build().getInput();
		System.out.println("Input: " + text);
		
		// Call encoder
		
		String encoded = EncoderFactory.build().convert(text);
		System.out.println(Mode.encode.equals(OptionManager.get().getMode()) ? "Encoded as " : "Decoded to " + encoded);
		
		// Output encoded

		System.out.println("Writing to " + OptionManager.get().getOutput().toAbsolutePath());
		
		ReturnerFactory.build().writeOut(OptionManager.get().getEncoderKey() + " : " + encoded + '\n');
		
		System.out.println("Done");
	}

}
