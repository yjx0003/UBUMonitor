package es.ubu.lsi.ubumonitor.view.chart.calendarevents;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.CourseEvent;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.ModuleType;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.VisTimeline;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;

public class CalendarEventTimeline extends VisTimeline {
	private ListView<CourseModule> listViewCourseModule;

	public CalendarEventTimeline(MainController mainController, WebView webView,
			ListView<CourseModule> listViewCourseModule) {
		super(mainController, ChartType.CALENDAR_EVENT_TIMELINE, webView);
		this.listViewCourseModule = listViewCourseModule;
	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<CourseEvent> courseEvents = getSelectedCalendarEvents();

		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader("id", "name",
				"description", "courseModuleId", "courseModuleName", "start", "end"))) {
			for (CourseEvent courseEvent : courseEvents) {
				printer.print(courseEvent.getId());
				printer.print(courseEvent.getName());
				printer.print(courseEvent.getDescription());
				CourseModule cm = courseEvent.getCourseModule();
				if (cm == null) {
					printer.print(null);
					printer.print(null);
				} else {
					printer.print(cm.getCmid());
					printer.print(cm.getModuleName());
				}
				printer.print(Controller.DATE_TIME_FORMATTER
						.format(LocalDateTime.ofInstant(courseEvent.getTimestart(), ZoneId.systemDefault())));
				printer.print(Controller.DATE_TIME_FORMATTER.format(LocalDateTime.ofInstant(courseEvent.getTimestart()
						.plusSeconds(courseEvent.getTimeduration()), ZoneId.systemDefault())));
				printer.println();
			}
		}

	}

	@Override
	public String getOptions(JSObject jsObject) {
		JSObject options = new JSObject();
		Instant startDate = actualCourse.getStartDate();
		if (!Instant.EPOCH.equals(startDate)) {
			options.putWithQuote("start", startDate);
		}

		Instant endDate = actualCourse.getEndDate();
		if (!Instant.EPOCH.equals(startDate)) {
			options.putWithQuote("end", endDate);
		}

		jsObject.put("options", options);
		return jsObject.toString();
	}

	@Override
	public void update() {
		List<CourseEvent> calendarEvents = getSelectedCalendarEvents();

		JSObject data = new JSObject();
		data.put("items", createItems(calendarEvents));
		data.put("groups", createGroups(calendarEvents));

		webViewChartsEngine.executeScript("updateVisTimeline(" + data + "," + getOptions() + ")");
	}

	private JSArray createGroups(List<CourseEvent> calendarEvents) {
		List<ModuleType> modulesTypes = calendarEvents.stream()
				.map(c -> Optional.ofNullable(c)
						.map(CourseEvent::getCourseModule)
						.map(CourseModule::getModuleType)
						.orElse(ModuleType.MODULE))
				.distinct()
				.collect(Collectors.toList());
		JSArray groups = new JSArray();
		for (ModuleType moduleType : modulesTypes) {
			JSObject group = new JSObject();
			group.put("id", moduleType.ordinal());
			group.putWithQuote("content", "<img style='vertical-align:middle' src='../img/" + moduleType + ".png'>	"
					+ I18n.get(moduleType));
			groups.add(group);
		}
		return groups;
	}

	private JSArray createItems(List<CourseEvent> calendarEvents) {
		JSArray items = new JSArray();
		for (CourseEvent calendarEvent : calendarEvents) {
			JSObject item = new JSObject();
			item.put("id", calendarEvent.getId());

			ModuleType moduleType = Optional.ofNullable(calendarEvent)
					.map(CourseEvent::getCourseModule)
					.map(CourseModule::getModuleType)
					.orElse(ModuleType.MODULE);

			item.put("group", moduleType.ordinal());
			String image = "<img style='vertical-align:middle' src='../img/" + moduleType + ".png'>	";
			item.putWithQuote("content", image + calendarEvent.getName());
			item.putWithQuote("title", image + calendarEvent.getDescription());
			item.putWithQuote("start", calendarEvent.getTimestart());
			if (calendarEvent.getTimeduration() != 0) {
				item.putWithQuote("end", calendarEvent.getTimestart()
						.plusSeconds(calendarEvent.getTimeduration()));
			}

			items.add(item);
		}
		return items;
	}

	public List<CourseEvent> getSelectedCalendarEvents() {
		Set<CourseModule> selectedCourseModules = new HashSet<>(listViewCourseModule.getSelectionModel()
				.getSelectedItems());
		return actualCourse.getCourseEvents()
				.stream()
				.filter(calendarEvent -> selectedCourseModules.contains(calendarEvent.getCourseModule()))
				.collect(Collectors.toList());

	}

}
