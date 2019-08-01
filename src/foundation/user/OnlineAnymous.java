package foundation.user;

public class OnlineAnymous extends OnlineUser {

	private static OnlineAnymous instance;
	
	static {
		instance = new OnlineAnymous();
	}
	
	public static OnlineAnymous getInstance() {
		return instance;
	}
	
	public OnlineAnymous() {
		super();
		
		id = 1;
		name = "anymous";
		emp_name = "测试用户";
		orgid = "anymous";
		orgCode = "anymous";
		orgName = "anymous";
	}

}
