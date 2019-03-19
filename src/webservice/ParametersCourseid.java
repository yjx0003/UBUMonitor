package webservice;

public abstract class ParametersCourseid extends WebService{

	private int courseid;
	
	public ParametersCourseid(int courseid) {
	
		this.courseid=courseid;
		
	}

	@Override
	public void appendToUrlParameters() {
		appendToUrlCourseid(courseid);
		
	}

}
