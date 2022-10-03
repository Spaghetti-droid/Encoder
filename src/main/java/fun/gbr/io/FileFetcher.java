package fun.gbr.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * Reads the contents of an input file and outputs it as a string
 *
 */
public class FileFetcher implements Fetcher {

	private Path path;

	public FileFetcher(Path inputFile) {
		this.path = inputFile;
	}
	
	@Override
	public byte[] getInput() throws IOException {
		if (!Files.isReadable(path)) {
			if(!Files.exists(path)) {
				throw new IOException("Input file not found!");
			}
			throw new IOException("Input file isn't readable!");
		}
		
		return Files.readAllBytes(path);
	}
}
