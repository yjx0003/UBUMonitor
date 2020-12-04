package es.ubu.lsi.ubumonitor.view.chart.multi;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
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

public class PointsTable extends TabulatorLogs {

	private static final String POINTS = "points";
	private static final String RANKING = "ranking";
	private static final String USER = "user";
	private static final String GRADE_ITEM = "gradeItem";
	private static final String LOG = "log";
	private static final String ACTIVITY_COMPLETION = "activityCompletion";

	private TreeView<GradeItem> treeViewGradeItem;
	private ListView<CourseModule> listViewActivityCompletion;

	public PointsTable(MainController mainController, TreeView<GradeItem> treeViewGradeItem,
			ListView<CourseModule> listViewActivityCompletion, DatePicker start, DatePicker end) {

		super(mainController, ChartType.POINTS_TABLE);
		this.treeViewGradeItem = treeViewGradeItem;
		this.listViewActivityCompletion = listViewActivityCompletion;
		this.datePickerStart = start;
		this.datePickerEnd = end;
		useRangeDate = true;
	}

	@Override
	public <E> String createData(List<E> typeLogs, DataSet<E> dataSet) {

		List<EnrolledUser> users = getSelectedEnrolledUser();
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		List<CourseModule> activities = new ArrayList<>(listViewActivityCompletion.getSelectionModel()
				.getSelectedItems());

		Map<EnrolledUser, Integer> pointsLog = getLogsPoints(users, typeLogs, dataSet, actualCourse.getLogStats()
				.getByType(TypeTimes.DAY), datePickerStart.getValue(), datePickerEnd.getValue());

		Map<EnrolledUser, DescriptiveStatistics> pointsGrades = getGradeItemPoints(users, gradeItems);

		Map<EnrolledUser, Integer> pointsActivities = getActivityCompletionPoints(users, activities,
				datePickerStart.getValue()
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant(),
				datePickerEnd.getValue()
						.plusDays(1)
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant());

		JSObject data = new JSObject();
		JSArray jsArray = new JSArray();
		data.put("tabledata", jsArray);
		if ((boolean) getConfigValue("statisticsRanking")) {
			Map<EnrolledUser, Double> rankingLog = UtilMethods.rankingStatistics(pointsLog);
			Map<EnrolledUser, Double> rankingGrades = UtilMethods.rankingStatistics(pointsGrades,
					DescriptiveStatistics::getMean);
			Map<EnrolledUser, Double> rankingActivities = UtilMethods.rankingStatistics(pointsActivities);
			data.put("columns", createColumns(users.size(), UtilMethods.getMax(pointsLog.values()), 10,
					activities.size(), typeLogs, gradeItems, activities));
			append(users, pointsLog, pointsGrades, pointsActivities, rankingLog, rankingGrades, rankingActivities,
					jsArray);
		} else {
			Map<EnrolledUser, Integer> rankingLog = UtilMethods.ranking(pointsLog);
			Map<EnrolledUser, Integer> rankingGrades = UtilMethods.ranking(pointsGrades,
					DescriptiveStatistics::getMean);
			Map<EnrolledUser, Integer> rankingActivities = UtilMethods.ranking(pointsActivities);

			data.put("columns", createColumns(users.size(), UtilMethods.getMax(pointsLog.values()), 10,
					activities.size(), typeLogs, gradeItems, activities));
			append(users, pointsLog, pointsGrades, pointsActivities, rankingLog, rankingGrades, rankingActivities,
					jsArray);
		}

		return data.toString();
	}

	public <T extends Comparable<T>> void append(List<EnrolledUser> users, Map<EnrolledUser, Integer> pointsLog,
			Map<EnrolledUser, DescriptiveStatistics> pointsGrades, Map<EnrolledUser, Integer> pointsActivities,
			Map<EnrolledUser, T> rankingLog, Map<EnrolledUser, T> rankingGrades, Map<EnrolledUser, T> rankingActivities,
			JSArray jsArray) {
		String rankingLogField = RANKING + LOG;
		String rankingGradeField = RANKING + GRADE_ITEM;
		String rankingActivityField = RANKING + ACTIVITY_COMPLETION;

		for (EnrolledUser user : users) {
			JSObject jsObject = new JSObject();
			jsObject.putWithQuote(USER, user.getFullName());
			jsObject.put(LOG, pointsLog.get(user));
			jsObject.put(GRADE_ITEM, Math.floor(pointsGrades.get(user)
					.getMean() * 100) / 100);
			jsObject.put(ACTIVITY_COMPLETION, pointsActivities.get(user));

			jsObject.put(rankingLogField, rankingLog.get(user));
			jsObject.put(rankingGradeField, rankingGrades.get(user));
			jsObject.put(rankingActivityField, rankingActivities.get(user));

			jsArray.add(jsObject);
		}
	}

