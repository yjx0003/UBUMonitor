package es.ubu.lsi.ubumonitor.export.builder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.export.CSVBuilderAbstract;
import es.ubu.lsi.ubumonitor.model.CourseEvent;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;

public class CSVCalendarEvent extends CSVBuilderAbstract{
	/** Header. */
	private static final String[] HEADER = new String[] { "id", "name",
			"description", "courseModuleId", "courseModuleName","userId", "userName", "start", "end" };
	public CSVCalendarEvent(String name, DataBase dataBase) {
		super(name, dataBase, HEADER);
	}
	@Override
	public void buildBody() {
		Set<CourseEvent> courseEvents = getDataBase().getActualCourse().getCourseEvents();
		
		for(CourseEvent courseEvent:courseEvents) {
			CourseModule cm = courseEvent.getCourseModule();
			EnrolledUser user = courseEvent.getUser();
			getData().add(new String[] { 
					Integer.toString(courseEvent.getId()),
					courseEvent.getName(),
					courseEvent.getDescription(),
					cm == null ? null : Integer.toString(cm.getCmid()),
					cm == null ? null : cm.getModuleName(),
					Integer.toString(user.getId()),
					user.getFullName(),
					Controller.DATE_TIME_FORMATTER.format(LocalDateTime.ofInstant(courseEvent.getTimestart(), ZoneId.systemDefault())),
					Controller.DATE_TIME_FORMATTER.format(LocalDateTime.ofInstant(courseEvent.getTimestart()
							.plusSeconds(courseEvent.getTimeduration()), ZoneId.systemDefault()))
			});
		}
	}

}
