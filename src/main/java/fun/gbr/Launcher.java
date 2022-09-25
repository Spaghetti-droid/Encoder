package fun.gbr;

import fun.gbr.encoders.EncoderSelector;
import fun.gbr.io.FetcherFactory;
import fun.gbr.io.ReturnerFactory;
import fun.gbr.options.Options;
import fun.gbr.options.Options.Mode;

/**
 * A modular encoder with no ui
 * 
 * TODO
 * - RSA Key generator only needs some options. Implement partial/progressive loading.
 * - Flexible option file handling allowing several option files if desired
 * - Store encoder options in dedicated map, not system properties (Forces option singleton loading)?
 * - logs
 *
 */
public class Launcher {

	public static void main(String[] args) {
		
		try {
			// Read input

			String text = FetcherFactory.build().getInput();
			System.out.println("Input: " + text);

			// Call encoder

			String encoded = EncoderSelector.build().convert(text);
			System.out.println((Mode.encode.equals(Options.get().getMode()) ? "Encoded as " : "Decoded to ") + encoded);

			// Output encoded

			System.out.println("Writing to " + Options.get().getOutput().toAbsolutePath());
			ReturnerFactory.build().writeOut(makeOperationLabel()  + " : " + encoded + '\n');

			System.out.println("Done");
			
		} catch(Exception e) {
			ReturnerFactory.build().writeOut("Error! (" + makeOperationLabel() + "): " + e.getMessage() + '\n');
			e.printStackTrace();
		}
	}
	
	/**
	 * @return A label identifying the operation performed
	 * 	during this run of the encoder.
	 */
	private static String makeOperationLabel() {
		return Options.get().getMode() + " - " + Options.get().getEncoderKey();
	}

}
