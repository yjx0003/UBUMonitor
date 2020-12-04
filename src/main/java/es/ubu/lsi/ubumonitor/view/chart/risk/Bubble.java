package es.ubu.lsi.ubumonitor.view.chart.risk;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;
import javafx.scene.paint.Color;

public class Bubble extends Plotly {

	public Bubble(MainController mainController) {
		super(mainController, ChartType.BUBBLE);
		useGeneralButton = false;
		useLegend = true;
		useGroupButton = false;
	}

	@Override
	public String getOnClickFunction() {
		
		return Plotly.MULTIPLE_USER_ON_CLICK_FUNCTION;
	}
	
	@Override
	public void createData(JSArray data) {
		List<EnrolledUser> selectedEnrolledUser = getSelectedEnrolledUser();
		int limit = getConfigValue("limitDays");
		Map<Long, Map<Long, List<EnrolledUser>>> lastAccess = createLastAccess(selectedEnrolledUser, limit);

		long p25 = Math.round(0.25 * limit);
		long p50 = Math.round(0.50 * limit);
		long p75 = Math.round(0.75 * limit);
		Color c25 = getConfigValue("firstInterval");
		Color c50 = getConfigValue("secondInterval");
		Color c75 = getConfigValue("thirdInterval");
		Color c100 = getConfigValue("fourthInterval");

		JSObject trace25 = createTrace(String.format("[0, %d)", p25), c25);
		JSObject trace50 = createTrace(String.format("[%d, %d)", p25, p50), c50);
		JSObject trace75 = createTrace(String.format("[%d, %d)", p50, p75), c75);
		JSObject trace100 = createTrace(String.format("[%d, ∞)", p75), c100);
		JSObject diagonalLine = createDiagonalLineTrace(limit, colorToRGB(getConfigValue("diagonalColor")));

		data.add(diagonalLine);
		data.add(trace25);
		data.add(trace50);
		data.add(trace75);
		data.add(trace100);

		for (Map.Entry<Long, Map<Long, List<EnrolledUser>>> entry : lastAccess.entrySet()) {
			long xValue = entry.getKey();
			Map<Long, List<EnrolledUser>> map = entry.getValue();
			for (Map.Entry<Long, List<EnrolledUser>> entryUsers : map.entrySet()) {
				long yValue = entryUsers.getKey();

				JSObject trace;
				if (xValue < p25) {
					trace = trace25;
				} else if (xValue < p50) {
					trace = trace50;
				} else if (xValue < p75) {
					trace = trace75;
				} else {
					trace = trace100;
				}
				List<EnrolledUser> users = map.getOrDefault(yValue, Collections.emptyList());
				JSArray x = (JSArray) trace.get("x");
				JSArray y = (JSArray) trace.get("y");
				JSArray text = (JSArray) trace.get("text");
				JSArray allUserNames = (JSArray) trace.get("customdata");
				JSArray allUserIds = (JSArray) trace.get("userids");
				JSArray size = (JSArray) ((JSObject) trace.get("marker")).get("size");

				x.add(xValue);
				y.add(yValue);
				text.add(users.size());
				size.add(users.size() * 5);

				StringBuilder usersName = new StringBuilder();
				JSArray usersId = new JSArray();
				users.forEach(u -> {
					usersName.append(u.getFullName());
					usersName.append("<br>");
					usersId.add(u.getId());
				});
				allUserNames.addWithQuote(usersName);
				allUserIds.add(usersId);
			}

		}

	}

	public static JSObject createDiagonalLineTrace(int limit, String color) {
		List<Integer> values = IntStream.rangeClosed(0, limit)
				.boxed()
				.collect(Collectors.toList());
		JSObject trace = new JSObject();
		trace.put("mode", "'lines'");
		trace.put("hoverinfo", "'none'");
		trace.put("showlegend", false);
		JSObject marker = new JSObject();

		marker.put("color", color);
		trace.put("marker", marker);
		trace.put("x", values);
		trace.put("y", values);
		return trace;
	}

