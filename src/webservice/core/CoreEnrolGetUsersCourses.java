package webservice.core;

import webservice.Userid;
import webservice.WSFunctions;

public class CoreEnrolGetUsersCourses extends Userid {

	public CoreEnrolGetUsersCourses(int userid) {
		super(userid);
	}

	@Override
	public WSFunctions getWSFunction() {
	
		return WSFunctions.CORE_ENROL_GET_USERS_COURSES;
	}

	
	
	


	

}
