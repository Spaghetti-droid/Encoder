package fun.gbr.encoders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.BitSet;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import fun.gbr.Utils;
import fun.gbr.options.Options;
import fun.gbr.options.Options.Mode;

/**
 * Encode and decode using One Time Pad
 *
 */
public class OTPEncoder implements Encoder {
	
	private Path otpPath;
	private boolean asHex;
	private boolean decode;
	private boolean generateOTP;
	
	public OTPEncoder(){
		otpPath = Utils.getDictionaryPath(OTP_FILE_PATH_KEY);
		decode = Mode.decode.equals(Options.get().getMode());
		generateOTP = "true".equals(System.getProperty(GENERATE_KEY)) 
				&& !decode 
				&& !Files.exists(otpPath);
		if(!generateOTP && !Files.isReadable(otpPath)) {
			throw new IllegalArgumentException("One Time Pad not readable: " + otpPath.toAbsolutePath());
		}
		asHex = "true".equals(System.getProperty(AS_HEX_KEY));
	}

	@Override
	public String convert(String text) throws DecoderException, IOException {
		
		// Convert text to bits
		
		byte[] textBytes;
		if(asHex && decode) {
			textBytes = Hex.decodeHex(text);
		} else {
			textBytes = text.getBytes(StandardCharsets.UTF_8);
		}
		
		BitSet textBits = BitSet.valueOf(textBytes);
		
		// Get OTP
		
		BitSet otpBits = getOTP(textBytes.length);
		
		if(otpBits.length() < textBits.length() && !decode) {
			System.err.println("WARNING: OTP is shorter than input text. It will have to be applied several times!");
		}
		
		// Apply OTP, reapplying if it isn't long enough
		
		byte[] encodedBytes = new byte[textBytes.length];
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
		
		// Build encoded String
		
		if(asHex && !decode) {
			return Hex.encodeHexString(encodedBytes);
		}
		return new String(encodedBytes, StandardCharsets.UTF_8);
	}
	
	/** Add everything in source to target from offset.
	 * Additions will stop at either the end of source or the end of target, whichever comes first
	 * @param source
	 * @param target
	 * @param offset
	 * @return index of the position after the last insertion
	 * 	ie if source has 3 elements and was inserted with an offset of 2, return value will be 5
	 */
	private static int add(byte[] source, byte[] target, int offset) {
		for(int i=0; offset<(target.length) && i<source.length; i++) {
			target[offset++] = source[i];
		}
		return offset;		
	}
	
	/** Obtain a one time pad
	 * @param textLength
	 * @return
	 * @throws IOException
	 */
	private BitSet getOTP(int textLength) throws IOException {
		if(generateOTP) {
			return generateOTP(textLength);
		}
		
		// Read if not generating
		
		try(var is = Files.newInputStream(otpPath, StandardOpenOption.READ)){
			// Only get the amount of the otp we need for encoding.
			return BitSet.valueOf(is.readNBytes(textLength));	
		}
	}
	
	/** Generate a random one time pad and write it to the file given by otpPath
	 * @param textLength
	 * @return the otp
	 * @throws IOException
	 */
	private BitSet generateOTP(int textLength) throws IOException {
		
		// Create random OTP
		
		byte[] otp = new byte[textLength];
		List<Byte> otpList = Utils.getRandom().ints(textLength, Byte.MIN_VALUE, Byte.MAX_VALUE).boxed().map(Integer::byteValue).toList();
		for(int i=0; i<textLength; i++) {
			otp[i] = otpList.get(i);
		}
		
		// Write OTP file
		
		Files.write(otpPath, otp);
		return BitSet.valueOf(otp);		
	}
	
	@Override
	public String getName() {
		return "OTP";
	}
	
	private static final String OTP_FILE_PATH_KEY = "otp_file_path";
	private static final String AS_HEX_KEY = "encoded_as_hex";
	private static final String GENERATE_KEY = "generate_if_no_key";
}
