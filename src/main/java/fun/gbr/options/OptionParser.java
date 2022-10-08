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
	
	private String logFileKey;
	private String logLevelKey;
	
	public OptionParser() {
		this(null, null);
	}
	
	public OptionParser(String logFileKey, String logLevelKey) {
		super();
		this.logFileKey = logFileKey == null ? LOG_FILE_KEY : logFileKey;
		this.logLevelKey = logLevelKey == null ? LOG_LEVEL_KEY : logLevelKey;
	}



	/** Parse file options into an options object and the system properties
	 * @param optionFilePath
	 * @return
	 * @throws IOException
	 */
	public Options parse(Path optionFilePath) throws IOException {		
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
	private void setOption(String key, String value, Options opt) {		
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
		default:			
			if(!setLogOption(key, value)) {
				// Treat as encoder specific option
				opt.setProperty(key, value);
			}
		}
	}
	
	
	/** Sets value as a log option if key corresponds to one
	 * @param key
	 * @param value
	 * @return true if key was a log option, false otherwise
	 */
	private boolean setLogOption(String key, String value) {
		if(logFileKey.equals(key)) {
			LoggerHandler.addLogFile(value);
			return true;
		}
		if(logLevelKey.equals(key)) {
			LoggerHandler.setLevel(value);
			return true;
		}
		
		return false;
	}
	
	// Matches [<a_prefix>/]<opt_name> : <opt_value>
	private static final Pattern OPTION_PATTERN = Pattern.compile("(\\w+\\/?\\w+)\\s*:\\s*([^\\s].*[^\\s]|[^\\s])\\s*");
	private static final String INPUT_KEY = "input";
	private static final String OUTPUT_KEY = "output";
	private static final String ENCODER_KEY = "encoder";
	private static final String MODE_KEY = "mode";
	private static final String LOG_FILE_KEY = "log_file";
	private static final String LOG_LEVEL_KEY = "log_level";
}
