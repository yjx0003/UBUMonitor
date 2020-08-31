package es.ubu.lsi.ubumonitor.controllers.load;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import es.ubu.lsi.ubumonitor.model.CourseEvent;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.DescriptionFormat;
import es.ubu.lsi.ubumonitor.model.ModuleType;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.webservice.api.core.calendar.CoreCalendarGetCalendarEvents;
import es.ubu.lsi.ubumonitor.webservice.webservices.WebService;

public class PopulateCourseEvent {

	private DataBase dataBase;
	private WebService webService;

	public PopulateCourseEvent(DataBase dataBase, WebService webService) {
		this.dataBase = dataBase;
		this.webService = webService;
	}

	public List<CourseEvent> populateCourseEvents(int courseid) {
		try {
			CoreCalendarGetCalendarEvents coreCalendarGetCalendarEvents = new CoreCalendarGetCalendarEvents();
			coreCalendarGetCalendarEvents.setCourseids(Collections.singleton(courseid));
			coreCalendarGetCalendarEvents.setSiteevents(false);
			coreCalendarGetCalendarEvents.setUserevents(false);
			JSONObject jsonObject = UtilMethods.getJSONObjectResponse(webService, coreCalendarGetCalendarEvents);
			return populateCourseEvents(jsonObject);
		} catch (Exception e) {
			return Collections.emptyList();
		}

	}

	public List<CourseEvent> populateCourseEvents(JSONObject jsonObject) {
		return populateCourseEvents(jsonObject.getJSONArray(Constants.EVENTS));
	}

	private List<CourseEvent> populateCourseEvents(JSONArray jsonArray) {
		List<CourseEvent> courseEvents = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); ++i) {
			courseEvents.add(populateCourseEvent(jsonArray.getJSONObject(i)));
		}
		return courseEvents;
	}

	private CourseEvent populateCourseEvent(JSONObject jsonObject) {
		CourseEvent courseEvent = dataBase.getCourseEvents()
				.getById(jsonObject.getInt(Constants.ID));
		courseEvent.setName(jsonObject.optString(Constants.NAME));
		courseEvent.setDescription(jsonObject.optString(Constants.DESCRIPTION));
		courseEvent.setFormat(DescriptionFormat.get(jsonObject.optInt(Constants.FORMAT)));
		courseEvent.setUser(dataBase.getUsers()
				.getById(jsonObject.optInt(Constants.USERID)));
		int instance = jsonObject.optInt(Constants.INSTANCE);
		ModuleType moduleType = ModuleType.get(jsonObject.optString(Constants.MODULENAME));	
		if (instance != 0 && moduleType!=null) {
			courseEvent.setCourseModule(dataBase.getModules()
					.getMap()
					.values()
					.stream()
					.filter(cm -> cm.getInstance() == instance && cm.getModuleType() == moduleType)
					.findFirst()
					.orElse(null));
		}

		courseEvent.setEventtype(jsonObject.optString(Constants.EVENTTYPE));
		courseEvent.setTimestart(Instant.ofEpochSecond(jsonObject.optLong(Constants.TIMESTART)));
		courseEvent.setTimeduration(jsonObject.optInt(Constants.TIMEDURATION));
		courseEvent.setTimemodified(Instant.ofEpochSecond(jsonObject.optLong(Constants.TIMEMODIFIED)));
		courseEvent.setVisible(jsonObject.optInt(Constants.VISIBLE) == 1);

		return courseEvent;
	}

}
