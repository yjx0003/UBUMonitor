package es.ubu.lsi.ubumonitor.view.chart.multi;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.SelectionController;
import es.ubu.lsi.ubumonitor.model.ActivityCompletion;
import es.ubu.lsi.ubumonitor.model.ActivityCompletion.State;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.model.log.TypeTimes;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.logs.TabulatorLogs;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;

public class RankingTable extends TabulatorLogs {

	private static final String POINTS = "points";
	private static final String RANKING = "ranking";
	private static final String USER = "user";
	private static final String GRADE_ITEM = "gradeItem";
	private static final String LOG = "log";
	private static final String ACTIVITY_COMPLETION = "activityCompletion";

	private TreeView<GradeItem> treeViewGradeItem;
	private ListView<CourseModule> listViewActivityCompletion;
	private DatePicker start;
	private DatePicker end;

	public RankingTable(MainController mainController, WebView webView, TreeView<GradeItem> treeViewGradeItem,
			ListView<CourseModule> listViewActivityCompletion, DatePicker start, DatePicker end) {
		super(mainController, ChartType.RANKING_TABLE, webView);
		this.treeViewGradeItem = treeViewGradeItem;
		this.listViewActivityCompletion = listViewActivityCompletion;
		this.start = start;
		this.end = end;
	}

	@Override
	public <E> String createData(List<E> typeLogs, DataSet<E> dataSet) {

		List<EnrolledUser> users = getSelectedEnrolledUser();
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		List<CourseModule> activities = new ArrayList<>(listViewActivityCompletion.getSelectionModel()
				.getSelectedItems());

		Map<EnrolledUser, Integer> pointsLog = getLogsRanking(users, typeLogs, dataSet, actualCourse.getLogStats()
				.getByType(TypeTimes.DAY), start.getValue(), end.getValue());
		Map<EnrolledUser, Integer> rankingLog = UtilMethods.ranking(pointsLog);

		Map<EnrolledUser, DescriptiveStatistics> pointsGrades = getGradeItemRanking(users, gradeItems);
		Map<EnrolledUser, Integer> rankingGrades = UtilMethods.ranking(pointsGrades, DescriptiveStatistics::getMean);

		Map<EnrolledUser, Integer> pointsActivities = getActivityCompletionRanking(users, activities, start.getValue()
				.atStartOfDay(ZoneId.systemDefault())
				.toInstant(),
				end.getValue()
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant());
		Map<EnrolledUser, Integer> rankingActivities = UtilMethods.ranking(pointsActivities);

		JSObject data = new JSObject();
		JSArray jsArray = new JSArray();
		data.put("tabledata", jsArray);

		data.put("columns", createColumns(rankingLog.values(), rankingGrades.values(), rankingActivities.values(),
				typeLogs.isEmpty(), gradeItems.isEmpty(), activities.isEmpty()));

		String rankingLogField = RANKING + LOG;
		String rankingGradeField = RANKING + GRADE_ITEM;
		String rankingActivityField = RANKING + ACTIVITY_COMPLETION;

		for (EnrolledUser user : users) {
			JSObject jsObject = new JSObject();
			jsObject.putWithQuote(USER, user.getFullName());
			jsObject.put(LOG, rankingLog.get(user));
			jsObject.put(GRADE_ITEM, rankingGrades.get(user));
			jsObject.put(ACTIVITY_COMPLETION, rankingActivities.get(user));

			jsObject.put(rankingLogField, pointsLog.get(user));
			jsObject.put(rankingGradeField, Math.floor(pointsGrades.get(user)
					.getMean() * 100) / 100);
			jsObject.put(rankingActivityField, pointsActivities.get(user));

			jsArray.add(jsObject);
		}

		return data.toString();
	}

	private JSArray createColumns(Collection<Integer> pointLogs, Collection<Integer> pointGradeItem,
			Collection<Integer> pointActivities, boolean logs, boolean gradeItems, boolean activities) {

		JSArray jsArray = new JSArray();
		JSObject jsObject = new JSObject();
		jsObject.putWithQuote("title", I18n.get("text.selectedUsers"));
		jsObject.putWithQuote("field", USER);
		jsObject.put("tooltip", true);
		jsObject.put("sorter", "'string'");
		jsArray.add(jsObject);

		SelectionController selectionController = mainController.getSelectionController();
		jsArray.add(createProgressColumn(selectionController.getTabUbuLogs()
				.getText() + " - "
				+ selectionController.getTabPaneUbuLogs()
						.getSelectionModel()
						.getSelectedItem()
						.getText(),
				LOG, pointLogs, logs));
		jsArray.add(createProgressColumn(selectionController.getTabUbuGrades()
				.getText(), GRADE_ITEM, pointGradeItem, gradeItems));
		jsArray.add(createProgressColumn(selectionController.getTabActivity()
				.getText(), ACTIVITY_COMPLETION, pointActivities, activities));
		return jsArray;

	}

