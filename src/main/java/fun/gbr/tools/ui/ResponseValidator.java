package fun.gbr.tools.ui;

public interface ResponseValidator {
	public boolean validate(String response) throws IllegalArgumentException;
	
	public static class EmptyValidator implements ResponseValidator{
		@Override
		public boolean validate(String response) throws IllegalArgumentException {
			return true;
		}
		
	}
}
