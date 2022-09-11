package fun.gbr.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import fun.gbr.options.OptionManager;

public class FileReturner implements Returner {
	
	private Path output = OptionManager.get().getOutput();
	
	public FileReturner() {}
	
	public FileReturner(Path output) {
		this.output = output;
	}

	@Override
	public void writeOut(String encoded) {
		if(!Files.exists(output) || Files.isWritable(output)) {
			try {
				Files.writeString(output, encoded, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			System.err.println("Can't write result to " + output + "! Not writeable!");
		}
	}

}
