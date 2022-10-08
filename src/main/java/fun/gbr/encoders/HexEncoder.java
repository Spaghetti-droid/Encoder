package fun.gbr.encoders;

import org.apache.commons.codec.binary.Hex;

import fun.gbr.options.Options;

public class HexEncoder implements Encoder {
	
	@Override
	public byte[] convert(byte[] bytes) throws Exception {
		if(Options.get().doDecode()) {
			String text = new String(bytes, Options.get().charset());
			return Hex.decodeHex(text);
		}
		return Hex.encodeHexString(bytes).getBytes(Options.get().charset());
	}

	@Override
	public String getName() {
		return "HEX";
	}
}
