package es.ubu.lsi.ubumonitor.webservice.api.core.enrol;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

/**
 * Get the list of courses where a user is enrolled in.
 * 
 * @author Yi Peng Ji
 *
 */
public class CoreEnrolGetUsersCourses extends WSFunctionAbstract {

	public CoreEnrolGetUsersCourses(int userid) {
		super(WSFunctionEnum.CORE_ENROL_GET_USERS_COURSES);
		setUserid(userid);
	}

	/**
	 * User id (required)
	 * 
	 * @param userid
	 */
	public void setUserid(int userid) {
		parameters.put("userid", userid);

	}

	/**
	 * Include count of enrolled users for each course? This can add several seconds
	 * to the response time if a user is on several large courses, so set this to
	 * false if the value will not be used to improve performance (default: true).
	 * 
	 * @param returnusercount true if return usercount
	 */
	public void setReturnusercount(boolean returnusercount) {
		parameters.put("returnusercount", returnusercount ? 1 : 0);
	}
}
