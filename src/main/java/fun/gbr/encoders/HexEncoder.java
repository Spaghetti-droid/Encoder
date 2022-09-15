package fun.gbr.encoders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import fun.gbr.options.Options;
import fun.gbr.options.Options.Mode;

public class HexEncoder implements Encoder {

	@Override
	public String convert(String text) throws IOException {
		if(Mode.decode.equals(Options.get().getMode())) {
			try {
				return new String(Hex.decodeHex(text), StandardCharsets.UTF_8);
			} catch (DecoderException e) {
				throw new RuntimeException(e);
			}
		}
		
		return Hex.encodeHexString(text.getBytes(StandardCharsets.UTF_8));
	}

}
