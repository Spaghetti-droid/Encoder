package fun.gbr.tools.ui;

import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementers check and adjust a provided response until it fits their parameters
 *
 */
public interface ResponseValidator {
	/** Validate the user's response
	 * @param response
	 * @return	Adjusted response after check
	 * @throws IllegalArgumentException	If the response is too outrageous
	 * @throws UserQuit 
	 */
	public String validate(Scanner scanner, String prompt, String response) throws IllegalArgumentException, UserQuit;
	
	// %%%%%%%%%% Some Implementing classes %%%%%%%%%%
	
	/**
	 * Used when no validation is necessary
	 *
	 */
	public static class EmptyValidator implements ResponseValidator{
		@Override
		public String validate(Scanner scanner, String prompt, String response){
			// Does nothing
			return response;
		}		
	}
	
	/**
	 * Validator that applies when a limited set of responses are acceptable
	 *
	 */
	public static class SetBasedValidator implements ResponseValidator{
		
		private UserPrompter prompter;
		private Set<String> validResponses;
		private boolean ignoreCase;		
		private boolean trimSpaces;		
		
		/**
		 * @param validResponses	The set of responses that the user is authorised to give
		 * @param ignoreCase		If true case will be ignored
		 * @param trimSpaces		If true spaces around response will be ignored
		 * @param prompter 			Prompter to use on validation failure
		 */
		public SetBasedValidator(Set<String> validResponses, boolean ignoreCase, boolean trimSpaces, UserPrompter prompter) {
			if(prompter == null) {
				throw new IllegalArgumentException("A prompter must be specified");
			}		
			this.trimSpaces = trimSpaces;
			this.ignoreCase = ignoreCase;
			if(ignoreCase) {
				this.validResponses = validResponses.stream().map(s -> s == null ? null : s.toLowerCase()).collect(Collectors.toSet());
			} else {
				this.validResponses = validResponses;
			}
			this.prompter = prompter;
		}

		@Override
		public String validate(Scanner scanner, String prompt, String response) throws IllegalArgumentException, UserQuit {
			if(ignoreCase) {
				response = response.toLowerCase();
			}
			if(trimSpaces) {
				response = response.trim();
			}
			if(!validResponses.contains(response)) {
				String validResponsesStr = validResponses.stream().collect(Collectors.joining("/", "(", ")"));
				System.err.println("Invalid response. Valid inputs are: " + validResponsesStr);
				return this.prompter.queryUser(scanner, prompt);
			}
			
			return response;
		}		
	}
}
