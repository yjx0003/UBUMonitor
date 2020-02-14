package es.ubu.lsi.ubumonitor.webservice.core;

import es.ubu.lsi.ubumonitor.webservice.ParametersUserid;
import es.ubu.lsi.ubumonitor.webservice.WSFunctions;

public class CoreCourseGetRecentCourses extends ParametersUserid{

	public CoreCourseGetRecentCourses(int userid) {
		super(userid);
	}

	@Override
	public WSFunctions getWSFunction() {
		return WSFunctions.CORE_COURSE_GET_RECENT_COURSES;
	}


}
