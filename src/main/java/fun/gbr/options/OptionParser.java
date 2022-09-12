package fun.gbr.options;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OptionParser {	
	
	public static Options parse(Path optionFilePath) throws IOException {
		List<String> lines = Files.lines(optionFilePath)
				.toList();
		
		Options opt = new Options();
		for(String line : lines) {
			Matcher matcher = OPTION_PATTERN.matcher(line);
			if(matcher.matches()) {
				setOption(matcher.group(1), matcher.group(2), opt);
			}
		}
		
		return opt;		
	}
	
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

}
