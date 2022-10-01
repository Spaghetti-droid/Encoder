package fun.gbr.options;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the user option file 
 *
 */
public class OptionParser {	
	
	private OptionParser() {}
	
	/** Parse file options into an options object and the system properties
	 * @param optionFilePath
	 * @return
	 * @throws IOException
	 */
	public static Options parse(Path optionFilePath) throws IOException {
		List<String> lines = Files.readAllLines(optionFilePath);
		
		Options opt = new Options();
		for(String line : lines) {
			Matcher matcher = OPTION_PATTERN.matcher(line);
			if(matcher.matches()) {
				setOption(matcher.group(1), matcher.group(2), opt);
			}
		}
		
		return opt;		
	}
	
	/** Records the option given by key and value.
	 * @param key
	 * @param value
	 * @param opt
	 */
	private static void setOption(String key, String value, Options opt) {
		switch (key) {
		case INPUT_KEY: 
			opt.setInput(Path.of(value));
			break;
		case OUTPUT_KEY:
			opt.setOutput(Path.of(value));
			break;
		case ENCODER_KEY:
			opt.setEncoder(value);
			break;
		case MODE_KEY:
			opt.setMode(value);
			break;
		case LOG_FILE_KEY:
			LoggerHandler.addLogFile(value);
			break;
		case LOG_LEVEL_KEY:
			LoggerHandler.setLevel(value);
			break;
		default:
			// Treat as encoder specific option
			System.setProperty(key, value);
		}
	}
	
	
	private static final Pattern OPTION_PATTERN = Pattern.compile("(\\w+)\\s*:\\s*([^\\s].*[^\\s])\\s*");
	private static final String INPUT_KEY = "input";
	private static final String OUTPUT_KEY = "output";
	private static final String ENCODER_KEY = "encoder";
	private static final String MODE_KEY = "mode";
	private static final String LOG_FILE_KEY = "log_file";
	private static final String LOG_LEVEL_KEY = "log_level";
}
