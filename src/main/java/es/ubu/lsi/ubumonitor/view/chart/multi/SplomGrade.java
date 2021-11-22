package es.ubu.lsi.ubumonitor.view.chart.multi;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.model.log.TypeTimes;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.logs.PlotlyLog;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TreeView;

public class SplomGrade extends PlotlyLog {

	private TreeView<GradeItem> treeViewGradeItem;

	public SplomGrade(MainController mainController, TreeView<GradeItem> treeViewGradeItem, DatePicker start,
			DatePicker end) {
		super(mainController, ChartType.SPLOM_GRADE);
		this.treeViewGradeItem = treeViewGradeItem;
		this.datePickerStart = start;
		this.datePickerEnd = end;
		useRangeDate = true;
	}

	@Override
	public <E> JSArray createData(List<E> typeLogs, DataSet<E> dataSet, List<EnrolledUser> selectedUsers,
			LocalDate dateStart, LocalDate dateEnd, GroupByAbstract<?> groupBy) {
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		Map<EnrolledUser, DescriptiveStatistics> userGradeItemPoints = RankingTable.getGradeItemPoints(selectedUsers,
				gradeItems);

		Map<EnrolledUser, Map<E, Integer>> pointsLog = getLogsPoints(selectedUsers, typeLogs, dataSet,
				actualCourse.getLogStats()
						.getByType(TypeTimes.DAY),
				datePickerStart.getValue(), datePickerEnd.getValue());

		JSArray dataArray = new JSArray();
		JSObject data = new JSObject();
		dataArray.add(data);
		data.put("type", "'splom'");
		JSArray dimensions = new JSArray();
		data.put("dimensions", dimensions);
		for (E typeLog : typeLogs) {
			JSObject dimension = new JSObject();
			dimensions.add(dimension);
			dimension.putWithQuote("label", dataSet.translate(typeLog));
			JSArray values = new JSArray();
			dimension.put("values", values);
			for (EnrolledUser user : selectedUsers) {
				Integer value = pointsLog.getOrDefault(user, Collections.emptyMap())
						.getOrDefault(typeLog, 0);
				
				values.add(value);
			}
		}
		JSObject marker = new JSObject();
		//data.put("marker", marker);
		marker.put("size", 7);
		JSArray colors = new JSArray();
		marker.put("color", colors);
		JSArray colorScale = new JSArray();
		marker.put("colorscale", colorScale);
		colorScale.add("[0," + colorToRGB(getConfigValue("minGradeColor")) + "]");
		colorScale.add("[1," + colorToRGB(getConfigValue("maxGradeColor")) + "]");
		
		JSArray text = new JSArray();
		data.put("text", text);
		for (EnrolledUser user : selectedUsers) {
			double mean = userGradeItemPoints.get(user)
					.getMean();
			text.addWithQuote(user.getFullName() + "<br>" + mean);
			colors.add(mean / 10);
		}
		
		return dataArray;
	}

	private <E> Map<EnrolledUser, Map<E, Integer>> getLogsPoints(List<EnrolledUser> users, List<E> typeLogs,
			DataSet<E> dataSet, GroupByAbstract<?> groupBy, LocalDate start, LocalDate end) {
		return dataSet.getUserLogsGroupedByLogElement(groupBy, users, typeLogs, start, end);
	}

	@Override
	public <E> JSObject createLayout(List<E> typeLogs, DataSet<E> dataSet, LocalDate dateStart, LocalDate dateEnd,
			GroupByAbstract<?> groupBy) {
		JSObject layout = new JSObject();
		layout.put("hovermode", "'closest'");
		layout.put("dragmode", "'select'");
		return layout;
	}

	@Override
	protected <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	protected String[] getCSVHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		// not has a desglosed version of CSV

	}

	@Override
	protected String[] getCSVDesglosedHeader() {
		return new String[0];
	}

}
