package fun.gbr.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import fun.gbr.Utils;
import fun.gbr.encoders.RSAEncoder;
import fun.gbr.options.Options.LoggerOptionKeys;

/**
 * Tool that generates RSA keys based on options
 *
 */
public class RSAKeyGenerator {
	
	private Path publicKeyPath;
	private Path privateKeyPath;

	public RSAKeyGenerator() {
		publicKeyPath = Utils.toNonNullPath(PUBLIC_KEY_OPTION);
		privateKeyPath = Utils.toNonNullPath(PRIVATE_KEY_OPTION);
	}
	
	public static void main(String[] args) {
		if(!Utils.initProgram(LOGGER_OPTION_KEYS)) {
			return;
		}
		
		LOGGER.info("Generating keys...");
		
		try {
			new RSAKeyGenerator().generateKeys(DEFAULT_KEY_SIZE);
		} catch (NoSuchAlgorithmException | IOException e) {
			LOGGER.log(Level.SEVERE, e, () -> "Key generation failed!");
		}
		
		LOGGER.info("Keys generated");
	}
	
	/** Generate an RSA Key pair and saves them to the paths set by options
	 * @param size key pair size
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	private KeyPair generateKeys(int size) throws NoSuchAlgorithmException, IOException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance(RSAEncoder.RSA);
		generator.initialize(size);
		KeyPair pair = generator.generateKeyPair();
		writeKeys(pair);
		return pair;
	}
	
	/** Write keys to the files specified by the path option values
	 * @param pair
	 * @throws IOException
	 */
	private void writeKeys(KeyPair pair) throws IOException {
		LOGGER.warning(() -> "Writing keys to \"" + privateKeyPath.toAbsolutePath() + "\" and  \"" + publicKeyPath.toAbsolutePath() + "\"");
		
		throwIfExist(privateKeyPath, "private key");
		throwIfExist(publicKeyPath, "public key");
		
		Files.write(privateKeyPath, pair.getPrivate().getEncoded());
		Files.write(publicKeyPath, pair.getPublic().getEncoded());
	}
	
	/** Throw an exception if the path exists
	 * @param path
	 * @param name
	 */
	private static void throwIfExist(Path path, String name) {
		if(Files.exists(path)) {
			throw new IllegalArgumentException("Can't write " + name + " to " + path.toAbsolutePath() + ": This file already exists!");
		}
	}

	private static final int DEFAULT_KEY_SIZE = 2048;
	private static final String PUBLIC_KEY_OPTION = "rsa_kg_public_key_path";
	private static final String PRIVATE_KEY_OPTION = "rsa_kg_private_key_path";
	private static final String LOG_FILE_OPTION = "rsa_kg_log_file";
	private static final String LOG_LEVEL_OPTION = "rsa_kg_log_level";
	private static final LoggerOptionKeys LOGGER_OPTION_KEYS = new LoggerOptionKeys(LOG_FILE_OPTION, LOG_LEVEL_OPTION);	
	private static final Logger LOGGER = Logger.getLogger(RSAKeyGenerator.class.getCanonicalName());
}
