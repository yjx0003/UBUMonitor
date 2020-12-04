package es.ubu.lsi.ubumonitor.view.chart.multi;

import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.logs.PlotlyLog;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeView;

public class BubbleComparison extends PlotlyLog {

	private static final List<String> SYMBOLS = Arrays.asList("circle", "square", "diamond", "cross", "x", "pentagon",
			"star", "hexagram");
	private TreeView<GradeItem> treeViewGradeItem;
	private ListView<CourseModule> listViewActivityCompletion;
	private ChoiceBox<GroupByAbstract<?>> groupBy;

	public BubbleComparison(MainController mainController, TreeView<GradeItem> treeViewGradeItem,
			ListView<CourseModule> listViewActivityCompletion, ChoiceBox<GroupByAbstract<?>> groupBy, DatePicker start,
			DatePicker end) {
		super(mainController, ChartType.BUBBLE_COMPARISON);
		this.treeViewGradeItem = treeViewGradeItem;
		this.listViewActivityCompletion = listViewActivityCompletion;
		this.groupBy = groupBy;
		this.datePickerStart = start;
		this.datePickerEnd = end;
		useGroupBy = true;
		useRangeDate = true;
		useLegend = true;
	}

	public void createData(JSArray data, JSArray frames, List<EnrolledUser> users, List<GradeItem> gradeItems) {
		JSObject frame = (JSObject) frames.get(0);
		Map<EnrolledUser, DescriptiveStatistics> userGradeItemPoints = RankingTable.getGradeItemPoints(users,
				gradeItems);
		double sizeref = 2 * userGradeItemPoints.values()
				.stream()
				.mapToDouble((DescriptiveStatistics::getMean))
				.max()
				.orElse(10) / 10000;
		boolean useOtherFigures = !(boolean) getConfigValue("useCircles");
		for (int i = 0; i < users.size(); ++i) {

			EnrolledUser user = users.get(i);

			double grade = Double.isNaN(userGradeItemPoints.get(user)
					.getMean()) ? 20.0
							: userGradeItemPoints.get(user)
									.getMean() * 5 + 20;

			JSObject dataObject = new JSObject();
			dataObject.put("mode", "'markers'");
			dataObject.putWithQuote("name", user.getFullName());
			JSArray frameDataArray = (JSArray) frame.get("data");
			JSObject frameData = (JSObject) frameDataArray.get(i);
			dataObject.put("x", frameData.get("x"));
			dataObject.put("y", frameData.get("y"));
			dataObject.put("grade", userGradeItemPoints.get(user)
					.getMean());
			dataObject.put("userids", "[" + user.getId() + "]");
			JSObject marker = new JSObject();
			marker.put("opacity", 0.8);
			marker.put("sizemode", "'area'");
			marker.put("sizeref", sizeref);
			marker.put("size", grade);
			marker.put("line", "{width:1.5,color:'black'}");
			if (useOtherFigures) {
				marker.putWithQuote("symbol", SYMBOLS.get(user.getId() % SYMBOLS.size()));
			}
			dataObject.put("marker", marker);
			dataObject.putWithQuote("hovertemplate", I18n.get("hovertemplateBubble"));
			data.add(dataObject);

		}
	}

	@Override
	public <E> String createData(List<E> typeLogs, DataSet<E> dataSet) {
		JSObject plot = new JSObject();
		JSArray data = new JSArray();
		JSObject layout = new JSObject();
		JSArray frames = new JSArray();

		List<EnrolledUser> users = getSelectedEnrolledUser();
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		List<CourseModule> activities = new ArrayList<>(listViewActivityCompletion.getSelectionModel()
				.getSelectedItems());

		Number maxLog = createFrames(frames, users, typeLogs, dataSet, activities, groupBy.getValue(),
				datePickerStart.getValue(), datePickerEnd.getValue());

		createData(data, frames, users, gradeItems);
		createLayout(layout, maxLog, activities.size(), groupBy.getValue()
				.getRangeString(datePickerStart.getValue(), datePickerEnd.getValue()),
				I18n.get(groupBy.getValue()
						.getTypeTime()));
		plot.put("data", data);
		plot.put("layout", layout);
		plot.put("frames", frames);

		return plot.toString();
	}

	private <T extends Serializable, E> Number createFrames(JSArray frames, List<EnrolledUser> users, List<E> typeLogs,
			DataSet<E> dataSet, Collection<CourseModule> activities, GroupByAbstract<T> group, LocalDate start,
			LocalDate end) {
		List<T> range = group.getRange(start, end);
		List<String> rangeString = group.getRangeString(range);
		Instant instantStart = group.getStartLocalDate(start)
				.atStartOfDay(ZoneId.systemDefault())
				.toInstant();
		Map<EnrolledUser, Integer> pointsLog = Collections.emptyMap();

		for (int i = 0; i < range.size(); i++) {

			T time = range.get(i);
			String timeString = rangeString.get(i);
			LocalDate endLocalDate = group.getEndLocalDate(time);
			pointsLog = RankingTable.getLogsPoints(users, typeLogs, dataSet, group, start, endLocalDate);
			Map<EnrolledUser, Integer> pointsActivities = RankingTable.getActivityCompletionPoints(users, activities,
					instantStart, endLocalDate.plusDays(1)
							.atStartOfDay(ZoneId.systemDefault())
							.toInstant());
			JSObject frame = new JSObject();
			frames.add(frame);
			frame.putWithQuote("name", timeString);
			JSArray dataArray = new JSArray();
			frame.put("data", dataArray);

			for (EnrolledUser user : users) {
				JSObject data = new JSObject();
				data.put("x", "[" + pointsLog.get(user) + "]");
				data.put("y", "[" + pointsActivities.get(user) + "]");
				dataArray.add(data);
			}

		}
		return UtilMethods.getMax(pointsLog.values());

	}

