package es.ubu.lsi.ubumonitor.webservice.api.core.course;

import java.util.Arrays;
import java.util.Collection;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreCourseGetUserAdministrationOptions extends WSFunctionAbstract {

	private Collection<Integer> courseids;

	public CoreCourseGetUserAdministrationOptions(Collection<Integer> courseids) {
		super(WSFunctionEnum.CORE_COURSE_GET_USER_ADMINISTRATION_OPTIONS);
		this.courseids = courseids;
	}
	
	public CoreCourseGetUserAdministrationOptions(int courseid) {
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
