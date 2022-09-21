package fun.gbr.encoders;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import fun.gbr.options.Options;

/**
 * Class in charge of selecting the correct encoder for the task given by the user
 * All encoders have to be registered in the REGISTRY in order to be usable by this program
 *
 */
public class EncoderSelector {
	
	private EncoderSelector() {}
	
	public static Encoder build() {
		return build(Options.get().getEncoderKey());
	}

	public static Encoder build(String encoderKey) {
		var encoder = REGISTRY.get(encoderKey);
		if(encoder == null) {
			throw new IllegalArgumentException("Encoder not recognised!");
		}
		
		try {
			return encoder.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static final Map<String, Class<? extends Encoder>> REGISTRY = new HashMap<>();
	static {
		REGISTRY.put("SHIFT", ShiftEncoder.class);
		REGISTRY.put("SUB", SubstitutionEncoder.class);
		REGISTRY.put("OTP", OTPEncoder.class);
		REGISTRY.put("HEX", HexEncoder.class);
		REGISTRY.put("BIN", BinaryEncoder.class);
	}
	
}
