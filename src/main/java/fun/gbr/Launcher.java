package fun.gbr;

import java.util.logging.Level;
import java.util.logging.Logger;

import fun.gbr.encoders.EncoderSelector;
import fun.gbr.io.FetcherFactory;
import fun.gbr.io.ReturnerFactory;
import fun.gbr.options.Options;
import fun.gbr.options.Options.Mode;

/**
 * A modular encoder with no ui
 * 
 * TODO
 * - Add encoder specific prefixes to their respective options
 * - RSA Key generator only needs some options. Implement partial/progressive loading.
 * - Flexible option file handling allowing several option files if desired
 * - Store encoder options in dedicated map, not system properties (Forces option singleton loading)?
 * - Issue: SubstitutionEncoder is not reliable when converting from patterns containing more than one character
 *
 */
public class Launcher {	

	private static final Logger LOGGER = Logger.getLogger(Launcher.class.getCanonicalName());

	public static void main(String[] args) {			
		if(!Utils.initProgram()) {
			return;
		}
		try {
			
			// Read input

			String text = FetcherFactory.build().getInput();
			LOGGER.info(() -> "Input: " + text);

			// Call encoder

			String encoded = EncoderSelector.build().convert(text);
			LOGGER.info(() -> (Mode.encode.equals(Options.get().getMode()) ? "Encoded as " : "Decoded to ") + encoded);

			// Output encoded

			LOGGER.info(() -> "Writing to " + Options.get().getOutput().toAbsolutePath());
			ReturnerFactory.build().writeOut(makeOperationLabel()  + " : " + encoded + '\n');

			LOGGER.info("Done");
			
		} catch(Exception e) {
			String errorLabel = "Error! (" + makeOperationLabel() + ")";
			ReturnerFactory.build().writeOut(errorLabel + ": " + e.getMessage() + '\n');
			LOGGER.log(Level.SEVERE, errorLabel, e);
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
