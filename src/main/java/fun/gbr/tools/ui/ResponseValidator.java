package fun.gbr.tools.ui;

/**
 * Implementers validate user response to determine whether they should be asked for input again
 *
 */
public interface ResponseValidator {
	/** Validate the user's response
	 * @param response
	 * @return	true if the response is appropriate, false if the user should be prompted again
	 * @throws IllegalArgumentException	If the response is too outrageous
	 */
	public boolean validate(String response) throws IllegalArgumentException;
	
	public static class EmptyValidator implements ResponseValidator{
		@Override
		public boolean validate(String response) throws IllegalArgumentException {
			return true;
		}
		
	}
}
