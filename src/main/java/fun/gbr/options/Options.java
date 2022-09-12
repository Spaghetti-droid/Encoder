package fun.gbr.options;

import java.nio.file.Path;

public class Options {
	
	private Path input = DEFAULT_INPUT;
	private Path output = DEFAULT_OUTPUT;
	private String encoder;	
	private Mode mode;
	
	
	public Path getInput() {
		return input;
	}
	public Path getOutput() {
		return output;
	}
	public String getEncoderKey() {
		return encoder;
	}
	public void setInput(Path input) {
		this.input = input;
	}
	public void setOutput(Path output) {
		this.output = output;
	}
	public void setEncoder(String encoder) {
		this.encoder = encoder;
	}
	
	public Mode getMode() {
		return mode;
	}
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	public void setMode(String mode) {
		this.mode = Mode.valueOf(mode);
	}

	@Override
	public String toString() {
		return "Options [input=" + input + ", output=" + output + ", encoder=" + encoder + ", mode=" + mode + "]";
	}

	private static final Path DEFAULT_INPUT = Path.of("input.txt");
	private static final Path DEFAULT_OUTPUT = Path.of("output.txt");

	public static enum Mode{
		encode,
		decode
	}
}
