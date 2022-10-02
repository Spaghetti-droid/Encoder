package fun.gbr.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import fun.gbr.io.dictionaries.DictionaryReader;
import fun.gbr.options.Options.Mode;
import fun.gbr.tools.ui.ResponseValidator.SetBasedValidator;
import fun.gbr.tools.ui.ResponseValidator;
import fun.gbr.tools.ui.UserPrompter;
import fun.gbr.tools.ui.UserQuit;

/**
 * A utility class for creating a dictionary in the correct format
 *
 */
public class SubstitutionDictionaryMaker {

	/** Can be useful if the user wants to manually create a cipher
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		System.out.println("Welcome let's make a substitution dictionary");
		
		// Setup
		
		Map<String, String> dictionary = new HashMap<>();
		
		// Start getting user input
		
		try(Scanner scanner = new Scanner(System.in)){
			
			PathStats stats = getPathFromUser(scanner);			
			if(stats.pathExists()) {
				dictionary.putAll(new DictionaryReader(stats.path(), Mode.encode).get());
			}			
			getKeyValuePairsFromUser(scanner, dictionary);			
			write(dictionary, stats.path());
			
		} catch (UserQuit q) {
			System.err.println("Exiting: user quit");
		}
		
		System.out.println("Done");
	}
	
	/** Get the dictionary path from user
	 * @param scanner
	 * @return Record of path info
	 * @throws UserQuit
	 * @throws IOException
	 */
	private static PathStats getPathFromUser(Scanner scanner) throws UserQuit, IOException {
		boolean pathOK = false;
		boolean exists = false;
		Path path = null;
		while(!pathOK) {
			path = Path.of(GENERIC_PROMPTER.queryUser(scanner, "Input the path of the dictionary to create"));	
			exists = Files.exists(path);
			if(exists) {
				pathOK = ynToBoolean(CONFIRM_PROMPTER.queryUser(scanner, "This file already exists. Append new entries? (y/n)"));
			} else {
				Files.createFile(path);
				pathOK = true;
			}
		}
		
		return new PathStats(path, exists);
	}
	
	/** Prompts user for key-value pairs to add to map
	 * @param scanner
	 * @param dictionary
	 * @throws UserQuit
	 */
	private static void getKeyValuePairsFromUser(Scanner scanner, Map<String, String> dictionary) throws UserQuit {
		UserPrompter valueUIH = initValuePrompter(dictionary);
		while(true) {
			String key = GENERIC_PROMPTER.queryUser(scanner, "Key (Empty to stop): ");
			if(key.isEmpty()) {
				return;
			}
			boolean write = dictionary.get(key) == null;
			if(!write) {
				String choice = CONFIRM_PROMPTER.queryUser(scanner, "The key \"" + key + "\" already has value \"" + dictionary.get(key) + "\". Overwrite? (y/n)");
				write = ynToBoolean(choice);
			}
			
			if(write) {
				String value = valueUIH.queryUser(scanner, "Value: ");				
				dictionary.put(key, value);
			}
		}
	}
	
	/** Converts a y or n user answer to a boolean
	 * @param yn
	 * @return
	 */
	private static boolean ynToBoolean(String yn) {
		return "y".equalsIgnoreCase(yn.trim());
	}
	
	/** Initialises the prompter that is used to obtain dictionary values
	 * This prompter will warn the user if they are about to duplicate a dictionary value
	 * @param dictionary
	 * @return A user input handler for dictionary values
	 */
	private static UserPrompter initValuePrompter(Map<String, String> dictionary) {
		return new UserPrompter().withValidator(new ResponseValidator() {		
			@Override
			public String validate(Scanner scanner, String propmt, String response) throws IllegalArgumentException, UserQuit {
				String lastResponse = null;
				while(dictionary.values().contains(response)) {
					if(response.equals(lastResponse)) {
						return response;
					}
					lastResponse = response;
					response = GENERIC_PROMPTER.queryUser(scanner, "The value \"" + response + "\" is already present in the dictionary. Type a different value or retype the same value to use it anyway");	
				}	
				return response;
			}
		});
	}
	
	/** Write dictionary to file at path
	 * @param dictionary
	 * @param path
	 * @throws IOException
	 */
	public static void write(Map<String, String> dictionary, Path path) throws IOException {
		System.out.println("Writing to " + path.toAbsolutePath());
		
		String text = dictionary.entrySet().stream()
				.map(entry -> "\"" + entry.getKey() + "\"" + "\t" + "\"" + entry.getValue() + "\"")
				.collect(Collectors.joining(System.lineSeparator()));
		Files.writeString(path, text);
		
		System.out.println("Done");
	}
	
	private static final UserPrompter CONFIRM_PROMPTER = new UserPrompter();	
	static {
		CONFIRM_PROMPTER.withValidator(new SetBasedValidator(Set.of("y", "n"), true, true, CONFIRM_PROMPTER));
	}
	private static final UserPrompter GENERIC_PROMPTER = new UserPrompter();
	
	private static record PathStats(Path path, boolean pathExists) {}
}
