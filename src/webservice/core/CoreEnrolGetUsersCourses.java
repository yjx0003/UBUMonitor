package webservice.core;

import java.io.IOException;

import webservice.MoodleOptions;
import webservice.WebService;

public class CoreEnrolGetUsersCourses extends WebService {

	private int userid;
	
	public CoreEnrolGetUsersCourses(int userid) {
		super(MoodleOptions.OBTENER_CURSOS);
		this.userid=userid;
	}
	
	
	@Override
	public String getResponse() throws IOException {
		
		String url=this.url;
		
		appendToUrlUserid(userid);
		
		return getContentWithJsoup(url);
	}

}
