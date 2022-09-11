package fun.gbr.tools.ui;

public interface QuitTrigger {
	public void checkIfQuit(String response) throws UserQuit;
	
	public static class EmptyQuitter implements QuitTrigger{
		@Override
		public void checkIfQuit(String response) throws UserQuit {			
		}		
	}
}
