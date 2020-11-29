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
import es.ubu.lsi.ubumonitor.view.chart.Plotly;

public class RiskBar extends Plotly {

	public RiskBar(MainController mainController) {
		this(mainController, ChartType.RISK_BAR);

	}

	public RiskBar(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
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
		ZonedDateTime lastUpdate = Controller.getInstance()
				.getUpdatedCourseData();
		Map<LastActivity, List<EnrolledUser>> lastCourseAccess = new TreeMap<>(
				Comparator.comparing(LastActivity::getIndex));
		Map<LastActivity, List<EnrolledUser>> lastAccess = new TreeMap<>(Comparator.comparing(LastActivity::getIndex));
		for (EnrolledUser user : selectedEnrolledUser) {
			lastCourseAccess
					.computeIfAbsent(LastActivityFactory.DEFAULT.getActivity(user.getLastcourseaccess(), lastUpdate),
							k -> new ArrayList<>())
					.add(user);
			lastAccess
					.computeIfAbsent(LastActivityFactory.DEFAULT.getActivity(user.getLastaccess(), lastUpdate),
							k -> new ArrayList<>())
					.add(user);
		}

		List<LastActivity> lastActivities = LastActivityFactory.DEFAULT.getAllLastActivity();

		data.add(createTrace(I18n.get("label.lastcourseaccess"), selectedEnrolledUser.size(), lastCourseAccess,
				lastActivities, 0.2));
		data.add(createTrace(I18n.get("label.lastaccess"), selectedEnrolledUser.size(), lastAccess, lastActivities,
				0.5));

	}

	public JSObject createTrace(String name, int nUsers, Map<LastActivity, List<EnrolledUser>> lastAccess,
			List<LastActivity> lastActivities, double opacity) {
		JSObject trace = new JSObject();

		JSArray color = new JSArray();

		JSArray y = new JSArray();
		JSArray x = new JSArray();
		x.addAllWithQuote(lastActivities);
		JSArray usersArray = new JSArray();
		JSArray usersIdArray = new JSArray();
		JSArray text = new JSArray();
		for (LastActivity lastActivity : lastActivities) {
			color.add(colorToRGB(lastActivity.getColor(), opacity));

			StringBuilder users = new StringBuilder();
			JSArray usersId = new JSArray();
			List<EnrolledUser> listUsers = lastAccess.computeIfAbsent(lastActivity, k -> Collections.emptyList());
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

	@Override
	public String getXAxisTitle() {

		return MessageFormat.format(super.getXAxisTitle(), Controller.getInstance()
				.getUpdatedCourseData()
				.format(Controller.DATE_TIME_FORMATTER));
	}

}
