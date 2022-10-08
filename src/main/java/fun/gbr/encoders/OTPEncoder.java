package fun.gbr.encoders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.BitSet;
import java.util.List;
import java.util.logging.Logger;

import fun.gbr.Utils;
import fun.gbr.options.Options;
import fun.gbr.options.Options.Mode;

/**
 * Encode and decode using One Time Pad
 *
 */
public class OTPEncoder implements Encoder {
	
	private Path otpPath;
	private boolean decode;
	private boolean generateOTP;
	
	public OTPEncoder(){
		otpPath = Utils.getDictionaryPath(OTP_FILE_PATH_KEY);
		decode = Mode.decode.equals(Options.get().mode());
		generateOTP = "true".equals(Options.get().property(GENERATE_KEY)) 
				&& !decode 
				&& !Files.exists(otpPath);
		if(!generateOTP && !Files.isReadable(otpPath)) {
			throw new IllegalArgumentException("One Time Pad not readable: " + otpPath.toAbsolutePath());
		}
	}
	
	@Override
	public byte[] convert(byte[] bytes) throws Exception {
		BitSet bits = BitSet.valueOf(bytes);
		
		// Get OTP
		
		BitSet otp = getOTP(bytes.length);		
		if(otp.length() < bits.length() && !decode) {
			Logger.getLogger(this.getClass().getCanonicalName()).warning("OTP is shorter than input text. It will have to be applied several times!");
		}
		
		// Apply OTP, reapplying if it isn't long enough
		
		byte[] encoded = new byte[bytes.length];
		int offset = 0;
		for(int i=0; i<bits.size(); i+=otp.size()) {			
			int chunkEnd = i + otp.size();
			if(chunkEnd>bits.size()) {
				chunkEnd = bits.size();
			}
			BitSet encodedBits = bits.get(i, chunkEnd);
			encodedBits.xor(otp);
			offset = add(encodedBits.toByteArray(), encoded, offset);
		}
		
		return encoded;		
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
	 * @param bytesLength
	 * @return
	 * @throws IOException
	 */
	private BitSet getOTP(int bytesLength) throws IOException {
		if(generateOTP) {
			return generateOTP(bytesLength);
		}
		
		// Read if not generating
		
		try(var is = Files.newInputStream(otpPath, StandardOpenOption.READ)){
			// Only get the amount of the otp we need for encoding.
			return BitSet.valueOf(is.readNBytes(bytesLength));	
		}
	}
	
	/** Generate a random one time pad and write it to the file given by otpPath
	 * @param bytesLength
	 * @return the otp
	 * @throws IOException
	 */
	private BitSet generateOTP(int bytesLength) throws IOException {
		
		// Create random OTP
		
		byte[] otp = new byte[bytesLength];
		List<Byte> otpList = Utils.getRandom().ints(bytesLength, Byte.MIN_VALUE, Byte.MAX_VALUE).boxed().map(Integer::byteValue).toList();
		for(int i=0; i<bytesLength; i++) {
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
	
	private static final String OTP_FILE_PATH_KEY = "otp/file_path";
	private static final String GENERATE_KEY = "otp/generate_if_no_key";
}
