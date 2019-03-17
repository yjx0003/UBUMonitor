package webservice;

public abstract class Courseid extends WebService{

	private int courseid;
	
	public Courseid(int courseid) {
	
		this.courseid=courseid;
		
	}

	@Override
	public void appendToUrlParameters() {
		appendToUrlCourseid(courseid);
		
	}

}