	private JSObject createProgressColumn(String title, String field, Collection<Integer> ranks, boolean isEmpty) {
		JSObject jsObject = new JSObject();
		jsObject.putWithQuote("title", title);
		jsObject.putWithQuote("field", field);
		int max = ranks.isEmpty() ? 0 : Collections.max(ranks);
		if (!isEmpty) {

			jsObject.put("formatter", "'progress'");
			JSObject formatterParams = new JSObject();
			jsObject.put("tooltip", "function(cell){return cell.getRow().getData().ranking" + field + "}");
			jsObject.put("formatterParams", formatterParams);
			formatterParams.put("min", 0);
			formatterParams.put("max", max);
			formatterParams.put("legend", "function(v) {return v + '/' + " + max + "}");

			JSArray color = new JSArray();

			color.add(colorToRGB(getConfigValue("firstInterval")));
			color.add(colorToRGB(getConfigValue("secondInterval")));
			color.add(colorToRGB(getConfigValue("thirdInterval")));
			color.add(colorToRGB(getConfigValue("fourthInterval")));
			formatterParams.put("color", color);

		} else {
			jsObject.put("formatter", "function(cell, formatterParams, onRendered){return 'N/A';}");
			jsObject.put("headerSort", false);
			jsObject.put("hozAlign", "'center'");
		}

		return jsObject;
	}

	private <E> Map<EnrolledUser, Integer> getLogsRanking(List<EnrolledUser> users, List<E> typeLogs,
			DataSet<E> dataSet, GroupByAbstract<?> groupBy, LocalDate start, LocalDate end) {
		Map<EnrolledUser, Map<E, List<Integer>>> counts = dataSet.getUserCounts(groupBy, users, typeLogs, start, end);
		Map<EnrolledUser, Integer> map = new HashMap<>();
		for (EnrolledUser user : users) {
			int userCount = counts.getOrDefault(user, Collections.emptyMap())
					.entrySet()
					.stream()
					.flatMap(e -> e.getValue()
							.stream())
					.mapToInt(Integer::intValue)
					.sum();
			map.put(user, userCount);
		}
		return map;
	}

	private Map<EnrolledUser, DescriptiveStatistics> getGradeItemRanking(Collection<EnrolledUser> users,
			Collection<GradeItem> gradeItems) {
		Map<EnrolledUser, DescriptiveStatistics> map = new HashMap<>();
		for (EnrolledUser user : users) {
			DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
			map.put(user, descriptiveStatistics);
			for (GradeItem gradeItem : gradeItems) {
				double grade = gradeItem.getEnrolledUserPercentage(user);
				descriptiveStatistics.addValue(Double.isNaN(grade) ? 0.0 : grade / 10);
			}
		}
		return map;
	}

	private Map<EnrolledUser, Integer> getActivityCompletionRanking(Collection<EnrolledUser> users,
			Collection<CourseModule> activities, Instant start, Instant end) {
		Map<EnrolledUser, Integer> map = new HashMap<>();
		for (EnrolledUser user : users) {
			int counter = 0;
			for (CourseModule activity : activities) {
				ActivityCompletion activityCompletion = activity.getActivitiesCompletion()
						.get(user);
				if (activityCompletion != null
						&& (activityCompletion.getState() == State.COMPLETE
								|| activityCompletion.getState() == State.COMPLETE_PASS)
						&& start.isBefore(activityCompletion.getTimecompleted())
						&& end.isAfter(activityCompletion.getTimecompleted())) {
					++counter;
				}
			}
			map.put(user, counter);
		}

		return map;
	}

	@Override
	protected <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		List<EnrolledUser> users = getSelectedEnrolledUser();
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		List<CourseModule> activities = new ArrayList<>(listViewActivityCompletion.getSelectionModel()
				.getSelectedItems());

		Map<EnrolledUser, Integer> pointsLog = getLogsRanking(users, typeLogs, dataSet, actualCourse.getLogStats()
				.getByType(TypeTimes.DAY), start.getValue(), end.getValue());
		Map<EnrolledUser, Integer> rankingLog = UtilMethods.ranking(pointsLog);

		Map<EnrolledUser, DescriptiveStatistics> pointsGrades = getGradeItemRanking(users, gradeItems);
		Map<EnrolledUser, Integer> rankingGrades = UtilMethods.ranking(pointsGrades, DescriptiveStatistics::getMean);

		Map<EnrolledUser, Integer> pointsActivities = getActivityCompletionRanking(users, activities, start.getValue()
				.atStartOfDay(ZoneId.systemDefault())
				.toInstant(),
				end.getValue()
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant());
		Map<EnrolledUser, Integer> rankingActivities = UtilMethods.ranking(pointsActivities);

		for (EnrolledUser user : users) {
			printer.print(user.getId());
			printer.print(user.getFullName());
			printer.print(rankingLog.get(user));
			printer.print(pointsLog.get(user));
			printer.print(rankingGrades.get(user));
			printer.print(pointsGrades.get(user)
					.getMean());
			printer.print(rankingActivities.get(user));
			printer.print(pointsActivities.get(user));
			printer.println();

		}
	}

	@Override
	protected String[] getCSVHeader() {
		return new String[] { "id", "fullName", RANKING + LOG, POINTS + LOG, RANKING + GRADE_ITEM, POINTS + GRADE_ITEM,
				RANKING + ACTIVITY_COMPLETION, POINTS + ACTIVITY_COMPLETION };
	}

	@Override
	protected <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		// no desglosed at the moment

	}

	@Override
	protected String[] getCSVDesglosedHeader() {
		// no desglosed at the moment
		return ArrayUtils.EMPTY_STRING_ARRAY;
	}

	@Override
	public JSObject getOptions(JSObject jsObject) {

		jsObject.put("invalidOptionWarnings", false);
		jsObject.put("height", "height");
		jsObject.put("tooltipsHeader", true);
		jsObject.put("virtualDom", true);
		jsObject.putWithQuote("layout", "fitColumns");
		jsObject.put("rowClick", "function(e,row){javaConnector.dataPointSelection(row.getPosition());}");
		return jsObject;
	}

}
