package es.ubu.lsi.ubumonitor.webservice.api.core.calendar;

import es.ubu.lsi.ubumonitor.webservice.webservices.Util;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreCalendarGetCalendarMonthlyView extends WSFunctionAbstract {

	private Integer year;
	private Integer month;

	public CoreCalendarGetCalendarMonthlyView(Integer year, Integer month) {
		super(WSFunctionEnum.CORE_CALENDAR_GET_CALENDAR_DAY_VIEW);
		this.year = year;
		this.month = month;
	}

	@Override
	public void addToMapParemeters() {
		Util.putIfNotNull(parameters, "year", year);
		Util.putIfNotNull(parameters, "month", month);
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
}
