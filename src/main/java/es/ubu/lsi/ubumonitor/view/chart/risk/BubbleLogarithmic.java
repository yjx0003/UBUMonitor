package es.ubu.lsi.ubumonitor.view.chart.risk;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.LastActivity;
import es.ubu.lsi.ubumonitor.model.LastActivityFactory;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;
import javafx.scene.paint.Color;

public class BubbleLogarithmic extends Plotly {

	public BubbleLogarithmic(MainController mainController) {
		super(mainController, ChartType.BUBBLE_LOGARITHMIC);
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
		LastActivityFactory lastActivityFactory = generateLastActivityFactory();
		Map<LastActivity, Map<LastActivity, List<EnrolledUser>>> lastActivities = new TreeMap<>();
		ZonedDateTime lastLogTime = Controller.getInstance()
				.getUpdatedCourseData();

		for (EnrolledUser enrolledUser : selectedEnrolledUser) {
			LastActivity lastActivityCourse = lastActivityFactory.getActivity(enrolledUser.getLastcourseaccess(),
					lastLogTime);
			LastActivity lastActivityMoodle = lastActivityFactory.getActivity(enrolledUser.getLastaccess(),
					lastLogTime);
			lastActivities.computeIfAbsent(lastActivityCourse, k -> new TreeMap<>())
					.computeIfAbsent(lastActivityMoodle, k -> new ArrayList<>())
					.add(enrolledUser);
		}
		Map<Color, String> names = getTraceNames(lastActivityFactory);
		Map<Color, JSObject> tracesMap = new LinkedHashMap<>();
		for (Map.Entry<LastActivity, Map<LastActivity, List<EnrolledUser>>> entryCourse : lastActivities.entrySet()) {
			LastActivity lastActivityCourse = entryCourse.getKey();
			for (Map.Entry<LastActivity, List<EnrolledUser>> entryMoodle : entryCourse.getValue()
					.entrySet()) {
				LastActivity lastActivityMoodle = entryMoodle.getKey();
				List<EnrolledUser> users = entryMoodle.getValue();
				JSObject trace = tracesMap.computeIfAbsent(lastActivityCourse.getColor(),
						c -> createTrace(names.get(c), c));

				JSArray x = (JSArray) trace.get("x");
				JSArray y = (JSArray) trace.get("y");
				JSArray text = (JSArray) trace.get("text");
				JSArray allUserNames = (JSArray) trace.get("customdata");
				JSArray allUserIds = (JSArray) trace.get("userids");
				JSArray size = (JSArray) ((JSObject) trace.get("marker")).get("size");

				x.add(lastActivityCourse.getIndex());
				y.add(lastActivityMoodle.getIndex());
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

		data.add(Bubble.createDiagonalLineTrace(lastActivityFactory.getAllLastActivity().size(), colorToRGB(getConfigValue("diagonalColor"))));
		tracesMap.values()
				.forEach(data::add);

	}

	@Override
	public void createLayout(JSObject layout) {

		LastActivityFactory lastActivityFactory = generateLastActivityFactory();
		JSArray ticktext = new JSArray();
		for (LastActivity lastActivity : lastActivityFactory.getAllLastActivity()) {
			ticktext.addWithQuote(lastActivity);
		}

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

	private Map<Color, String> getTraceNames(LastActivityFactory lastActivityFactory) {
		if (lastActivityFactory.getAllLastActivity()
				.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Color, String> map = new HashMap<>();

		LastActivity start = lastActivityFactory.getActivity(0);

		for (LastActivity lastActivity : lastActivityFactory.getAllLastActivity()) {
			if (!start.getColor()
					.equals(lastActivity.getColor())) {
				map.put(start.getColor(),
						String.format("[%d %s - %d %s)", start.getStartInclusive(), I18n.get(start.getChronoUnit()
								.toString()), lastActivity.getStartInclusive(), I18n.get(
										lastActivity.getChronoUnit()
												.toString())));
				start = lastActivity;
			}
		}
		map.put(start.getColor(), "+" + start.getStartInclusive() + " " + I18n.get(start.getChronoUnit()
				.toString()));
		return map;
	}

	private LastActivityFactory generateLastActivityFactory() {

		Color c25 = getConfigValue("firstInterval");
		Color c50 = getConfigValue("secondInterval");
		Color c75 = getConfigValue("thirdInterval");
		Color c100 = getConfigValue("fourthInterval");

		LastActivityFactory lastActivityFactory = new LastActivityFactory();

		lastActivityFactory.addActivity(0, 6, c25, ChronoUnit.HOURS);
		lastActivityFactory.addActivity(6, 12, c25, ChronoUnit.HOURS);
		lastActivityFactory.addActivity(12, 24, c25, ChronoUnit.HOURS);
		lastActivityFactory.addActivity(1, 2, c25, ChronoUnit.DAYS);
		lastActivityFactory.addActivity(2, 4, c50, ChronoUnit.DAYS);
		lastActivityFactory.addActivity(4, 8, c50, ChronoUnit.DAYS);
		lastActivityFactory.addActivity(8, 16, c75, ChronoUnit.DAYS);
		lastActivityFactory.addActivity(16, 32, c75, ChronoUnit.DAYS);
		lastActivityFactory.addActivity(32, Integer.MAX_VALUE, c100, ChronoUnit.DAYS);
		return lastActivityFactory;
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
						ChronoUnit.HOURS.between(enrolledUser.getLastcourseaccess(), updateTime),
						ChronoUnit.HOURS.between(enrolledUser.getLastaccess(), updateTime));

			}
		}

	}

}
