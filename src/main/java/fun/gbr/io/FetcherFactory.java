package fun.gbr.io;

import fun.gbr.options.Options;

public class FetcherFactory {
	
	public static Fetcher build() {
		return new FileFetcher(Options.get().getInput());
	}

}