	private void createLayout(JSObject layout, Number maxLog, Number activityCompletionMax,
			Collection<String> timeTypes, String prefix) {

		JSObject xaxis = new JSObject();
		xaxis.putWithQuote("title", getXAxisTitle());
		xaxis.put("range", "[" + maxLog.intValue() / -10 + "," + maxLog + "]");
		layout.put("xaxis", xaxis);

		JSObject yaxis = new JSObject();
		yaxis.putWithQuote("title", getYAxisTitle());
		yaxis.put("range", "[" + activityCompletionMax.intValue() / -5 + "," + activityCompletionMax + "]");
		layout.put("yaxis", yaxis);
		layout.put("hovermode", "'closest'");

		JSObject updateMenus = new JSObject();
		updateMenus.put("x", 0);
		updateMenus.put("y", 0);
		updateMenus.put("yanchor", "'top'");
		updateMenus.put("xanchor", "'left'");
		updateMenus.put("showactive", false);
		updateMenus.put("direction", "'left'");
		updateMenus.put("type", "'buttons'");
		updateMenus.put("pad", "{t:85,r:10}");

		JSObject playButton = new JSObject();
		playButton.put("method", "'animate'");
		playButton.put("args",
				"[null,{mode:'immediate',fromcurrent:!0,transition:{duration:" + getConfigValue("transitionDuration")
						+ "},frame:{duration:" + getConfigValue("frameDuration") + ",redraw:!1}}]");
		playButton.put("label", "'▶'");

		JSObject stopButton = new JSObject();
		stopButton.put("method", "'animate'");
		stopButton.put("args", "[[null],{mode:'immediate',transition:{duration:0},frame:{duration:0,redraw:!1}}]");
		stopButton.put("label", "'⏸'");

		updateMenus.put("buttons", "[" + playButton + "," + stopButton + "]");
		layout.put("updatemenus", "[" + updateMenus + "]");
		JSArray sliderSteps = new JSArray();
		for (String timeType : timeTypes) {
			String scapedTime = UtilMethods.escapeJavaScriptText(timeType);
			JSObject step = new JSObject();
			step.put("method", "'animate'");
			step.put("label", "'" + scapedTime + "'");
			step.put("args",
					"[['" + scapedTime + "'],{mode:'immediate',transition:{duration:"
							+ getConfigValue("transitionDuration") + "},frame:{duration:"
							+ getConfigValue("frameDuration") + ",redraw:!1}}]");
			sliderSteps.add(step);

		}

		layout.put("sliders", "[{pad:{l:100,t:55},currentvalue:{visible:true,prefix:'" + prefix
				+ ":',xanchor:'right',font:{size:20,color:'#666'}},steps:" + sliderSteps + "}]");

	}

	@Override
	protected <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {

		exportCSV(printer, dataSet, typeLogs, groupBy.getValue());

	}

	private <T extends Serializable, E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs,
			GroupByAbstract<T> group) throws IOException {
		List<EnrolledUser> users = getSelectedEnrolledUser();
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		List<CourseModule> activities = new ArrayList<>(listViewActivityCompletion.getSelectionModel()
				.getSelectedItems());
		LocalDate start = datePickerStart.getValue();
		LocalDate end = datePickerEnd.getValue();
		Instant instantStart = group.getStartLocalDate(start)
				.atStartOfDay(ZoneId.systemDefault())
				.toInstant();
		Map<EnrolledUser, DescriptiveStatistics> pointsGrades = RankingTable.getGradeItemPoints(users, gradeItems);
		List<T> range = group.getRange(start, end);
		List<String> rangeString = group.getRangeString(range);

		for (int i = 0; i < range.size(); ++i) {

			LocalDate endLocalDate = group.getEndLocalDate(range.get(i));
			Map<EnrolledUser, Integer> pointsLog = RankingTable.getLogsPoints(users, typeLogs, dataSet, group, start,
					endLocalDate);
			Map<EnrolledUser, Integer> pointsActivities = RankingTable.getActivityCompletionPoints(users, activities,
					instantStart, endLocalDate.plusDays(1)
							.atStartOfDay(ZoneId.systemDefault())
							.toInstant());

			for (EnrolledUser user : users) {
				printer.print(user.getId());
				printer.print(user.getFullName());
				printer.print(rangeString.get(i));
				printer.print(pointsLog.get(user));
				printer.print(pointsGrades.get(user)
						.getMean());
				printer.print(pointsActivities.get(user));
				printer.println();

			}
		}

	}

	@Override
	protected String[] getCSVHeader() {
		return new String[] { "userid", "username", "time", "pointsLog", "pointsGrades", "pointsActivityCompletion" };
	}

	@Override
	protected <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		// nothing at the moment

	}

	@Override
	protected String[] getCSVDesglosedHeader() {
		return new String[0];
	}

	@Override
	public <E> JSArray createData(List<E> typeLogs, DataSet<E> dataSet, List<EnrolledUser> selectedUsers,
			LocalDate dateStart, LocalDate dateEnd, GroupByAbstract<?> groupBy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E> JSObject createLayout(List<E> typeLogs, DataSet<E> dataSet, LocalDate dateStart, LocalDate dateEnd,
			GroupByAbstract<?> groupBy) {
		// TODO Auto-generated method stub
		return null;
	}

}
