package es.ubu.lsi.ubumonitor.webservice.api.core.course;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

/**
 * List of courses a user has accessed most recently. WARNING: actually not
 * works with web service, only works with AJAX.
 * 
 * @author Yi Peng
 *
 */
public class CoreCourseGetRecentCourses extends WSFunctionAbstract {

	public CoreCourseGetRecentCourses() {
		super(WSFunctionEnum.CORE_COURSE_GET_RECENT_COURSES);
	}

	/**
	 * ID of the user, default to current user.
	 * 
	 * @param userid id of the user
	 */
	public void setUserid(int userid) {
		parameters.put("userid", userid);
	}

	/**
	 * Result set limit (default: 0).
	 * 
	 * @param limit Result set limit
	 */
	public void setLimit(int limit) {
		parameters.put("limit", limit);
	}

	/**
	 * Result set offset (default: 0).
	 * 
	 * @param offset Result set offset
	 */
	public void setOffset(int offset) {
		parameters.put("offset", offset);
	}

	/**
	 * Sort string column name (default: null)
	 * 
	 * @param sort column name
	 */
	public void setSort(String sort) {
		parameters.put("sort", sort);
	}

}
