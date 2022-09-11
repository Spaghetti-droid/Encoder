package fun.gbr.logic;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import fun.gbr.options.OptionManager;

public class EncoderFactory {
	
	public static Encoder build() {
		return build(OptionManager.get().getEncoderKey());
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
		REGISTRY.put("TO_MORSE", ToMorseEncoder.class);
		REGISTRY.put("SHIFT", ShiftEncoder.class);
	}
	
}
