package fun.gbr.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import fun.gbr.tools.ui.UserInputHandler;
import fun.gbr.tools.ui.UserQuit;

/**
 * TODO
 * - Give option to append
 * - Warn if resetting previous value and display old value
 *
 */
public class SubstitutionDictionaryMaker {

	public static void main(String[] args) throws IOException {
		
		System.out.println("Welcome let's make a substitution dictionary");
		
		UserInputHandler uih = new UserInputHandler();
		
		Map<String, String> dictionary = new HashMap<>();
		try(Scanner scanner = new Scanner(System.in)){
			
			Path path = Path.of(uih.treatUserInput(scanner, "Input the path of the dictionary to create"));
			if(Files.exists(path)) {
				throw new IllegalArgumentException("Provided path would overwrite existing file!");
			}
			
			Files.createFile(path);
			
			while(true) {
				String key = uih.treatUserInput(scanner, "Key (Empty to stop): ");
				if(key.isEmpty()) {
					break;
				}
				String value = uih.treatUserInput(scanner, "Value: ");
				
				dictionary.put("\"" + key + "\"", "\"" + value + "\"");
			}
			
			System.out.println("Writing to " + path.toAbsolutePath());
			
			String text = dictionary.entrySet().stream()
					.map(entry -> entry.getKey() + "\t" + entry.getValue())
					.collect(Collectors.joining(System.lineSeparator()));
			Files.writeString(path, text);
		} catch (UserQuit q) {
			System.err.println("Exiting: user quit");
		}
		
		System.out.println("Done");

	}

}
