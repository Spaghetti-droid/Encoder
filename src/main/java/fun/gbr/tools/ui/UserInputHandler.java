package fun.gbr.tools.ui;

import java.util.Scanner;

import fun.gbr.tools.ui.QuitTrigger.EmptyQuitter;
import fun.gbr.tools.ui.ResponseValidator.EmptyValidator;

public class UserInputHandler {
	
	private QuitTrigger quitter = new EmptyQuitter();
	private ResponseValidator validator = new EmptyValidator();	
	
	@SuppressWarnings("hiding")
	public UserInputHandler withQuitter(QuitTrigger quitter) {
		this.quitter = quitter;
		return this;
	}
	
	@SuppressWarnings("hiding")
	public UserInputHandler withValidator(ResponseValidator validator) {
		this.validator = validator;
		return this;
	}

	/**
	 * Sends prompt to user and reads input. Calls specific methods for certain
	 * inputs
	 * 
	 * @param scanner
	 * @param prompt
	 * @param expected Inputs considered valid for this prompt (disregarding special commands)
	 * @return User response
	 * @throws UserQuit
	 */
	public String treatUserInput(Scanner scanner, String prompt) throws UserQuit {
		
		if (scanner == null) {
			throw new IllegalArgumentException("scanner can't be null");
		}

		System.out.println(prompt);

		boolean getMoreInput = true;
		String reponse = null;
		while (getMoreInput) {
			reponse = scanner.nextLine();
			
			this.quitter.checkIfQuit(reponse);
			
			// Check for commands
			
			getMoreInput = !this.validator.validate(reponse);
		}

		return reponse;		
	}
}
