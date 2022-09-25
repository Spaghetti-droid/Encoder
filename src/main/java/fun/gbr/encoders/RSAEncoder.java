package fun.gbr.encoders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import fun.gbr.options.Options;

/**
 * 
 *
 */
public class RSAEncoder implements Encoder {
	
	private Path publicKeyPath;
	private Path privateKeyPath;
	private boolean decode;
	
	public RSAEncoder() throws IOException {
		decode = Options.Mode.decode.equals(Options.get().getMode());
		privateKeyPath = System.getProperty(PRIVATE_KEY_OPTION) == null ? null : Path.of(System.getProperty(PRIVATE_KEY_OPTION)) ;
		if(decode) {
			if(privateKeyPath == null) {
				throw new IllegalArgumentException("Private Key must be specified when decoding");
			}
			if(!Files.isReadable(privateKeyPath)) {
				throw new IOException("Private key not readable");
			}
		} else {
			publicKeyPath = toPath(PUBLIC_KEY_OPTION);
		}
	}
	
	/** Converts system property to path if not null
	 * @param propertyName
	 * @return Path equivalent of property
	 */
	private static Path toPath(String propertyName) {
		String pathString = System.getProperty(propertyName);
		if(pathString == null) {
			throw new IllegalArgumentException(propertyName + " must be specified.");
		}
		return Path.of(pathString);
	}

	@Override
	public String convert(String text) throws Exception {
		if(decode) {
			return decode(text);
		}
		
		return encode(text);
	}
	
	/**
	 * @param text Base64 encoded, RSA encrypted
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 * @throws IOException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	private String decode(String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, IOException, IllegalBlockSizeException, BadPaddingException {
		byte[] bytes = Base64.getDecoder().decode(text);
		Cipher cipher = Cipher.getInstance(RSA);
		cipher.init(Cipher.DECRYPT_MODE, loadKey(privateKeyPath, PRIVATE));
		byte[] decryptedBytes = cipher.doFinal(bytes);
		return new String(decryptedBytes, StandardCharsets.UTF_8);
	}
	
	/**
	 * @param text
	 * @return text, RSA encrypted, Base64 encoded
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	private String encode(String text) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {		
		PublicKey key = (PublicKey) loadKey(publicKeyPath, PUBLIC);		
		Cipher encryptCipher = Cipher.getInstance(RSA);
		encryptCipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encryptedMessageBytes = encryptCipher.doFinal(text.getBytes(StandardCharsets.UTF_8));		
		return Base64.getEncoder().encodeToString(encryptedMessageBytes);
	}
	
	/** Load a key from a file
	 * @param path
	 * @param isPublic
	 * @return The key
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InvalidKeySpecException
	 */
	private static Key loadKey(Path path, boolean isPublic) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		byte[] keyBytes = Files.readAllBytes(path);
		KeyFactory keyFactory = KeyFactory.getInstance(RSA);
		if(isPublic) {
			EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			return keyFactory.generatePublic(keySpec);
		}
		
		EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		return keyFactory.generatePrivate(keySpec);
	}
	
	@Override
	public String getName() {
		return "RSA";
	}
	
	public static final String PUBLIC_KEY_OPTION = "public_key_path";
	public static final String PRIVATE_KEY_OPTION = "private_key_path";
	public static final String RSA = "RSA";
	private static final boolean PRIVATE = false;
	private static final boolean PUBLIC = true;
}
