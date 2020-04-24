package es.ubu.lsi.ubumonitor.view.chart.risk;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Chartjs;
import es.ubu.lsi.ubumonitor.view.chart.Tabs;
import javafx.scene.paint.Color;

public class Bubble extends Chartjs {

	public Bubble(MainController mainController) {
		super(mainController, ChartType.BUBBLE, Tabs.RISK);
		useGeneralButton = false;
		useLegend = true;
		useGroupButton = false;
	}

	@Override
	public void exportCSV(String path) throws IOException {
		ZonedDateTime updateTime = Controller.getInstance()
				.getUpdateCourse();
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

	@Override
	public String getOptions() {

		int limit = Controller.getInstance()
				.getMainConfiguration()
				.getValue(this.chartType, "limitDays");

		JSObject jsObject = getDefaultOptions();
		jsObject.putWithQuote("typeGraph", "bubble");
		JSObject callbacks = new JSObject();

		callbacks.put("beforeTitle", String.format("function(e,t){return'%s'+(%d==e[0].xLabel?'>'+%d:e[0].xLabel)}",
				I18n.get("label.course"), limit, limit));
		callbacks.put("title", String.format("function(e,t){return'%s'+(%d==e[0].yLabel?'>'+%d:e[0].yLabel)}",
				I18n.get("text.moodle"), limit, limit));
		callbacks.put("label", "function(e,t){return t.datasets[e.datasetIndex].data[e.index].users}");
		jsObject.put("tooltips", "{callbacks:" + callbacks + "}");

		JSObject scales = new JSObject();

		JSObject ticks = new JSObject();
		ticks.put("min", 0);
		ticks.put("max", limit);
		ticks.put("callback", "function(e,t,n){return " + limit + "==e?'>'+e:e}");

		scales.put("yAxes", "[{" + getYScaleLabel() + ",ticks:" + ticks + "}]");
		scales.put("xAxes", "[{" + getXScaleLabel() + ",ticks:" + ticks + "}]");
		jsObject.put("scales", scales);
		jsObject.put("layout", "{padding:{right:50,left:50,top:50,bottom:50}}");
		jsObject.put("plugins",
				"{datalabels:{display:true,font:{style:'italic',weight:'bold'},formatter:function(a,t){return a.users.length}}}");

		jsObject.put("onClick",
				"function(t,e){let n=myChart.getElementsAtEventForMode(t,'nearest',{intersect:!0});if(n.length>0){let t=n[0],e=t._chart.config.data.datasets[t._datasetIndex].data[t._index].usersId;javaConnector.dataPointSelection(e[counter%e.length]),counter++}}");
		return jsObject.toString();
	}

	@Override
	public void update() {
		String dataset = createDataset(getSelectedEnrolledUser());
		String options = getOptions();
		System.out.println(dataset);
		System.out.println(options);
		webViewChartsEngine.executeScript(String.format("updateChartjs(%s,%s)", dataset, options));

	}

	private String createDataset(List<EnrolledUser> selectedEnrolledUser) {
		MainConfiguration mainConfiguration = Controller.getInstance()
				.getMainConfiguration();
		int limit = Controller.getInstance()
				.getMainConfiguration()
				.getValue(this.chartType, "limitDays");
		Map<Long, Map<Long, List<EnrolledUser>>> lastAccess = createLastAccess(selectedEnrolledUser, limit);
		JSObject data = new JSObject();

		JSArray datasets = new JSArray();

		long p25 = Math.round(0.25 * limit);
		long p50 = Math.round(0.50 * limit);
		long p75 = Math.round(0.75 * limit);
		Color c25 = mainConfiguration.getValue(chartType, "firstInterval");
		Color c50 = mainConfiguration.getValue(chartType, "secondInterval");
		Color c75 = mainConfiguration.getValue(chartType, "thirdInterval");
		Color c100 = mainConfiguration.getValue(chartType, "fourthInterval");

		for (Map.Entry<Long, Map<Long, List<EnrolledUser>>> entry : lastAccess.entrySet()) {
			long x = entry.getKey();
			Map<Long, List<EnrolledUser>> map = entry.getValue();
			for (Map.Entry<Long, List<EnrolledUser>> entryUsers : map.entrySet()) {
				long y = entryUsers.getKey();
				JSObject dataset = new JSObject();

				Color color;
				if (x < p25) {
					color = c25;
				} else if (x < p50) {
					color = c50;
				} else if (x < p75) {
					color = c75;
				} else {
					color = c100;
				}
				dataset.putWithQuote("label", (x == limit ? ">" + x : x) + "," + (y == limit ? ">" + y : y));
				dataset.put("backgroundColor", colorToRGB(color, 0.2));
				dataset.put("borderColor", colorToRGB(color));

				JSObject jsObject = new JSObject();
				jsObject.put("x", x);
				jsObject.put("y", y);
				jsObject.put("r", entryUsers.getValue()
						.size() * 5);
				JSArray users = new JSArray();
				JSArray usersId = new JSArray();
				entryUsers.getValue()
						.forEach(u -> {
							users.addWithQuote(u.getFullName());
							usersId.add(u.getId());
						});
				jsObject.put("users", users);
				jsObject.put("usersId", usersId);
				dataset.put("data", "[" + jsObject + "]");
				datasets.add(dataset);
			}

		}

		data.put("datasets", datasets);
		return data.toString();
	}

	public Map<Long, Map<Long, List<EnrolledUser>>> createLastAccess(List<EnrolledUser> selectedEnrolledUser,
			int limit) {
		ZonedDateTime lastLogTime = Controller.getInstance()
				.getUpdateCourse();
		Map<Long, Map<Long, List<EnrolledUser>>> lastAccess = new TreeMap<>();

		for (EnrolledUser user : selectedEnrolledUser) {
			long diffCourse = Math.min(Math.max(0L, ChronoUnit.DAYS.between(user.getLastcourseaccess(), lastLogTime)),
					limit);
			long diffServer = Math.min(Math.max(0, ChronoUnit.DAYS.between(user.getLastaccess(), lastLogTime)), limit);

			lastAccess.computeIfAbsent(diffCourse, k -> new TreeMap<>())
					.computeIfAbsent(diffServer, k -> new ArrayList<>())
					.add(user);

		}
		return lastAccess;
	}

	@Override
	public int onClick(int userid) {
		EnrolledUser user = Controller.getInstance()
				.getDataBase()
				.getUsers()
				.getById(userid);
		return listParticipants.getItems()
				.indexOf(user);
	}
}
