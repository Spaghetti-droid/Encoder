package fun.gbr;

import java.util.logging.Level;
import java.util.logging.Logger;

import fun.gbr.encoders.EncoderSelector;
import fun.gbr.io.FetcherFactory;
import fun.gbr.io.Returner;
import fun.gbr.io.ReturnerFactory;
import fun.gbr.options.Options;
import fun.gbr.options.Options.Mode;

/**
 * A modular encoder with no ui
 * 
 * TODO
 * - make input fetcher read more gradually 
 * - Flexible option file handling allowing several option files if desired
 * - Add option to OTP to rsa-encode/decode key
 * - Implement unit testing
 * - See if there is an easy way to have multi executable compilation while keeping dependencies separate from jars
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

			byte[] bytes = FetcherFactory.build().getInput();
			LOGGER.info(() -> "Input: " + new String(bytes, Options.get().charset()));

			// Call encoder

			byte[] encoded = EncoderSelector.build().convert(bytes);
			LOGGER.info(() -> (Mode.encode.equals(Options.get().mode()) ? "Encoded as " : "Decoded to ") + new String(encoded, Options.get().charset()));

			// Output encoded

			// save bytes here
			LOGGER.info(() -> "Writing to " + Options.get().output().toAbsolutePath());
			Returner ret = ReturnerFactory.build();
			ret.writeOut(makeOperationLabel()  + " : ");
			ret.writeOut(encoded);
			ret.writeOut("\n");

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
		return Options.get().mode() + " - " + Options.get().encoderKey();
	}
}
