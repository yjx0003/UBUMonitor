package es.ubu.lsi.ubumonitor.view.chart.risk;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.Component;
import es.ubu.lsi.ubumonitor.model.ComponentEvent;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.Event;
import es.ubu.lsi.ubumonitor.model.LastActivity;
import es.ubu.lsi.ubumonitor.model.LastActivityFactory;
import es.ubu.lsi.ubumonitor.model.LogLine;
import es.ubu.lsi.ubumonitor.model.log.TypeTimes;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;
import javafx.scene.control.DatePicker;
import javafx.scene.paint.Color;

public class RiskBarTemporal extends Plotly {

	private static final ComponentEvent COURSE_ACCESS_EVENT = ComponentEvent.get(Component.SYSTEM, Event.COURSE_VIEWED);

	private DatePicker datePickerStart;
	private DatePicker datePickerEnd;

	public RiskBarTemporal(MainController mainController, DatePicker datePickerStart, DatePicker datePickerEnd) {
		super(mainController, ChartType.RISK_BAR_TEMPORAL);
		this.datePickerStart = datePickerStart;
		this.datePickerEnd = datePickerEnd;
		useRangeDate = true;
		useOptions = true;
		useLogs = true;
	}
	
	@Override
	public String getOnClickFunction() {
		return MULTIPLE_USER_ON_CLICK_FUNCTION;
	}

	@Override
	public void createData(JSArray data) {
		List<EnrolledUser> selectedEnrolledUser = getSelectedEnrolledUser();
		ZonedDateTime start = datePickerStart.getValue()
				.atStartOfDay(ZoneId.systemDefault());
		ZonedDateTime lastUpdate = datePickerEnd.getValue() == null ? start.plusDays(1)
				: datePickerEnd.getValue()
						.plusDays(1)
						.atStartOfDay(ZoneId.systemDefault());

		Map<LastActivity, List<EnrolledUser>> lastCourseAccess = new TreeMap<>(
				Comparator.comparing(LastActivity::getIndex));
		List<EnrolledUser> noActivity = new ArrayList<>();
		Map<EnrolledUser, Map<ComponentEvent, List<LogLine>>> logs = getUserLogs(selectedEnrolledUser, actualCourse);
		for (EnrolledUser user : selectedEnrolledUser) {
			ZonedDateTime lastUserLog = getLastLog(l -> !l.getTime()
					.isBefore(start) && l.getTime()
							.isBefore(lastUpdate),
					logs, user);

			if (lastUserLog != null) {
				lastCourseAccess
						.computeIfAbsent(LastActivityFactory.DEFAULT.getActivity(lastUserLog, lastUpdate),
								k -> new ArrayList<>())
						.add(user);
			} else {
				noActivity.add(user);
			}

		}

		List<LastActivity> lastActivities = LastActivityFactory.DEFAULT.getAllLastActivity();

		JSObject trace = createTrace(I18n.get("label.lastcourseaccess"), lastCourseAccess, noActivity,
				selectedEnrolledUser.size(), lastActivities, 0.2);

		data.add(trace);

	}

	private JSObject createTrace(String name, Map<LastActivity, List<EnrolledUser>> lastAccess,
			List<EnrolledUser> noActivity, int nUsers, List<LastActivity> lastActivities, double opacity) {
		JSObject trace = new JSObject();

		JSArray x = new JSArray();
		x.addAllWithQuote(lastActivities);
		x.addWithQuote(I18n.get("text.noActivity"));

		JSArray y = new JSArray();
		JSArray color = new JSArray();
		JSArray usersArray = new JSArray();
		JSArray usersIdArray = new JSArray();
		JSArray text = new JSArray();
		for (LastActivity lastActivity : lastActivities) {
			List<EnrolledUser> listUsers = lastAccess.computeIfAbsent(lastActivity, k -> Collections.emptyList());
			addAttributes(nUsers, opacity, y, color, usersArray, usersIdArray, text, listUsers,
					lastActivity.getColor());

		}
		addAttributes(nUsers, opacity, y, color, usersArray, usersIdArray, text, noActivity, Color.web("#808080"));
		trace.put("type", "'bar'");
		trace.putWithQuote("name", name);
		trace.put("x", x);
		trace.put("y", y);
		JSObject marker = new JSObject();
		marker.put("color", color);
		trace.put("marker", marker);
		trace.put("userids", usersIdArray);
		trace.put("customdata", usersArray);
		trace.put("text", text);
		trace.put("textposition", "'auto'");
		trace.put("hovertemplate", "'<b>%{x}<br>%{data.name}:</b> %{y}<br><br>%{customdata}<extra></extra>'");

		return trace;

	}

