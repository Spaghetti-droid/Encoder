package fun.gbr.io;

public class ReturnerFactory {
	
	private ReturnerFactory() {}

	public static Returner build() {
		return new FileReturner();
	}
	
}
