package es.ubu.lsi.ubumonitor.webservice.api.core.course;

import java.util.Collection;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

/**
 * Return a list of administration options in a set of courses that are
 * avaialable or not for the current user.
 * 
 * @author Yi Peng Ji
 *
 */
public class CoreCourseGetUserAdministrationOptions extends WSFunctionAbstract {

	public CoreCourseGetUserAdministrationOptions(Collection<Integer> courseids) {
		super(WSFunctionEnum.CORE_COURSE_GET_USER_ADMINISTRATION_OPTIONS);
		setCouseids(courseids);
	}

	/**
	 * Course ids (required).
	 * 
	 * @param courseids courses ids
	 */
	public void setCouseids(Collection<Integer> courseids) {
		if (courseids != null && !courseids.isEmpty()) {
			parameters.put("courseids", courseids);
		}
	}

}
