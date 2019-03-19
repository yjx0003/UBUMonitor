package webservice;

public abstract class ParametersUserid extends WebService {

	private int userid;
	
	public ParametersUserid(int userid) {
		this.userid = userid;
	}

	@Override
	protected void appendToUrlParameters() {
		appendToUrlUserid(userid);
	}

}
