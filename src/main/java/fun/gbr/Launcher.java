package fun.gbr;

import java.io.IOException;

import fun.gbr.encoders.EncoderSelector;
import fun.gbr.io.FetcherFactory;
import fun.gbr.io.ReturnerFactory;
import fun.gbr.options.Options;
import fun.gbr.options.Options.Mode;

/**
 * A modular encoder with no ui
 * 
 * TODO
 * - Hierarchical option file handling. 1 main and 1 per encoder
 * - Comment
 * - logs
 * - more encoders
 * - decoders
 *
 */
public class Launcher {

	public static void main(String[] args) throws IOException {
		
		// Read input
		
		String text = FetcherFactory.build().getInput();
		System.out.println("Input: " + text);
		
		// Call encoder
		
		String encoded = EncoderSelector.build().convert(text);
		System.out.println((Mode.encode.equals(Options.get().getMode()) ? "Encoded as " : "Decoded to ") + encoded);
		
		// Output encoded

		System.out.println("Writing to " + Options.get().getOutput().toAbsolutePath());
		
		ReturnerFactory.build().writeOut(Options.get().getMode() + " - " + Options.get().getEncoderKey() + " : " + encoded + '\n');
		
		System.out.println("Done");
	}

}
