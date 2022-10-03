package fun.gbr.encoders;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import fun.gbr.options.Options;
import fun.gbr.options.Options.Mode;

/**
 * Combines the effects of several other encoders
 *
 */
public class CompositeEncoder implements Encoder {
	
	private List<Encoder> components;	

	public CompositeEncoder(List<Encoder> components) {
		super();
		this.components = components;
		if(Mode.decode.equals(Options.get().getMode())) {
			// Decoding is done in the opposite order of encoding
			Collections.reverse(components);
		}
	}
	
	@Override
	public byte[] convert(byte[] bytes) throws Exception {
		for(Encoder component : components) {
			try {
				bytes = component.convert(bytes);
			} catch(Exception e) {
				throw new ComponentFailureException("Chained encoder " + component.getName() + " failed: " + e.getMessage(), e);
			}
		}
		return bytes;
	}
	
	@Override
	public String getName() {
		return components.stream().map(Encoder::getName).collect(Collectors.joining("+"));
	}
	
	public static class ComponentFailureException extends RuntimeException{

		/**
		 * 
		 */
		private static final long serialVersionUID = 5632191014403194363L;

		public ComponentFailureException(String message, Throwable cause) {
			super(message, cause);
		}

		public ComponentFailureException(String message) {
			super(message);
		}

		public ComponentFailureException(Throwable cause) {
			super(cause);
		}		
	}
}
