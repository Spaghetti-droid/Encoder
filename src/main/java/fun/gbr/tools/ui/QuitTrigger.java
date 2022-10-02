package fun.gbr.tools.ui;

import java.util.Scanner;

/**
 * Implementers are in charge of checking whether a user is asking to (or should be made to) quit
 *
 */
public interface QuitTrigger {
	/** Throws UserQuit throwable if the cli should be quit
	 * @param response
	 * @throws UserQuit
	 */
	public void checkIfQuit(Scanner scanner, String response) throws UserQuit;
	
	public static class EmptyQuitter implements QuitTrigger{
		@Override
		public void checkIfQuit(Scanner scanner, String response) throws UserQuit {	
			// Empty Quitter deactivates quitting
		}		
	}
}
