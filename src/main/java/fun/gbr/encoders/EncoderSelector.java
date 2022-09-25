package fun.gbr.encoders;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fun.gbr.options.Options;

/**
 * Class in charge of selecting the correct encoder for the task given by the user
 * All encoders have to be registered in the REGISTRY in order to be usable by this program
 *
 */
public class EncoderSelector {
	
	private EncoderSelector() {}
	
	public static Encoder build() throws InstantiationException, IllegalAccessException, 
	IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return build(Options.get().getEncoderKey());
	}

	public static Encoder build(String encoderKey) throws InstantiationException, IllegalAccessException, 
	IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		String[] keys = encoderKey.split(SEPARATION_REGEX);
		return makeEncoder(keys);
	}
	
	/** Makes an encoder based on the provided array of encoder keys
	 * @param keys
	 * @return A simple or composite encoder depending on the number of keys passed
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private static Encoder makeEncoder(String[] keys) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if(keys.length == 1) {
			return makeSimpleEncoder(keys[0]);
		}
		
		// Many keys -> composite encoder
		
		List<Encoder> components = new ArrayList<>(keys.length);
		for(String key: keys) {
			components.add(makeSimpleEncoder(key));
		}
		
		return new CompositeEncoder(components);
	}
	
	/** Initialises a simple encoder based on the provided key
	 * @param key
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private static Encoder makeSimpleEncoder(String key) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		var encoder = REGISTRY.get(key);
		if(encoder == null) {
			throw new IllegalArgumentException("Encoder not recognised!");
		}
		return encoder.getDeclaredConstructor().newInstance();
	}
	
	private static final String SEPARATION_REGEX = "\\s*\\+\\s*";
	private static final Map<String, Class<? extends Encoder>> REGISTRY = new HashMap<>();
	static {
		REGISTRY.put("SHIFT", ShiftEncoder.class);
		REGISTRY.put("SUB", SubstitutionEncoder.class);
		REGISTRY.put("OTP", OTPEncoder.class);
		REGISTRY.put("HEX", HexEncoder.class);
		REGISTRY.put("BIN", BinaryEncoder.class);
		REGISTRY.put("RSA", RSAEncoder.class);
		REGISTRY.put("B64", Base64Encoder.class);
	}
	
}
