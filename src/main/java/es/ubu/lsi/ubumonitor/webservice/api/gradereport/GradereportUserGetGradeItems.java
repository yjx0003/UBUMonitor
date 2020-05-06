package es.ubu.lsi.ubumonitor.webservice.api.gradereport;

import es.ubu.lsi.ubumonitor.webservice.webservices.Util;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class GradereportUserGetGradeItems extends WSFunctionAbstract{
	
	private Integer courseid;
	private Integer userid;
	private Integer groupid;

	public GradereportUserGetGradeItems(Integer courseid) {
		super(WSFunctionEnum.GRADEREPORT_USER_GET_GRADE_ITEMS);
		this.courseid = courseid;
	}

	public Integer getCourseid() {
		return courseid;
	}

	public void setCourseid(Integer courseid) {
		this.courseid = courseid;
	}

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public Integer getGroupid() {
		return groupid;
	}

	public void setGroupid(Integer groupid) {
		this.groupid = groupid;
	}

	@Override
	public void addToMapParemeters() {
		Util.putIfNotNull(parameters, "courseid", courseid);
		Util.putIfNotNull(parameters, "userid", userid);
		Util.putIfNotNull(parameters, "groupid", groupid);
	}
}
