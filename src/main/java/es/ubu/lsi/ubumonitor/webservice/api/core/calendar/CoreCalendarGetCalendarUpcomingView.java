package es.ubu.lsi.ubumonitor.webservice.api.core.calendar;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreCalendarGetCalendarUpcomingView extends WSFunctionAbstract {

	public CoreCalendarGetCalendarUpcomingView() {
		super(WSFunctionEnum.CORE_CALENDAR_GET_CALENDAR_UPCOMING_VIEW);
	}

	public void setCourseid(int courseid) {
		parameters.putOpt("courseid", courseid);
	}

	public void setCategoryid(int categoryid) {
		parameters.putOpt("categoryid", categoryid);
	}

}
