package es.ubu.lsi.ubumonitor.view.chart.risk;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Tabs;
import javafx.scene.control.DatePicker;

public class RiskBarTemporal extends RiskBar {
	private DatePicker datePicker;

	public RiskBarTemporal(MainController mainController) {
		super(mainController, ChartType.RISK_BAR_TEMPORAL, Tabs.RISK);
		datePicker = mainController.getRiskController()
				.getDatePicker();
	}

	@Override
	public void exportCSV(String path) throws IOException {
		ZonedDateTime lastUpdate = datePicker.getValue()
				.atStartOfDay(ZoneId.systemDefault());
		List<EnrolledUser> users = getSelectedEnrolledUser();
		ComponentEvent courseAccessEvent = ComponentEvent.get(Component.SYSTEM, Event.COURSE_VIEWED);
		Map<EnrolledUser, Map<ComponentEvent, List<LogLine>>> logs = Controller.getInstance()
				.getActualCourse()
				.getLogStats()
				.getByType(TypeTimes.ALL)
				.getComponentsEvents()
				.getUserLogs(users, Collections.singletonList(courseAccessEvent), null, null);

		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader("userid", "fullname", "courseAccess", "diffCourseAccess"))) {
			for (EnrolledUser enrolledUser : users) {
				LogLine lastUserLog = logs.get(enrolledUser)
						.get(courseAccessEvent)
						.stream()
						.filter(l -> !l.getTime()
								.isAfter(lastUpdate))
						.max(Comparator.comparing(LogLine::getTime))
						.orElse(null);

				printer.print(enrolledUser.getId());
				printer.print(enrolledUser.getFullName());
				printer.print(
						lastUserLog == null ? null : Controller.DATE_TIME_FORMATTER.format(lastUserLog.getTime()));
				printer.print(lastUserLog == null ? null : ChronoUnit.DAYS.between(lastUserLog.getTime(), lastUpdate));
				printer.println();
			}
		}

	}

	@Override
	protected String createDataset(List<EnrolledUser> selectedEnrolledUser) {

		ZonedDateTime lastUpdate = datePicker.getValue()
				.atStartOfDay(ZoneId.systemDefault());

		ComponentEvent courseAccessEvent = ComponentEvent.get(Component.SYSTEM, Event.COURSE_VIEWED);
		Map<LastActivity, List<EnrolledUser>> lastCourseAccess = new TreeMap<>(
				Comparator.comparing(LastActivity::getIndex));
		Map<EnrolledUser, Map<ComponentEvent, List<LogLine>>> logs = Controller.getInstance()
				.getActualCourse()
				.getLogStats()
				.getByType(TypeTimes.ALL)
				.getComponentsEvents()
				.getUserLogs(selectedEnrolledUser, Collections.singletonList(courseAccessEvent), null, null);
		for (EnrolledUser user : selectedEnrolledUser) {
			LogLine lastUserLog = logs.get(user)
					.get(courseAccessEvent)
					.stream()
					.filter(l -> !l.getTime()
							.isAfter(lastUpdate))
					.max(Comparator.comparing(LogLine::getTime))
					.orElse(null);

			if (lastUserLog != null) {
				lastCourseAccess
						.computeIfAbsent(LastActivityFactory.getActivity(lastUserLog.getTime(), lastUpdate),
								k -> new ArrayList<>())
						.add(user);
			}

		}

		Set<LastActivity> lastActivities = LastActivityFactory.getAllLastActivity();

		JSObject data = new JSObject();
		JSArray labels = new JSArray();
		labels.addAllWithQuote(lastActivities);
		data.put("labels", labels);
		JSArray datasets = new JSArray();
		datasets.add(createDataset("Last course access", lastCourseAccess, lastActivities));

		data.put("datasets", datasets);
		return data.toString();
	}

}
