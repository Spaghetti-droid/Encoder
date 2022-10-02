package fun.gbr.tools.ui;

import java.util.Scanner;

import fun.gbr.tools.ui.QuitTrigger.EmptyQuitter;
import fun.gbr.tools.ui.ResponseValidator.EmptyValidator;

/**
 * Class for prompting user and recovering their response
 *
 */
public class UserPrompter {
	
	private QuitTrigger quitter = new EmptyQuitter();
	private ResponseValidator validator = new EmptyValidator();	
	
	@SuppressWarnings("hiding")
	public UserPrompter withQuitter(QuitTrigger quitter) {
		this.quitter = quitter;
		return this;
	}
	
	@SuppressWarnings("hiding")
	public UserPrompter withValidator(ResponseValidator validator) {
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
	public String queryUser(Scanner scanner, String prompt) throws UserQuit {

		if (scanner == null) {
			throw new IllegalArgumentException("scanner can't be null");
		}

		System.out.println(prompt);
		String reponse = scanner.nextLine();
		this.quitter.checkIfQuit(scanner, reponse);

		// Check for commands

		return this.validator.validate(scanner, prompt, reponse);
	}
}
