package webservice;

public abstract class Userid extends WebService {

	private int userid;
	
	public Userid(int userid) {
		this.userid = userid;
	}

	@Override
	protected void appendToUrlParameters() {
		appendToUrlUserid(userid);
	}

}
