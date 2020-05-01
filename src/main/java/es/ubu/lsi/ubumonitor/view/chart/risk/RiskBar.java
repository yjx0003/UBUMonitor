package es.ubu.lsi.ubumonitor.view.chart.risk;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import es.ubu.lsi.ubumonitor.view.chart.Tabs;

public class RiskBar extends Chartjs {

	public RiskBar(MainController mainController) {
		super(mainController, ChartType.RISK_BAR, Tabs.RISK);
		useGeneralButton = false;
		useLegend = true;
		useGroupButton = false;

	}

	public RiskBar(MainController mainController, ChartType chartType, Tabs tab) {
		super(mainController, chartType, tab);
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

		JSObject jsObject = getDefaultOptions();
		jsObject.putWithQuote("typeGraph", "bar");
		JSObject callbacks = new JSObject();
		callbacks.put("title", "function(a,t){return t.datasets[a[0].datasetIndex].label}");
		callbacks.put("label", "function(e,t){return t.datasets[e.datasetIndex].users[e.index]}");
		jsObject.put("tooltips", "{callbacks:" + callbacks + "}");

		JSObject scales = new JSObject();

		scales.put("yAxes", "[{" + getYScaleLabel() + "}]");
		scales.put("xAxes", "[{" + getXScaleLabel() + "}]");
		jsObject.put("scales", scales);

		jsObject.put("plugins",
				"{datalabels:{display:!0,font:{weight:'bold'},formatter:function(t,a){if(0===t)return'';let e=a.chart.data.datasets[a.datasetIndex].data,r=0;for(i=0;i<e.length;i++)r+=e[i];return t+'/'+r+' ('+(t/r).toLocaleString(locale,{style:'percent',maximumFractionDigits:2})+')'}}}");

		jsObject.put("onClick",
				"function(t,e){let n=myChart.getElementsAtEventForMode(t,'nearest',{intersect:!0});if(n.length>0){let t=n[0],e=t._chart.config.data.datasets[t._datasetIndex].usersId[t._index];javaConnector.dataPointSelection(e[counter%e.length]),counter++}}");
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

	protected String createDataset(List<EnrolledUser> selectedEnrolledUser) {

		ZonedDateTime lastUpdate = Controller.getInstance()
				.getUpdateCourse();
		Map<LastActivity, List<EnrolledUser>> lastCourseAccess = new TreeMap<>(
				Comparator.comparing(LastActivity::getIndex));
		Map<LastActivity, List<EnrolledUser>> lastAccess = new TreeMap<>(Comparator.comparing(LastActivity::getIndex));
		for (EnrolledUser user : selectedEnrolledUser) {
			lastCourseAccess
					.computeIfAbsent(LastActivityFactory.getActivity(user.getLastcourseaccess(), lastUpdate),
							k -> new ArrayList<>())
					.add(user);
			lastAccess
					.computeIfAbsent(LastActivityFactory.getActivity(user.getLastaccess(), lastUpdate),
							k -> new ArrayList<>())
					.add(user);
		}

		List<LastActivity> lastActivities = LastActivityFactory.getAllLastActivity();

		JSObject data = new JSObject();
		JSArray labels = new JSArray();
		labels.addAllWithQuote(lastActivities);
		data.put("labels", labels);
		JSArray datasets = new JSArray();
		datasets.add(createDataset(I18n.get("label.lastcourseaccess"), lastCourseAccess, lastActivities, 0.2));
		datasets.add(createDataset(I18n.get("label.lastaccess"), lastAccess, lastActivities, 0.5));
		data.put("datasets", datasets);
		return data.toString();
	}

	public JSObject createDataset(String label, Map<LastActivity, List<EnrolledUser>> lastAccess,
			List<LastActivity> lastActivities, double opacity) {
		JSObject dataset = new JSObject();
		dataset.putWithQuote("label", label);
		JSArray backgroundColor = new JSArray();
		JSArray borderColor = new JSArray();
		JSArray datasetData = new JSArray();
		JSArray usersArray = new JSArray();
		JSArray usersIdArray = new JSArray();
		for (LastActivity lastActivity : lastActivities) {
			backgroundColor.add(colorToRGB(lastActivity.getColor(), opacity));
			borderColor.add(colorToRGB(lastActivity.getColor()));
			JSArray users = new JSArray();
			JSArray usersId = new JSArray();
			List<EnrolledUser> listUsers = lastAccess.computeIfAbsent(lastActivity, k -> Collections.emptyList());
			datasetData.add(listUsers.size());
			listUsers.forEach(e -> {
				usersId.add(e.getId());
				users.addWithQuote(e.getFullName());
			});
			usersArray.add(users);
			usersIdArray.add(usersId);

		}

		dataset.put("backgroundColor", backgroundColor);
		dataset.put("borderColor", borderColor);
		dataset.put("data", datasetData);
		dataset.put("users", usersArray);
		dataset.put("usersId", usersIdArray);
		return dataset;
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

	@Override
	public String getXAxisTitle() {

		return MessageFormat.format(super.getXAxisTitle(), Controller.getInstance()
				.getUpdateCourse()
				.format(Controller.DATE_TIME_FORMATTER));
	}
}
