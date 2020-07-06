package es.ubu.lsi.ubumonitor.webservice.api.gradereport;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

/**
 * Get the user/s report grades table for a course.
 * 
 * @author Yi Peng Ji
 *
 */
public class GradereportUserGetGradesTable extends WSFunctionAbstract {

	public GradereportUserGetGradesTable(int courseid) {
		this(WSFunctionEnum.GRADEREPORT_USER_GET_GRADES_TABLE, courseid);
	}

	protected GradereportUserGetGradesTable(WSFunctionEnum wsFunction, int courseid) {
		super(wsFunction);
		setCourseid(courseid);
	}

	/**
	 * Course id
	 * 
	 * @param courseid
	 */
	public void setCourseid(int courseid) {
		parameters.put("courseid", courseid);
	}

	/**
	 * Return grades only for this user (optional).
	 * 
	 * @param userid user id
	 */
	public void setUserid(int userid) {
		parameters.put("userid", userid);
	}

	/**
	 * Get users from this group only.
	 * 
	 * @param groupid group id
	 */
	public void setGroupid(int groupid) {
		parameters.put("groupid", groupid);
	}

}
