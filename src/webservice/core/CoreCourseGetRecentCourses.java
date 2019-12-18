package webservice.core;

import webservice.ParametersUserid;
import webservice.WSFunctions;

public class CoreCourseGetRecentCourses extends ParametersUserid{

	public CoreCourseGetRecentCourses(int userid) {
		super(userid);
	}

	@Override
	public WSFunctions getWSFunction() {
		return WSFunctions.CORE_COURSE_GET_RECENT_COURSES;
	}


}
