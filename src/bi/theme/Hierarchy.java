package bi.theme;

public class Hierarchy {

	private static Hierarchy instance;
	
	private Hierarchy() {
		
	}
	
	public static synchronized Hierarchy getInstance() {
		if (instance == null) {
			instance = new Hierarchy();
		}
		
		return instance;
	}

	public boolean isCompatible(String userid, String result) {
		return true;
	}

	
}
