package es.ubu.lsi.ubumonitor.webservice.api.core.course;

import java.util.Arrays;
import java.util.Collection;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreCourseGetUserNavigationOptions extends WSFunctionAbstract {

	private Collection<Integer> courseids;

	public CoreCourseGetUserNavigationOptions(Collection<Integer> courseids) {
		super(WSFunctionEnum.CORE_COURSE_GET_USER_NAVIGATION_OPTIONS);
		this.courseids = courseids;
	}
	
	public CoreCourseGetUserNavigationOptions(int courseid) {
		this(Arrays.asList(courseid));
	}

	@Override
	public void addToMapParemeters() {
		int i = 0;
		for (Integer courseid : courseids) {
			parameters.put("courseids[" + i + "]", courseid.toString());
			++i;
		}
	}

}
