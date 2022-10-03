package fun.gbr.io;

import java.io.IOException;

public interface Fetcher {
	public byte[] getInput() throws IOException;
}
