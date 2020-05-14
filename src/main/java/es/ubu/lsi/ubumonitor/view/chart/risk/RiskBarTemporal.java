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
import es.ubu.lsi.ubumonitor.view.chart.Tabs;
import javafx.scene.control.DatePicker;

public class RiskBarTemporal extends RiskBar {

	private static final ComponentEvent COURSE_ACCESS_EVENT = ComponentEvent.get(Component.SYSTEM, Event.COURSE_VIEWED);

	public RiskBarTemporal(MainController mainController, DatePicker datePickerStart, DatePicker datePickerEnd) {
		super(mainController, ChartType.RISK_BAR_TEMPORAL, Tabs.RISK);
		this.datePickerStart = datePickerStart;
		this.datePickerEnd = datePickerEnd;
		useRangeDate = true;
		useOptions = true;
	}

	public RiskBarTemporal(MainController mainController, ChartType chartType) {
		super(mainController, chartType, Tabs.RISK);
	}

	@Override
	public void exportCSV(String path) throws IOException {
		ZonedDateTime start = datePickerStart.getValue()
				.atStartOfDay(ZoneId.systemDefault());
		ZonedDateTime lastUpdate = datePickerEnd.getValue()
				.atStartOfDay(ZoneId.systemDefault());
		List<EnrolledUser> users = getSelectedEnrolledUser();

		Map<EnrolledUser, Map<ComponentEvent, List<LogLine>>> logs = getUserLogs(users);

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

	public ZonedDateTime getLastLog(Predicate<LogLine> filter,
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

	public Map<EnrolledUser, Map<ComponentEvent, List<LogLine>>> getUserLogs(List<EnrolledUser> users) {
		return Controller.getInstance()
				.getActualCourse()
				.getLogStats()
				.getByType(TypeTimes.ALL)
				.getComponentsEvents()
				.getUserLogs(users, Collections.singletonList(COURSE_ACCESS_EVENT), null, null);

	}

	@Override
	protected String createDataset(List<EnrolledUser> selectedEnrolledUser) {

		ZonedDateTime start = datePickerStart.getValue()
				.atStartOfDay(ZoneId.systemDefault());
		ZonedDateTime lastUpdate = datePickerEnd.getValue() == null ? start
				: datePickerEnd.getValue().plusDays(1)
						.atStartOfDay(ZoneId.systemDefault());

		Map<LastActivity, List<EnrolledUser>> lastCourseAccess = new TreeMap<>(
				Comparator.comparing(LastActivity::getIndex));
		List<EnrolledUser> noActivity = new ArrayList<>();
		Map<EnrolledUser, Map<ComponentEvent, List<LogLine>>> logs = getUserLogs(selectedEnrolledUser);
		for (EnrolledUser user : selectedEnrolledUser) {
			ZonedDateTime lastUserLog = getLastLog(l -> !l.getTime()
					.isBefore(start) && l.getTime()
							.isBefore(lastUpdate),
					logs, user);

			if (lastUserLog != null) {
				lastCourseAccess
						.computeIfAbsent(LastActivityFactory.getActivity(lastUserLog, lastUpdate),
								k -> new ArrayList<>())
						.add(user);
			} else {
				noActivity.add(user);
			}

		}

		List<LastActivity> lastActivities = LastActivityFactory.getAllLastActivity();

		JSObject data = new JSObject();
		JSArray labels = new JSArray();
		labels.addAllWithQuote(lastActivities);
		labels.addWithQuote(I18n.get("text.noActivity"));
		data.put("labels", labels);
		JSArray datasets = new JSArray();
		JSObject dataset = createDataset(I18n.get("label.lastcourseaccess"), lastCourseAccess, lastActivities, 0.2);
		addNoActivity(dataset, noActivity);
		datasets.add(dataset);
		data.put("datasets", datasets);
		return data.toString();
	}

	private void addNoActivity(JSObject dataset, List<EnrolledUser> noActivity) {
		JSArray data = (JSArray) dataset.get("data");
		data.add(noActivity.size());
		JSArray usersArray = (JSArray) dataset.get("users");
		JSArray usersIdArray = (JSArray) dataset.get("usersId");
		JSArray users = new JSArray();
		JSArray usersId = new JSArray();
		for (EnrolledUser user : noActivity) {
			users.addWithQuote(user.getFullName());
			usersId.add(user.getId());
		}
		usersArray.add(users);
		usersIdArray.add(usersId);
	}

	@Override
	public String getXAxisTitle() {
		LocalDateTime start = datePickerStart.getValue()
				.atStartOfDay();

		LocalDateTime lastUpdate = datePickerEnd.getValue()
				.atStartOfDay();

		return MessageFormat.format(I18n.get(getChartType() + ".xAxisTitle"),
				start.format(Controller.DATE_TIME_FORMATTER), lastUpdate.format(Controller.DATE_TIME_FORMATTER));
	}

}
