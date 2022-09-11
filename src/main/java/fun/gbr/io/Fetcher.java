package fun.gbr.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public interface Fetcher {
	public String getInput();
}
