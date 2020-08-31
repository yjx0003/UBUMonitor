package es.ubu.lsi.ubumonitor.webservice.api.core.calendar;

import java.util.Collection;

import org.json.JSONObject;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreCalendarGetCalendarEvents extends WSFunctionAbstract {

	private JSONObject events;
	private JSONObject options;

	public CoreCalendarGetCalendarEvents() {
		super(WSFunctionEnum.CORE_CALENDAR_GET_CALENDAR_EVENTS);
		events = new JSONObject();
		options = new JSONObject();
	}

	public void setEventids(Collection<Integer> eventids) {
		events.putOpt("eventids", eventids);
	}

	public void setCourseids(Collection<Integer> courseids) {
		events.putOpt("courseids", courseids);
	}

	public void setGroupids(Collection<Integer> groupids) {
		events.putOpt("groupids", groupids);
	}

	public void setCategoryids(Collection<Integer> categoryids) {
		events.putOpt("categoryids", categoryids);
	}

	public void setUserevents(boolean userevents) {
		options.put("userevents", userevents ? 1 : 0);
	}

	public void setSiteevents(boolean siteevents) {
		options.put("siteevents", siteevents ? 1 : 0);
	}
	
	public void setTimestart(int timestart) {
		options.put("timestart", timestart);
	}
	
	public void setTimeend(int timeend) {
		options.put("timeend", timeend);
	}

	@Override
	public void addToMapParemeters() {
		if (!events.isEmpty())
			parameters.put("events", events);
		if (!options.isEmpty())
			parameters.put("options", options);

	}

}
