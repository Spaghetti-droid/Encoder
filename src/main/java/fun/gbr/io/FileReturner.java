package fun.gbr.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

import fun.gbr.options.Options;

/**
 * Writes text to a file
 *
 */
public class FileReturner implements Returner {
	
	private Path output = Options.get().output();
	
	public FileReturner() {}
	
	public FileReturner(Path output) {
		this.output = output;
	}
	
	@Override
	public void writeOut(String result) {
		writeOut(result.getBytes(Options.get().charset()));
	}
	
	@Override
	public void writeOut(byte[] result) {
		// No point throwing exception as normal exception handling uses writeOut
		if(!Files.exists(output) || Files.isWritable(output)) {
			try {
				Files.write(output, result, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
			} catch (IOException e) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e, () -> "Can't write to " + output +"! " + e.getMessage());
			}
		} else {
			Logger.getLogger(this.getClass().getCanonicalName()).severe(() -> "Can't write result to " + output + "! Not writeable!");
		}
	}
}
