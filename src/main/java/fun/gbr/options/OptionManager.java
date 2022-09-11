package fun.gbr.options;

import java.io.IOException;
import java.nio.file.Path;

public class OptionManager {
	
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
	
	private static Path OPTION_FILE_PATH = Path.of("options.txt");
		
}
