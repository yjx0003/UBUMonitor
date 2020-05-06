package es.ubu.lsi.ubumonitor.webservice.api.core.enrol;

import es.ubu.lsi.ubumonitor.webservice.webservices.Util;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreEnrolGetUsersCourses extends WSFunctionAbstract {

	/**
	 * user id (required)
	 */
	Integer userid;
	/**
	 * Include count of enrolled users for each course? This can add several seconds
	 * to the response time if a user is on several large courses, so set this to
	 * false if the value will not be used to improve performance.
	 */
	Boolean returnusercount;

	public CoreEnrolGetUsersCourses(Integer userid) {
		super(WSFunctionEnum.CORE_ENROL_GET_USERS_COURSES);
		this.userid = userid;
	}

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public Boolean getReturnusercount() {
		return returnusercount;
	}

	public void setReturnusercount(Boolean returnusercount) {
		this.returnusercount = returnusercount;
	}

	@Override
	public void addToMapParemeters() {
		Util.putIfNotNull(parameters, "userid", userid.toString());
		Util.putIfNotNull(parameters, "returnusercount", Util.booleanToBinary(returnusercount));
	}

}
