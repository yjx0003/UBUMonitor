package webservice.core;

import webservice.Courseid;
import webservice.WSFunctions;

public class CoreEnrolGetUsersCourses extends Courseid {

	public CoreEnrolGetUsersCourses(int courseid) {
		super(courseid);
	}

	@Override
	public WSFunctions getWSFunction() {
	
		return WSFunctions.OBTENER_CURSOS;
	}

	
	
	


	

}
