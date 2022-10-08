package fun.gbr.io;

import fun.gbr.options.Options;

public class FetcherFactory {	
	private FetcherFactory() {}
	
	public static Fetcher build() {
		return new FileFetcher(Options.get().input());
	}
}
