package fun.gbr.options;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Represents the main options that will always be needed
 *
 */
public class Options {
	
	// Singleton management
	
	private static Options singleton; 
	
	/**
	 * Load and parse options
	 */
	public static void init() {
		init(null);
	}
	
	/**
	 * Load and parse options
	 */
	public static void init(LoggerOptionKeys lok) {
		try {
			OptionParser parser;
			if(lok == null) {
				parser = new OptionParser();
			} else {
				parser = new OptionParser(lok.fileKey, lok.levelKey);
			}
			singleton = parser.parse(OPTION_FILE_PATH);
			Logger.getLogger(Options.class.getCanonicalName()).info(() -> "Loaded " + singleton);
		} catch (IOException e) {
			throw new OptionLoadingException("Failed to load options on path: " + OPTION_FILE_PATH.toAbsolutePath(), e);
		}
	}
	
	/**
	 * @return Options object
	 * @throws IllegalStateException if Options hasn't been initialised
	 */
	public static Options get() {
		if(singleton == null) {
			throw new IllegalStateException("Options not initialised!");
		}
		return singleton;
	}
	
	// Options object
	
	private Path input = DEFAULT_INPUT;
	private Path output = DEFAULT_OUTPUT;
	private String encoder;	
	private Mode mode;
	private Charset charset = StandardCharsets.UTF_8;
	
	public Charset charset() {
		return charset;
	}
	
	public boolean decode() {
		return Mode.decode.equals(mode);
	}	
	public boolean encode() {
		return Mode.encode.equals(mode);
	}
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

	public enum Mode{
		encode,
		decode
	}
	
	public static class OptionLoadingException extends RuntimeException{

		public OptionLoadingException(String message, Throwable cause, boolean enableSuppression,
				boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}

		public OptionLoadingException(String message, Throwable cause) {
			super(message, cause);
		}

		public OptionLoadingException(String message) {
			super(message);
		}

		public OptionLoadingException(Throwable cause) {
			super(cause);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 4246160215969899495L;
		
	}
	
	public static record LoggerOptionKeys(String fileKey, String levelKey) {}
}
