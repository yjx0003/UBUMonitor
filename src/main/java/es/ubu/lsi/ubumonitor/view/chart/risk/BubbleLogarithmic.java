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
import es.ubu.lsi.ubumonitor.view.chart.Chartjs;
import javafx.scene.paint.Color;

public class BubbleLogarithmic extends Chartjs {

	public BubbleLogarithmic(MainController mainController) {
		super(mainController, ChartType.BUBBLE_LOGARITHMIC);
		useGeneralButton = false;
		useLegend = true;
		useGroupButton = false;
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

	@Override
	public JSObject getOptions(JSObject jsObject) {
		jsObject.putWithQuote("typeGraph", "bubble");
		JSObject callbacks = new JSObject();

		callbacks.put("beforeTitle",
				String.format("function(e,t){return'%s'+bubbleLabels[t.datasets[e[0].datasetIndex].data[e[0].index].x]}",
						I18n.get("label.course")));
		callbacks.put("title",
				String.format("function(e,t){return'%s'+bubbleLabels[t.datasets[e[0].datasetIndex].data[e[0].index].y]}",
						I18n.get("text.moodle")));
		callbacks.put("label", "function(e,t){return t.datasets[e.datasetIndex].data[e.index].users}");
		jsObject.put("tooltips", "{callbacks:" + callbacks + "}");

		JSObject scales = new JSObject();

		JSObject ticks = new JSObject();
		ticks.put("min", 0);
		ticks.put("max", 8);
		ticks.put("callback", "function(e,t,n){return bubbleLabels[e]}");

		scales.put("xAxes", "[{" + getXScaleLabel() + ",ticks:" + ticks + "}]");
		ticks.put("callback", "function(e,t,n){return bubbleLabels[e]}");

		scales.put("yAxes", "[{" + getYScaleLabel() + ",ticks:" + ticks + "}]");
		jsObject.put("scales", scales);
		jsObject.put("layout", "{padding:{right:50,left:50,top:50,bottom:50}}");
		jsObject.put("plugins",
				"{datalabels:{display:true,font:{style:'italic',weight:'bold'},formatter:function(a,t){return a.users.length}}}");

		jsObject.put("onClick",
				"function(t,e){let n=myChart.getElementsAtEventForMode(t,'nearest',{intersect:!0});if(n.length>0){let t=n[0],e=t._chart.config.data.datasets[t._datasetIndex].data[t._index].usersId;javaConnector.dataPointSelection(e[counter%e.length]),counter++}}");

		jsObject.put("elements",
				"{point:{radius:function(a){var t=a.dataset.data[a.dataIndex];return a.chart.width/24*t.v/100+5}}}");
		return jsObject;
	}

	@Override
	public void update() {
		String dataset = createDataset(getSelectedEnrolledUser());
		JSObject options = getOptions();
		webViewChartsEngine.executeScript(String.format("updateChartjs(%s,%s)", dataset, options));
	}

	private String createDataset(List<EnrolledUser> selectedEnrolledUser) {

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

		Map<Color, JSArray> datasetMap = new LinkedHashMap<>();
		for (Map.Entry<LastActivity, Map<LastActivity, List<EnrolledUser>>> entry : lastActivities.entrySet()) {
			LastActivity lastActivityCourse = entry.getKey();
			for (Map.Entry<LastActivity, List<EnrolledUser>> entry2 : entry.getValue()
					.entrySet()) {
				LastActivity lastActivityMoodle = entry2.getKey();
				List<EnrolledUser> userList = entry2.getValue();
				JSArray dataArray = datasetMap.computeIfAbsent(lastActivityCourse.getColor(), k -> new JSArray());

				dataArray.add(createDataset(lastActivityCourse, lastActivityMoodle, userList));
			}
		}
		Map<Color, String> labels = getLabels(lastActivityFactory);
		JSArray datasets = new JSArray();

		for (Map.Entry<Color, JSArray> entry : datasetMap.entrySet()) {
			Color color = entry.getKey();
			JSArray dataArray = entry.getValue();
			JSObject dataset = new JSObject();

			dataset.putWithQuote("label", labels.get(color));
			dataset.put("backgroundColor", colorToRGB(color, 0.2));
			dataset.put("borderColor", colorToRGB(color));
			dataset.put("data", dataArray);
			datasets.add(dataset);
		}
		JSObject data = new JSObject();
		data.put("datasets", datasets);
		JSArray labelsArray = new JSArray();
		for (LastActivity lastActivity : lastActivityFactory.getAllLastActivity()) {
			labelsArray.addWithQuote(lastActivity.toString());
		}

		webViewChartsEngine.executeScript("var bubbleLabels=" + labelsArray+";");
		return data.toString();
	}

	public JSObject createDataset(LastActivity lastActivityCourse, LastActivity lastActivityMoodle,
			List<EnrolledUser> userList) {
		JSObject dataObject = new JSObject();
		dataObject.put("x", lastActivityCourse.getIndex());
		dataObject.put("y", lastActivityMoodle.getIndex());
		dataObject.put("v", userList.size() * 5);
		JSArray users = new JSArray();
		JSArray usersId = new JSArray();
		userList.forEach(u -> {
			users.addWithQuote(u.getFullName());
			usersId.add(u.getId());
		});
		dataObject.put("users", users);
		dataObject.put("usersId", usersId);
		return dataObject;
	}

	private Map<Color, String> getLabels(LastActivityFactory lastActivityFactory) {
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
	
		Color c25 = mainConfiguration.getValue(chartType, "firstInterval");
		Color c50 = mainConfiguration.getValue(chartType, "secondInterval");
		Color c75 = mainConfiguration.getValue(chartType, "thirdInterval");
		Color c100 = mainConfiguration.getValue(chartType, "fourthInterval");

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
	public int onClick(int userid) {
		EnrolledUser user = Controller.getInstance()
				.getDataBase()
				.getUsers()
				.getById(userid);
		return getUsers()
				.indexOf(user);
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
}
