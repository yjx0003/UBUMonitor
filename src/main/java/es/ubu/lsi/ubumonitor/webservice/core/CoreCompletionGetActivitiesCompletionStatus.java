package es.ubu.lsi.ubumonitor.webservice.core;

import es.ubu.lsi.ubumonitor.webservice.WSFunctions;
import es.ubu.lsi.ubumonitor.webservice.WebService;

public class CoreCompletionGetActivitiesCompletionStatus extends WebService {
	private int courseid;
	private int userid;

	public CoreCompletionGetActivitiesCompletionStatus(int courseid, int userid) {
		this.courseid = courseid;
		this.userid = userid;
	}

	public int getCourseid() {
		return courseid;
	}

	public void setCourseid(int courseid) {
		this.courseid = courseid;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	@Override
	public WSFunctions getWSFunction() {
		return WSFunctions.CORE_COMPLETION_GET_ACTIVITIES_COMPLETION_STATUS;
	}

	@Override
	protected void appendToUrlParameters() {
		appendToUrlCourseid(courseid);
		appendToUrlUserid(userid);
	}

}
