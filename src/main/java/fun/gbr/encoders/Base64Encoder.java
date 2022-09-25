package fun.gbr.encoders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import fun.gbr.options.Options;
import fun.gbr.options.Options.Mode;

/**
 * 
 *
 */
public class Base64Encoder implements Encoder {

	@Override
	public String convert(String text) throws Exception {
		if(Mode.decode.equals(Options.get().getMode())) {
			byte[] decodedBytes = Base64.getDecoder().decode(text);
			return new String(decodedBytes, StandardCharsets.UTF_8);
		}
		
		return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String getName() {
		return "B64";
	}
}
