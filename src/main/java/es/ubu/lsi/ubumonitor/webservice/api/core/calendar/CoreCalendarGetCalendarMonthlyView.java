package es.ubu.lsi.ubumonitor.webservice.api.core.calendar;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreCalendarGetCalendarMonthlyView extends WSFunctionAbstract {



	public CoreCalendarGetCalendarMonthlyView(int year, int month) {
		super(WSFunctionEnum.CORE_CALENDAR_GET_CALENDAR_DAY_VIEW);
		setYear(year);
		setMonth(month);
	}

	public void setMonth(int month) {
		parameters.put("month", month);
	}

	public void setDay(int day) {
		parameters.put("day", day);
	}

	public void setYear(int year) {
		parameters.put("year", year);
	}
}
