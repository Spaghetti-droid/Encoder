package fun.gbr.io;

public class ReturnerFactory {

	public static Returner build() {
		return new FileReturner();
	}
	
}