	public void addAttributes(int nUsers, double opacity, JSArray y, JSArray colors, JSArray usersArray,
			JSArray usersIdArray, JSArray text, List<EnrolledUser> listUsers, Color color) {
		colors.add(colorToRGB(color, opacity));

		StringBuilder users = new StringBuilder();
		JSArray usersId = new JSArray();

		y.add(listUsers.size());
		listUsers.forEach(e -> {
			usersId.add(e.getId());
			users.append(e.getFullName());
			users.append("<br>");
		});
		usersArray.addWithQuote(users);
		usersIdArray.add(usersId);
		text.add("'<b>'+toPercentage(" + listUsers.size() + "," + nUsers + ")+'</b>'");
	}

	@Override
	public void createLayout(JSObject layout) {
		boolean horizontalMode = getConfigValue("horizontalMode", false);
		JSObject axis = new JSObject();
		axis.put("showgrid", true);
		axis.put("tickmode", "'array'");
		axis.put("type", "'category'");
		axis.put("tickson", "'boundaries'");

		JSObject xaxis = horizontalMode ? new JSObject() : axis;

		JSObject yaxis = horizontalMode ? axis : new JSObject();

		if (horizontalMode) {
			Plotly.defaultAxisValues(xaxis, getYAxisTitle(), "");
			Plotly.defaultAxisValues(yaxis, getXAxisTitle(), null);
			yaxis.putAll(axis);

			layout.put("xaxis", yaxis);
			layout.put("yaxis", xaxis);
		} else {
			Plotly.defaultAxisValues(xaxis, getXAxisTitle(), null);
			Plotly.defaultAxisValues(yaxis, getYAxisTitle(), "");
			xaxis.putAll(axis);

			layout.put("xaxis", xaxis);
			layout.put("yaxis", yaxis);
		}

		layout.put("barmode", "'group'");
		layout.put("hovermode", "'closest'");

	}

	public static ZonedDateTime getLastLog(Predicate<LogLine> filter,
			Map<EnrolledUser, Map<ComponentEvent, List<LogLine>>> logs, EnrolledUser enrolledUser) {
		LogLine logLine = logs.get(enrolledUser)
				.get(COURSE_ACCESS_EVENT)
				.stream()
				.filter(filter)
				.max(Comparator.comparing(LogLine::getTime))
				.orElse(null);
		if (logLine == null) {
			return null;
		}
		return logLine.getTime();

	}

	public static Map<EnrolledUser, Map<ComponentEvent, List<LogLine>>> getUserLogs(List<EnrolledUser> users, Course actualCourse) {
		return actualCourse.getLogStats()
				.getByType(TypeTimes.ALL)
				.getComponentsEvents()
				.getUserLogs(users, Collections.singletonList(COURSE_ACCESS_EVENT), null, null);

	}

	@Override
	public String getXAxisTitle() {
		LocalDateTime start = datePickerStart.getValue()
				.atStartOfDay();

		LocalDateTime lastUpdate = datePickerEnd.getValue()
				.plusDays(1)
				.atStartOfDay();

		return MessageFormat.format(I18n.get(getChartType() + ".xAxisTitle"),
				start.format(Controller.DATE_TIME_FORMATTER), lastUpdate.format(Controller.DATE_TIME_FORMATTER));
	}

	@Override
	public void exportCSV(String path) throws IOException {
		ZonedDateTime start = datePickerStart.getValue()
				.atStartOfDay(ZoneId.systemDefault());
		ZonedDateTime lastUpdate = datePickerEnd.getValue()
				.atStartOfDay(ZoneId.systemDefault());
		List<EnrolledUser> users = getSelectedEnrolledUser();

		Map<EnrolledUser, Map<ComponentEvent, List<LogLine>>> logs = getUserLogs(users, actualCourse);

		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader("userid", "fullname", "courseAccess", "diffCourseAccess"))) {
			for (EnrolledUser enrolledUser : users) {
				ZonedDateTime lastUserLog = getLastLog(l -> !l.getTime()
						.isBefore(start) && l.getTime()
								.isBefore(lastUpdate),
						logs, enrolledUser);

				printer.print(enrolledUser.getId());
				printer.print(enrolledUser.getFullName());
				printer.print(lastUserLog == null ? null : Controller.DATE_TIME_FORMATTER.format(lastUserLog));
				printer.print(lastUserLog == null ? null : ChronoUnit.DAYS.between(lastUserLog, lastUpdate));
				printer.println();
			}
		}

	}

}
