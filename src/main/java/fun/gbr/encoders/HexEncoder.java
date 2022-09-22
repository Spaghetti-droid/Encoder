package fun.gbr.encoders;

import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import fun.gbr.options.Options;
import fun.gbr.options.Options.Mode;

public class HexEncoder implements Encoder {

	@Override
	public String convert(String text) throws DecoderException {
		if (Mode.decode.equals(Options.get().getMode())) {
			return new String(Hex.decodeHex(text), StandardCharsets.UTF_8);
		}

		return Hex.encodeHexString(text.getBytes(StandardCharsets.UTF_8));
	}

}
