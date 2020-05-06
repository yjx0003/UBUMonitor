package es.ubu.lsi.ubumonitor.webservice.api.core.calendar;

import es.ubu.lsi.ubumonitor.webservice.webservices.Util;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreCalendarGetCalendarDayView extends WSFunctionAbstract{

	private Integer year;
	private Integer month;
	private Integer day;
	
	public CoreCalendarGetCalendarDayView(Integer year, Integer month, Integer day) {
		super(WSFunctionEnum.CORE_CALENDAR_GET_CALENDAR_DAY_VIEW);
		this.year = year;
		this.month = month;
		this.day = day;
	}

	@Override
	public void addToMapParemeters() {
		Util.putIfNotNull(parameters, "year", year);
		Util.putIfNotNull(parameters, "month", month);
		Util.putIfNotNull(parameters, "day", day);
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

}
