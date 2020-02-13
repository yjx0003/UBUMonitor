package es.ubu.lsi.webservice.core;

import es.ubu.lsi.webservice.ParametersUserid;
import es.ubu.lsi.webservice.WSFunctions;

public class CoreCourseGetRecentCourses extends ParametersUserid{

	public CoreCourseGetRecentCourses(int userid) {
		super(userid);
	}

	@Override
	public WSFunctions getWSFunction() {
		return WSFunctions.CORE_COURSE_GET_RECENT_COURSES;
	}


}