	@Override
	public void createLayout(JSObject layout) {

		int limit = getConfigValue("limitDays");

		JSArray ticktext = new JSArray();
		for (int i = 0; i < limit; ++i) {

			ticktext.addWithQuote(i);

		}
		ticktext.addWithQuote("+" + limit);

		JSObject xaxis = new JSObject();
		defaultAxisValues(xaxis, getXAxisTitle(), null);
		createCategoryAxis(xaxis, ticktext);

		JSObject yaxis = new JSObject();
		defaultAxisValues(yaxis, getYAxisTitle(), null);
		createCategoryAxis(yaxis, ticktext);

		layout.put("xaxis", xaxis);
		layout.put("yaxis", yaxis);
		layout.put("hovermode", "'closest'");

	}

	private JSObject createTrace(String name, Color color) {
		JSObject trace = new JSObject();
		trace.put("type", "'scatter'");
		trace.put("mode", "'markers+text'");
		trace.putWithQuote("name", name);
		trace.put("text", new JSArray());
		trace.put("x", new JSArray());
		trace.put("y", new JSArray());
		JSObject marker = new JSObject();
		marker.put("size", new JSArray());
		marker.put("color", colorToRGB(color, 0.2));
		JSObject line = new JSObject();
		line.put("width", 2);
		line.put("color", colorToRGB(color));
		marker.put("line", line);
		trace.put("marker", marker);
		trace.put("customdata", new JSArray());
		trace.put("userids", new JSArray());
		trace.put("hovertemplate", "'<b>" + I18n.get("label.course") + " </b>%{x}<br><b>" + I18n.get("text.moodle")
				+ " </b>%{y}<br><br>%{customdata}<extra></extra>'");
		return trace;
	}

	/**
	 * Create map with the instant last course access then server last access
	 * 
	 * @param selectedEnrolledUser selected users
	 * @param limit                limit value for user not enter
	 * @return map
	 */
	public Map<Long, Map<Long, List<EnrolledUser>>> createLastAccess(List<EnrolledUser> selectedEnrolledUser,
			int limit) {
		ZonedDateTime lastLogTime = Controller.getInstance()
				.getUpdatedCourseData();
		Map<Long, Map<Long, List<EnrolledUser>>> lastAccess = new TreeMap<>();

		for (EnrolledUser user : selectedEnrolledUser) {
			Instant lastCourseAccess = user.getLastcourseaccess() == null ? Instant.EPOCH : user.getLastcourseaccess();
			Instant lastMoodle = user.getLastaccess() == null ? Instant.EPOCH : user.getLastaccess();
			long diffCourse = Math.min(Math.max(0L, ChronoUnit.DAYS.between(lastCourseAccess, lastLogTime)), limit);
			long diffServer = Math.min(Math.max(0, ChronoUnit.DAYS.between(lastMoodle, lastLogTime)), limit);

			lastAccess.computeIfAbsent(diffCourse, k -> new TreeMap<>())
					.computeIfAbsent(diffServer, k -> new ArrayList<>())
					.add(user);

		}
		return lastAccess;
	}

	@Override
	public String getXAxisTitle() {

		return MessageFormat.format(super.getXAxisTitle(), Controller.getInstance()
				.getUpdatedCourseData()
				.format(Controller.DATE_TIME_FORMATTER));
	}

	@Override
	public String getYAxisTitle() {

		return MessageFormat.format(super.getYAxisTitle(), Controller.getInstance()
				.getUpdatedCourseData()
				.format(Controller.DATE_TIME_FORMATTER));
	}

	@Override
	public void exportCSV(String path) throws IOException {
		ZonedDateTime updateTime = Controller.getInstance()
				.getUpdatedCourseData();
		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader("userid", "fullname",
				"lastCourseAccess", "lastMoodleAcess", "diffCourseAccess", "diffMoodleAccess"))) {
			for (EnrolledUser enrolledUser : getSelectedEnrolledUser()) {
				printer.printRecord(enrolledUser.getId(), enrolledUser.getFullName(),
						Controller.DATE_TIME_FORMATTER.format(enrolledUser.getLastcourseaccess()
								.atZone(ZoneId.systemDefault())),
						Controller.DATE_TIME_FORMATTER.format(enrolledUser.getLastaccess()
								.atZone(ZoneId.systemDefault())),
						ChronoUnit.DAYS.between(enrolledUser.getLastcourseaccess(), updateTime),
						ChronoUnit.DAYS.between(enrolledUser.getLastaccess(), updateTime));

			}
		}

	}
}
