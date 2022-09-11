package fun.gbr.io;

import fun.gbr.options.OptionManager;

public class FetcherFactory {
	
	public static Fetcher build() {
		return new FileFetcher(OptionManager.get().getInput());
	}

}
