package fun.gbr.encoders;

import java.io.IOException;

public interface Encoder {
	public String convert(String text) throws IOException;
}