	private <E> JSArray createColumns(int selectedUsers, Number maxLog, Number maxGrade, Number maxActivity,
			List<E> logs, List<GradeItem> gradeItems, List<CourseModule> activities) {

		JSArray jsArray = new JSArray();
		JSObject jsObject = new JSObject();
		jsObject.putWithQuote("title", I18n.get("text.selectedUsers") + " (" + selectedUsers + ")");
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
				LOG, maxLog, logs));
		jsArray.add(createProgressColumn(selectionController.getTabUbuGrades()
				.getText(), GRADE_ITEM, maxGrade, gradeItems));
		jsArray.add(createProgressColumn(selectionController.getTabActivity()
				.getText(), ACTIVITY_COMPLETION, maxActivity, activities));
		return jsArray;

	}

	private <E> JSObject createProgressColumn(String title, String field, Number max, Collection<E> collection) {
		JSObject jsObject = new JSObject();
		jsObject.putWithQuote("title", title + " (" + collection.size() + ")");
		jsObject.putWithQuote("field", field);

		if (!collection.isEmpty()) {

			jsObject.put("formatter", "'progress'");
			JSObject formatterParams = new JSObject();
			jsObject.put("tooltip", "function(cell){return cell.getRow().getData().ranking" + field
					+ "+'/'+cell.getTable().getRows().length}");
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

	public static <E> Map<EnrolledUser, Integer> getLogsPoints(List<EnrolledUser> users, List<E> typeLogs,
			DataSet<E> dataSet, GroupByAbstract<?> groupBy, LocalDate start, LocalDate end) {
		return dataSet.getUserTotalLogs(groupBy, users, typeLogs, start, end);
	}

	public static Map<EnrolledUser, DescriptiveStatistics> getGradeItemPoints(Collection<EnrolledUser> users,
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

	public static Map<EnrolledUser, Integer> getActivityCompletionPoints(Collection<EnrolledUser> users,
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

		Map<EnrolledUser, Integer> pointsLog = getLogsPoints(users, typeLogs, dataSet, actualCourse.getLogStats()
				.getByType(TypeTimes.DAY), datePickerStart.getValue(), datePickerEnd.getValue());

		Map<EnrolledUser, DescriptiveStatistics> pointsGrades = getGradeItemPoints(users, gradeItems);

		Map<EnrolledUser, Integer> pointsActivities = getActivityCompletionPoints(users, activities,
				datePickerStart.getValue()
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant(),
				datePickerEnd.getValue()
						.plusDays(1)
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant());
		if ((boolean) getConfigValue("statisticsRanking")) {
			Map<EnrolledUser, Double> rankingLog = UtilMethods.rankingStatistics(pointsLog);
			Map<EnrolledUser, Double> rankingGrades = UtilMethods.rankingStatistics(pointsGrades,
					DescriptiveStatistics::getMean);
			Map<EnrolledUser, Double> rankingActivities = UtilMethods.rankingStatistics(pointsActivities);
			print(printer, users, pointsLog, pointsGrades, pointsActivities, rankingLog, rankingGrades,
					rankingActivities);
		} else {
			Map<EnrolledUser, Integer> rankingLog = UtilMethods.ranking(pointsLog);
			Map<EnrolledUser, Integer> rankingGrades = UtilMethods.ranking(pointsGrades,
					DescriptiveStatistics::getMean);
			Map<EnrolledUser, Integer> rankingActivities = UtilMethods.ranking(pointsActivities);
			print(printer, users, pointsLog, pointsGrades, pointsActivities, rankingLog, rankingGrades,
					rankingActivities);
		}

	}

	public <T extends Comparable<T>> void print(CSVPrinter printer, List<EnrolledUser> users,
			Map<EnrolledUser, Integer> pointsLog, Map<EnrolledUser, DescriptiveStatistics> pointsGrades,
			Map<EnrolledUser, Integer> pointsActivities, Map<EnrolledUser, T> rankingLog,
			Map<EnrolledUser, T> rankingGrades, Map<EnrolledUser, T> rankingActivities) throws IOException {
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
	public void fillOptions(JSObject jsObject) {

		jsObject.put("invalidOptionWarnings", false);
		jsObject.put("height", "height");
		jsObject.put("tooltipsHeader", true);
		jsObject.put("virtualDom", true);
		jsObject.putWithQuote("layout", "fitColumns");
		jsObject.put("rowClick", "function(e,row){javaConnector.dataPointSelection(row.getPosition());}");

	}
}
