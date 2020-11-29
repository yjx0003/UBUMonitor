package es.ubu.lsi.ubumonitor.view.chart.calendarevents;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

public class CalendarEventTimeline extends VisTimeline {
	private ListView<CourseModule> listViewCourseModule;
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL,
			FormatStyle.SHORT);

	public CalendarEventTimeline(MainController mainController, ListView<CourseModule> listViewCourseModule) {
		super(mainController, ChartType.CALENDAR_EVENT_TIMELINE);
		this.listViewCourseModule = listViewCourseModule;
	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<CourseEvent> courseEvents = getSelectedCalendarEvents();

		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader("id", "name",
				"description", "courseModuleId", "courseModuleName", "userId", "userName", "start", "end"))) {
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
				printer.print(courseEvent.getUser()
						.getId());
				printer.print(courseEvent.getUser()
						.getFullName());
				printer.print(Controller.DATE_TIME_FORMATTER
						.format(LocalDateTime.ofInstant(courseEvent.getTimestart(), ZoneId.systemDefault())));
				printer.print(Controller.DATE_TIME_FORMATTER.format(LocalDateTime.ofInstant(courseEvent.getTimestart()
						.plusSeconds(courseEvent.getTimeduration()), ZoneId.systemDefault())));
				printer.println();
			}
		}

	}

	@Override
	public void fillOptions(JSObject jsObject) {
		JSObject options = new JSObject();
		Instant startDate = actualCourse.getStartDate();
		if (!Instant.EPOCH.equals(startDate)) {
			options.putWithQuote("start", startDate);
		}

		Instant endDate = actualCourse.getEndDate();
		if (!Instant.EPOCH.equals(startDate)) {
			options.putWithQuote("end", endDate);
		}
		options.put("minHeight", "'90vh'");
		options.put("tooltip", "{overflowMethod:'cap'}");
		options.put("order", "function(a,b){return b.epoch-a.epoch}");
		jsObject.put("options", options);
		
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
			group.putWithQuote("content", "<img style='vertical-align:middle' src='../img/" + moduleType.getModName()
					+ ".png'>	" + I18n.get(moduleType));
			groups.add(group);
		}
		return groups;
	}

	private JSArray createItems(List<CourseEvent> calendarEvents) {
		Map<CourseEvent, CourseEvent> map = createOpenCloseCourseEvent(calendarEvents);

		JSArray items = new JSArray();
		for (Map.Entry<CourseEvent, CourseEvent> entry : map.entrySet()) {

			CourseEvent open = entry.getKey();
			CourseEvent close = entry.getValue();

			JSObject item = new JSObject();
			item.put("id", open.getId());
			item.put("epoch", open.getTimestart() == null ? Instant.MAX.getEpochSecond()
					: open.getTimestart()
							.getEpochSecond());
			ModuleType moduleType = Optional.ofNullable(open)
					.map(CourseEvent::getCourseModule)
					.map(CourseModule::getModuleType)
					.orElse(ModuleType.MODULE);

			item.put("group", moduleType.ordinal());

			String image = "<img style='vertical-align:middle' src='../img/" + moduleType.getModName() + ".png'>	";
			fillItem(image, item, open, close);

			items.add(item);
		}

		return items;
	}

	private void fillItem(String image, JSObject item, CourseEvent open, CourseEvent close) {

		item.putWithQuote("content", image + open.getName());

		item.putWithQuote("start", open.getTimestart());
		StringBuilder builder = new StringBuilder();
		builder.append("<p style='line-height: 2;'>");
		builder.append(image);
		builder.append(open.getName());
		builder.append("<br><b>");
		builder.append(open.getUser()
				.getFullName());
		builder.append("</b><br>");

		if (close != null) {
			builder.append(LocalDateTime.ofInstant(open.getTimestart(), ZoneId.systemDefault())
					.format(DATE_TIME_FORMATTER));
			builder.append(" - ");
			builder.append(LocalDateTime.ofInstant(close.getTimestart(), ZoneId.systemDefault())
					.format(DATE_TIME_FORMATTER));
			builder.append("<br>");
			String timeElapsed = MessageFormat.format(I18n.get("text.daydifference"), open.getTimestart()
					.until(close.getTimestart(), ChronoUnit.DAYS),
					open.getTimestart()
							.until(close.getTimestart(), ChronoUnit.HOURS) % 24);
			builder.append(timeElapsed);
			item.putWithQuote("end", close.getTimestart());
			item.put("type", "'range'");
		} else if (open.getTimeduration() != 0) {
			Instant end = open.getTimestart()
					.plusSeconds(open.getTimeduration());

			builder.append(LocalDateTime.ofInstant(open.getTimestart(), ZoneId.systemDefault())
					.format(DATE_TIME_FORMATTER));
			builder.append(" - ");
			builder.append(LocalDateTime.ofInstant(end, ZoneId.systemDefault())
					.format(DATE_TIME_FORMATTER));
			builder.append("<br>");
			String timeElapsed = MessageFormat.format(I18n.get("text.daydifference"), open.getTimestart()
					.until(end, ChronoUnit.DAYS),
					open.getTimestart()
							.until(end, ChronoUnit.HOURS) % 24);
			builder.append(timeElapsed);
			item.putWithQuote("end", end);
			item.put("type", "'range'");
		} else {
			builder.append(LocalDateTime.ofInstant(open.getTimestart(), ZoneId.systemDefault())
					.format(DATE_TIME_FORMATTER));
			builder.append("<br>");
			item.put("type", "'point'");
		}
		builder.append("</p>");
		builder.append("<hr>");
		builder.append(open.getDescription());

		item.putWithQuote("title", builder);
	}

	private Map<CourseEvent, CourseEvent> createOpenCloseCourseEvent(List<CourseEvent> calendarEvents) {
		Map<CourseEvent, CourseEvent> map = new HashMap<>();
		Map<String, Set<CourseEvent>> eventsByType = calendarEvents.stream()
				.filter(e -> e.getEventtype() != null)
				.collect(Collectors.groupingBy(CourseEvent::getEventtype, Collectors.toSet()));
		Set<CourseEvent> openEvents = eventsByType.get("open");
		Set<CourseEvent> closeEvents = eventsByType.get("close");

		if (openEvents != null && closeEvents != null) {
			for (CourseEvent openEvent : openEvents) {
				CourseModule openCourseModule = openEvent.getCourseModule();
				if (openCourseModule != null) {
					CourseEvent closeEvent = closeEvents.stream()
							.filter(close -> openCourseModule.equals(close.getCourseModule()))
							.findFirst()
							.orElse(null);
					closeEvents.remove(closeEvent);
					map.put(openEvent, closeEvent);
				}

			}
			// add orphan close events to map
			closeEvents.forEach(closeEvent -> map.put(closeEvent, null));
			eventsByType.remove("open");
			eventsByType.remove("close");

		}
		for (Set<CourseEvent> events : eventsByType.values()) {
			for (CourseEvent event : events) {
				map.put(event, null);
			}
		}
		return map;

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
