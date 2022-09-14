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
	public String getInput() {

		if (!Files.isReadable(path)) {
			return null;
		}

		String text;
		try {
			text = Files.lines(path).collect(Collectors.joining("\n"));
		} catch (IOException e) {
			System.err.println("Issue reading \"" + path + "\": " + e.getMessage());
			e.printStackTrace();
			return null;
		}

		return text;
	}
}
