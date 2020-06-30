package es.ubu.lsi.ubumonitor.webservice.api.core.calendar;

import java.util.HashSet;
import java.util.Set;

import es.ubu.lsi.ubumonitor.webservice.webservices.Util;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreCalendarGetCalendarEvents extends WSFunctionAbstract{

	private Set<Integer> eventids;
	private Set<Integer> courseids;
	private Set<Integer> groupids;
	private Set<Integer> categoryids;
	
	private Boolean userevents;
	private Boolean siteevents;
	private Integer timestart;
	private Integer timeend;
	private Boolean ignorehidden;
	
	public CoreCalendarGetCalendarEvents() {
		super(WSFunctionEnum.CORE_CALENDAR_GET_CALENDAR_EVENTS);
		eventids = new HashSet<>();
		courseids = new HashSet<>();
		groupids = new HashSet<>();
		categoryids = new HashSet<>();
	}

	public Set<Integer> getEventids() {
		return eventids;
	}

	public void setEventids(Set<Integer> eventids) {
		this.eventids = eventids;
	}

	public Set<Integer> getCourseids() {
		return courseids;
	}

	public void setCourseids(Set<Integer> courseids) {
		this.courseids = courseids;
	}

	public Set<Integer> getGroupids() {
		return groupids;
	}

	public void setGroupids(Set<Integer> groupids) {
		this.groupids = groupids;
	}

	public Set<Integer> getCategoryids() {
		return categoryids;
	}

	public void setCategoryids(Set<Integer> categoryids) {
		this.categoryids = categoryids;
	}

	public Boolean getUserevents() {
		return userevents;
	}

	public void setUserevents(Boolean userevents) {
		this.userevents = userevents;
	}

	public Boolean getSiteevents() {
		return siteevents;
	}

	public void setSiteevents(Boolean siteevents) {
		this.siteevents = siteevents;
	}

	public Integer getTimestart() {
		return timestart;
	}

	public void setTimestart(Integer timestart) {
		this.timestart = timestart;
	}

	public Integer getTimeend() {
		return timeend;
	}

	public void setTimeend(Integer timeend) {
		this.timeend = timeend;
	}

	public Boolean getIgnorehidden() {
		return ignorehidden;
	}

	public void setIgnorehidden(Boolean ignorehidden) {
		this.ignorehidden = ignorehidden;
	}

	@Override
	public void addToMapParemeters() {
		String events = "events";
		String options = "options";
		Util.putIfNotNull(parameters, events, "eventids", eventids);
		Util.putIfNotNull(parameters, events, "courseids", courseids);
		Util.putIfNotNull(parameters, events, "groupids", groupids);
		Util.putIfNotNull(parameters, events, "categoryids", categoryids);
		
		Util.putIfNotNull(parameters, options, "userevents", Util.booleanToBinary(userevents));
		Util.putIfNotNull(parameters, options, "siteevents", Util.booleanToBinary(siteevents));
		Util.putIfNotNull(parameters, options, "timestart", timestart);
		Util.putIfNotNull(parameters, options, "timeend", timeend);
		Util.putIfNotNull(parameters, options, "ignorehidden", Util.booleanToBinary(ignorehidden));
		
	}

}
