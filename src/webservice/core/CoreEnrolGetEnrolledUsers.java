package webservice.core;

import java.io.IOException;

import webservice.MoodleOptions;
import webservice.WebService;

public class CoreEnrolGetEnrolledUsers extends WebService{

	private int courseid;
	
	public CoreEnrolGetEnrolledUsers(int courseid) {
		super(MoodleOptions.OBTENER_USUARIOS_MATRICULADOS);
		this.courseid=courseid;
		
	}
	/**
	 * 
	 */
	@Override
	public String getResponse() throws IOException {
		
		String url=this.url;
		
		appendToUrlCourseid(courseid);
		
		return getContentWithJsoup(url);
	}

}
