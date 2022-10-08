package fun.gbr.encoders;

import fun.gbr.options.Options;

/**
 * Encode to and from binary
 *
 */
public class BinaryEncoder implements Encoder {

	@Override
	public byte[] convert(byte[] bytes) throws Exception {		
		if(Options.get().decode()) {
			return decode(bytes);
		}
		
		return encode(bytes);
	}
	
	/** Convert bytes representing binary to bytes representing the 8-bit equivalent
	 * @param bytes must be a combination of the bytes for 0 and 1
	 * @return bytes for the 8-bit array equivalent of the binary string
	 */
	private static byte[] decode(byte[] bytes) {
		
		// Check if binary
		
		for(byte b : bytes) {
			if(b != '0' && b != '1') {
				throw new IllegalArgumentException("Input is not binary!");
			}
		}
		
		// Convert to bit array
		
		boolean[] bits = new boolean[bytes.length];
		for(int i=0; i<bytes.length; i++) {
			bits[i] = bytes[i] == '1';
		}
		
		// Convert BitSet to bytes and then String
		
		return bitsToBytes(bits);		
	}
	
	/** Convert 8-bit char bytes to a series of 1s and 0s in byte form
	 * @param bytes
	 * @return
	 */
	private static byte[] encode(byte[] bytes) {
		boolean[] bits = bytesToBits(bytes);
		byte[] encoded = new byte[bits.length];
		for(int i=0; i<bits.length; i++) {
			encoded[i] = (byte) (bits[i] ? '1' : '0');
		}
		
		return encoded;
	}
	
	/** Turn array of bytes into array of booleans, each representing 1 bit
	 * byte representation is Big-endian
	 * @param bytes
	 * @return array of booleans representing the bytes' bits
	 */
	private static boolean[] bytesToBits(byte[] bytes) {
		int nBits = bytes.length *8;
		boolean[] bits = new boolean[nBits];
		for(int i=0; i<nBits; i++) {
			bits[i] = ((bytes[i/8] & 0xff & (0b10000000>>(i%8))) != 0);
		}
		return bits;
	}
	
	/** Converts a big-endian bit array into bytes
	 * @param bits
	 * @return the byte array equivalent to the input bits
	 */
	private static byte[] bitsToBytes(boolean[] bits) {
		byte[] bytes = new byte[bits.length/8];
		for(int i=0; i<bits.length; i++) {
			if(bits[i]) {
				bytes[i/8] += Math.pow(2, 7-i%8);
			}
		}
		
		return bytes;
	}
	
	@Override
	public String getName() {
		return "BIN";
	} 	
}
