package es.ubu.lsi.ubumonitor.webservice.api.core.calendar;

import es.ubu.lsi.ubumonitor.webservice.webservices.Util;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreCalendarGetCalendarUpcomingView extends WSFunctionAbstract{
	
	private Integer courseid;
	private Integer categoryid;
	
	public CoreCalendarGetCalendarUpcomingView() {
		super(WSFunctionEnum.CORE_CALENDAR_GET_CALENDAR_UPCOMING_VIEW);
	}

	
	public Integer getCourseid() {
		return courseid;
	}

	public void setCourseid(Integer courseid) {
		this.courseid = courseid;
	}

	public Integer getCategoryid() {
		return categoryid;
	}

	public void setCategoryid(Integer categoryid) {
		this.categoryid = categoryid;
	}


	@Override
	public void addToMapParemeters() {
		Util.putIfNotNull(parameters, "courseid", courseid);
		Util.putIfNotNull(parameters, "categoryid", categoryid);
		
	}

}
