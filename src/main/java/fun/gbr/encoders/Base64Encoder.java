package fun.gbr.encoders;

import java.util.Base64;

import fun.gbr.options.Options;
import fun.gbr.options.Options.Mode;

/**
 * 
 *
 */
public class Base64Encoder implements Encoder {
	
	@Override
	public byte[] convert(byte[] bytes) throws Exception {
		if(Mode.decode.equals(Options.get().getMode())) {
			return Base64.getDecoder().decode(bytes);
		}
		
		return Base64.getEncoder().encode(bytes);
	}

	@Override
	public String getName() {
		return "B64";
	}
}
