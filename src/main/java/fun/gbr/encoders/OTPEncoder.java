package fun.gbr.encoders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.BitSet;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import fun.gbr.Utils;
import fun.gbr.options.Options;
import fun.gbr.options.Options.Mode;

public class OTPEncoder implements Encoder {
	
	private Path otpPath;
	private boolean asHex;
	private boolean decode = Mode.decode.equals(Options.get().getMode());
	
	public OTPEncoder(){
		this.otpPath = Utils.getDictionaryPath(OTP_FILE_PATH_KEY);
		if(!Files.isReadable(otpPath)) {
			// TODO generate otp if no file
			throw new IllegalArgumentException("Can't read otp file");
		}
		this.asHex = "true".equals(System.getProperty(AS_HEX_KEY));
	}

	@Override
	public String convert(String text) throws IOException {
		byte[] textBytes;
		if(asHex && decode) {
			try {
				textBytes = Hex.decodeHex(text);
			} catch (DecoderException e) {
				throw new RuntimeException(e);
			}
		} else {
			textBytes = text.getBytes(StandardCharsets.UTF_8);
		}
		
		byte[] encodedBytes = new byte[textBytes.length];
		BitSet textBits = BitSet.valueOf(textBytes);
		BitSet otpBits;
		try(var is = Files.newInputStream(otpPath, StandardOpenOption.READ)){
			// Get a maximum of text length bytes from file as we don't need more
			otpBits = BitSet.valueOf(is.readNBytes(textBytes.length));	
		}
		
		int offset = 0;
		for(int i=0; i<textBits.size(); i+=otpBits.size()) {
			int chunkEnd = i + otpBits.size();
			if(chunkEnd>textBits.size()) {
				chunkEnd = textBits.size();
			}
			BitSet converted = textBits.get(i, chunkEnd);
			converted.xor(otpBits);
			offset = add(converted.toByteArray(), encodedBytes, offset);
		}
		if(asHex && !decode) {
			return Hex.encodeHexString(encodedBytes);
		}
		return new String(encodedBytes, StandardCharsets.UTF_8);
	}
	
	private static int add(byte[] source, byte[] target, int offset) {
		for(int i=0; offset<(target.length) && i<source.length; i++) {
			target[offset++] = source[i];
		}
		return offset;		
	}
	
	private static final String OTP_FILE_PATH_KEY = "otp_file_path";
	private static final String AS_HEX_KEY = "encoded_as_hex";

}
