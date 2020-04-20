package es.ubu.lsi.ubumonitor.view.chart.risk;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Chartjs;
import es.ubu.lsi.ubumonitor.view.chart.Tabs;

public class Bubble extends Chartjs {

	public Bubble(MainController mainController) {
		super(mainController, ChartType.BUBBLE, Tabs.RISK);
		useGeneralButton = false;
		useLegend = true;
		useGroupButton = false;
	}

	@Override
	public void exportCSV(String path) throws IOException {
		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader("userid", "fullname", "lastCourseAccess", "lastMoodleAcess"))) {
			for (EnrolledUser enrolledUser : getSelectedEnrolledUser()) {
				printer.printRecord(enrolledUser.getId(), enrolledUser.getFullName(),
						Controller.DATE_TIME_FORMATTER.format(enrolledUser.getLastcourseaccess()
								.atZone(ZoneId.systemDefault())),
						Controller.DATE_TIME_FORMATTER.format(enrolledUser.getLastaccess()
								.atZone(ZoneId.systemDefault())));

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

		callbacks.put("beforeTitle", String.format("function(e,t){return'%s'+(%d==e[0].xLabel?'>'+%d:e[0].xLabel)}", I18n.get("label.course"), limit, limit));
		callbacks.put("title", String.format("function(e,t){return'%s'+(%d==e[0].yLabel?'>'+%d:e[0].yLabel)}", I18n.get("text.moodle"), limit, limit));
		callbacks.put("label", "function(e,t){return t.datasets[e.datasetIndex].data[e.index].users}");
		jsObject.put("tooltips", "{callbacks:" + callbacks + "}");

		JSObject scales = new JSObject();

		JSObject ticks = new JSObject();
		ticks.put("min", 0);
		ticks.put("max", limit + 2);
		ticks.put("callback", "function(e,t,n){return " + limit + "==e?'>'+e:e>" + limit + "?'':e}");

		scales.put("yAxes", "[{" + getYScaleLabel() + ",ticks:" + ticks + "}]");
		scales.put("xAxes", "[{" + getXScaleLabel() + ",ticks:" + ticks + "}]");
		jsObject.put("scales", scales);
		//jsObject.put("onClick", "function(t,e){let a=myChart.getElementsAtEventForMode(t,'point',{intersect:!1});if(a.length>0){let t=a[0];t._chart.config.data.datasets[t._datasetIndex].data[t._index].usersId[counter%usersId.length]}}");
		return jsObject.toString();
	}

	@Override
	public void update() {
		String dataset = createDataset(getSelectedEnrolledUser());
		String options = getOptions();
		System.out.println(options);
		webViewChartsEngine.executeScript(String.format("updateChartjs(%s,%s)", dataset, options));

	}

	private String createDataset(List<EnrolledUser> selectedEnrolledUser) {
		int limit = Controller.getInstance()
				.getMainConfiguration()
				.getValue(this.chartType, "limitDays");
		Map<Long, Map<Long, List<EnrolledUser>>> lastAccess = createLastAccess(selectedEnrolledUser, limit);
		JSObject data = new JSObject();

		JSArray datasets = new JSArray();
		JSObject dataset = new JSObject();
		dataset.putWithQuote("label", "Selected Users");
		dataset.putWithQuote("backgroundColor", "rgba(207,52,118,0.2)");
		dataset.putWithQuote("borderColor", "rgba(207,52,118,1)");
		JSArray datasetData = new JSArray();
		for (Map.Entry<Long, Map<Long, List<EnrolledUser>>> entry : lastAccess.entrySet()) {
			long lastCourseAccess = entry.getKey();
			Map<Long, List<EnrolledUser>> map = entry.getValue();
			for (Map.Entry<Long, List<EnrolledUser>> entryUsers : map.entrySet()) {
				JSObject jsObject = new JSObject();
				jsObject.put("x", lastCourseAccess);
				jsObject.put("y", entryUsers.getKey());
				jsObject.put("r", entryUsers.getValue()
						.size() * 10);
				JSArray users = new JSArray();
				JSArray usersId = new JSArray();
				entryUsers.getValue()
						.forEach(u -> {
							users.addWithQuote(u.getFullName());
							usersId.add(u.getId());
						});
				jsObject.put("users", users);
				jsObject.put("usersId", usersId);
				datasetData.add(jsObject);
			}

		}
		dataset.put("data", datasetData);
		datasets.add(dataset);
		data.put("datasets", datasets);
		return data.toString();
	}

	public Map<Long, Map<Long, List<EnrolledUser>>> createLastAccess(List<EnrolledUser> selectedEnrolledUser,
			int limit) {
		ZonedDateTime lastLogTime = Controller.getInstance()
				.getActualCourse()
				.getLogs()
				.getLastDatetime();
		Map<Long, Map<Long, List<EnrolledUser>>> lastAccess = new HashMap<>();

		for (EnrolledUser user : selectedEnrolledUser) {
			long diffCourse = Math.min(Math.max(0L, ChronoUnit.DAYS.between(user.getLastcourseaccess(), lastLogTime)),
					limit);
			long diffServer = Math.min(Math.max(0, ChronoUnit.DAYS.between(user.getLastaccess(), lastLogTime)), limit);

			lastAccess.computeIfAbsent(diffCourse, k -> new HashMap<>())
					.computeIfAbsent(diffServer, k -> new ArrayList<>())
					.add(user);

		}
		return lastAccess;
	}
	@Override
	public int onClick(int userid) {
		EnrolledUser user = Controller.getInstance().getDataBase().getUsers().getById(userid);
		return listParticipants.getItems()
		.indexOf(user);
	}
}
