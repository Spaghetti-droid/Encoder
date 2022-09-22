package fun.gbr.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import fun.gbr.options.Options;

/**
 * Writes text to a file
 *
 */
public class FileReturner implements Returner {
	
	private Path output = Options.get().getOutput();
	
	public FileReturner() {}
	
	public FileReturner(Path output) {
		this.output = output;
	}

	@Override
	public void writeOut(String encoded) {
		// No point throwing exception as normal exception handling depends on writeOut
		if(!Files.exists(output) || Files.isWritable(output)) {
			try {
				Files.writeString(output, encoded, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
			} catch (IOException e) {
				System.err.println("Can't write to " + output +"! " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			System.err.println("Can't write result to " + output + "! Not writeable!");
		}
	}

}
