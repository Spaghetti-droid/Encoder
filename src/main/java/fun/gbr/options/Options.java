package fun.gbr.options;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Represents the main options that will always be needed
 *
 */
public class Options {
	
	// Singleton management
	
	private static Options SINGLETON; 
	
	public static Options get() {
		if(SINGLETON == null) {
			try {
				SINGLETON = OptionParser.parse(OPTION_FILE_PATH);
				System.out.println("Loaded " + SINGLETON);
			} catch (IOException e) {
				throw new RuntimeException("Failed to load options on path: " + OPTION_FILE_PATH.toAbsolutePath(), e);
			}
		}
		return SINGLETON;
	}
	
	// Options object
	
	private Path input = DEFAULT_INPUT;
	private Path output = DEFAULT_OUTPUT;
	private String encoder;	
	private Mode mode;
	
	
	public Path getInput() {
		return input;
	}
	public Path getOutput() {
		return output;
	}
	public String getEncoderKey() {
		return encoder;
	}
	public void setInput(Path input) {
		this.input = input;
	}
	public void setOutput(Path output) {
		this.output = output;
	}
	public void setEncoder(String encoder) {
		this.encoder = encoder;
	}
	
	public Mode getMode() {
		return mode;
	}
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	public void setMode(String mode) {
		this.mode = Mode.valueOf(mode);
	}

	@Override
	public String toString() {
		return "Options [input=" + input + ", output=" + output + ", encoder=" + encoder + ", mode=" + mode + "]";
	}

	private static final Path DEFAULT_INPUT = Path.of("input.txt");
	private static final Path DEFAULT_OUTPUT = Path.of("output.txt");
	private static final Path OPTION_FILE_PATH = Path.of("options.txt");

	public static enum Mode{
		encode,
		decode
	}
}
