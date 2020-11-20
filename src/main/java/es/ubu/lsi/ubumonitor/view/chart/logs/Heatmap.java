package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;

public class Heatmap extends ChartLogs {

	private static final Logger LOGGER = LoggerFactory.getLogger(Heatmap.class);

	private DescriptiveStatistics descriptiveStatistics;

	public Heatmap(MainController mainController) {
		super(mainController, ChartType.HEAT_MAP);
		descriptiveStatistics = new DescriptiveStatistics();
		useGroupBy = true;
		useLegend = true;
		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}
	public String getXScaleLabel() {
	
		JSObject jsObject = new JSObject();

		boolean display = mainConfiguration.getValue(MainConfiguration.GENERAL, "displayXScaleTitle");
		if (!display) {
			return "title:{}";
		}
		jsObject.putWithQuote("text", getXAxisTitle());
		JSObject style = new JSObject();
		style.putWithQuote("fontSize", 14);
		style.put("color", colorToRGB(mainConfiguration.getValue(MainConfiguration.GENERAL, "fontColorXScaleTitle")));
		style.putWithQuote("cssClass", "apexcharts");
		jsObject.put("style", style);

		return "title:" + jsObject;

	}

	public String getYScaleLabel() {
	
		JSObject jsObject = new JSObject();

		boolean display = mainConfiguration.getValue(MainConfiguration.GENERAL, "displayYScaleTitle");
		if (!display) {
			return "title:{}";
		}
		jsObject.putWithQuote("text", getYAxisTitle());
		JSObject style = new JSObject();
		style.putWithQuote("fontSize", 14);
		style.put("color", colorToRGB(mainConfiguration.getValue(MainConfiguration.GENERAL, "fontColorYScaleTitle")));
		jsObject.put("style", style.toString());
		return "title:" + jsObject.toString();

	}

	public static String createCategory(GroupByAbstract<?> groupBy, LocalDate dateStart, LocalDate dateEnd) {
		return "[" + UtilMethods.joinWithQuotes(groupBy.getRangeString(dateStart, dateEnd)) + "]";
	}

	@Override
	public String calculateMax() {

		long maxYAxis = 1L;
		List<EnrolledUser> users = getUsers();
		if (tabComponent.isSelected()) {
			maxYAxis = choiceBoxDate.getValue()
					.getComponents()
					.getMaxElement(users, listViewComponent.getSelectionModel()
							.getSelectedItems(), datePickerStart.getValue(), datePickerEnd.getValue());
		} else if (tabEvent.isSelected()) {
			maxYAxis = choiceBoxDate.getValue()
					.getComponentsEvents()
					.getMaxElement(users, listViewEvent.getSelectionModel()
							.getSelectedItems(), datePickerStart.getValue(), datePickerEnd.getValue());
		} else if (tabSection.isSelected()) {
			maxYAxis = choiceBoxDate.getValue()
					.getSections()
					.getMaxElement(users, listViewSection.getSelectionModel()
							.getSelectedItems(), datePickerStart.getValue(), datePickerEnd.getValue());
		} else if (tabCourseModule.isSelected()) {
			maxYAxis = choiceBoxDate.getValue()
					.getCourseModules()
					.getMaxElement(users, listViewCourseModule.getSelectionModel()
							.getSelectedItems(), datePickerStart.getValue(), datePickerEnd.getValue());
		}
		return Long.toString(maxYAxis);
	}

	@Override
	public void fillOptions(JSObject jsObject) {

		String zeroValue = colorToRGB(mainConfiguration.getValue(getChartType(), "zeroValue"));
		String firstInterval = colorToRGB(mainConfiguration.getValue(getChartType(), "firstInterval"));
		String secondInterval = colorToRGB(mainConfiguration.getValue(getChartType(), "secondInterval"));
		String thirdInterval = colorToRGB(mainConfiguration.getValue(getChartType(), "thirdInterval"));
		String fourthInterval = colorToRGB(mainConfiguration.getValue(getChartType(), "fourthInterval"));
		String moreMax = colorToRGB(mainConfiguration.getValue(getChartType(), "moreMax"));

		jsObject.putWithQuote("typeGraph", "heatmap");
		jsObject.put("tooltip", "{x:{show:!0}}");

		jsObject.put("legend", "{position:'top'}");
		jsObject.put("chart",
				"{type:'heatmap',events:{dataPointSelection:function(e,t,n){javaConnector.dataPointSelection(n.w.config.series.length-1-n.seriesIndex)}},height:height,toolbar:{show:!1},animations:{enabled:!1}}");
		jsObject.put("dataLabels", "{formatter:function(r,t){return 0==r?\"\":r},style:{colors:[\"#000000\"]}}");

		jsObject.put("xaxis", "{" + getXScaleLabel() + "}");
		jsObject.put("yaxis", "{labels:{maxWidth:300}}");

		if ((boolean) mainConfiguration.getValue(getChartType(), "useQuartile")) {
			quartileColor(zeroValue, firstInterval, secondInterval, thirdInterval, fourthInterval, jsObject);
		} else {
			intervalColor(zeroValue, firstInterval, secondInterval, thirdInterval, fourthInterval, moreMax,
					getSuggestedMax(textFieldMax.getText()), jsObject);
		}

		

	}

	private void intervalColor(String zeroValue, String firstInterval, String secondInterval, String thirdInterval,
			String fourthInterval, String moreMax, long maxValue, JSObject jsObject) {
		jsObject.put("plotOptions",
				"{heatmap:{enableShades:!1,colorScale:{ranges:[{from:-1,to:0,color:" + zeroValue
						+ ",name:'0'},{from:1,to:" + Math.max(Math.floor(.25 * maxValue), 1) + ",color:" + firstInterval
						+ "},{from:" + Math.max(Math.ceil(.25 * maxValue), 1) + ",to:"
						+ Math.max(Math.floor(.5 * maxValue), 1) + ",color:" + secondInterval + "},{from:"
						+ Math.max(Math.ceil(.5 * maxValue), 1) + ",to:" + Math.max(Math.floor(.75 * maxValue), 1)
						+ ",color:" + thirdInterval + "},{from:" + Math.max(Math.ceil(.75 * maxValue), 1) + ",to:"
						+ Math.max(Math.max(maxValue, 1), Math.floor(.75 * maxValue)) + ",color:" + fourthInterval
						+ "},{from:" + (maxValue + 1) + ",to:Number.POSITIVE_INFINITY,color:" + moreMax + ",name:'+'+("
						+ (maxValue + 1) + ")}]}}}");
	}

	private void quartileColor(String zeroValue, String firstInterval, String secondInterval, String thirdInterval,
			String fourthInterval, JSObject jsObject) {
		long first = Math.max(1, Math.round(descriptiveStatistics.getPercentile(25)));
		long second = Math.max(1, Math.round(descriptiveStatistics.getPercentile(50)));
		long third = Math.max(1, Math.round(descriptiveStatistics.getPercentile(75)));
		long fourth = Math.max(1, Math.round(descriptiveStatistics.getPercentile(100)));
		jsObject.put("plotOptions",
				"{heatmap:{enableShades:!1,colorScale:{ranges:[{from:-1,to:0,color:" + zeroValue
						+ ",name:'0'},{from:1,to:" + first + ",color:" + firstInterval + "},{from:" + first + ",to:"
						+ second + ",color:" + secondInterval + "},{from:" + second + ",to:" + third + ",color:"
						+ thirdInterval + "},{from:" + third + ",to:" + fourth + ",color:" + fourthInterval + "}]}}}");
	}

	@Override
	public String getXAxisTitle() {
		return MessageFormat.format(I18n.get(getChartType() + ".xAxisTitle"), I18n.get(choiceBoxDate.getValue()
				.getTypeTime()));
	}

	@Override
	protected String getJSFunction(String dataset, String options) {
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		String categories = createCategory(groupBy, dateStart, dateEnd);
		LOGGER.debug("Categorias de heatmap {}", categories);
		return "updateApexCharts(" + dataset + "," + categories + "," + options + ")";
	}

	@Override
	public <E> String createData(List<E> typeLogs, DataSet<E> dataSet) {
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		List<EnrolledUser> enrolledUsers = getUsers();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[");

		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, typeLogs,
				dateStart, dateEnd);
		List<String> rangeDates = groupBy.getRangeString(dateStart, dateEnd);

		boolean useQuartile = mainConfiguration.getValue(getChartType(), "useQuartile");

		if (useQuartile) {
			descriptiveStatistics.clear();
		}

		for (int i = selectedUsers.size() - 1; i >= 0; i--) {
			EnrolledUser selectedUser = selectedUsers.get(i);

			stringBuilder.append("{name:'" + UtilMethods.escapeJavaScriptText(selectedUser.toString()) + "',");

			Map<E, List<Integer>> types = userCounts.get(selectedUser);
			List<Long> results = new ArrayList<>();
			for (int j = 0; j < rangeDates.size(); j++) {
				long result = 0;
				for (E type : typeLogs) {
					List<Integer> times = types.get(type);
					result += times.get(j);
				}
				if (useQuartile && result != 0) {
					descriptiveStatistics.addValue(result);
				}
				results.add(result);

			}

			stringBuilder.append("data: [" + UtilMethods.join(results) + "]},");

		}
		stringBuilder.append("]");

		return stringBuilder.toString();
	}

	@Override
	protected <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<?> rangeDates = groupBy.getRange(dateStart, dateEnd);
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, typeLogs,
				dateStart, dateEnd);
		for (EnrolledUser selectedUser : enrolledUsers) {
			Map<E, List<Integer>> types = userCounts.get(selectedUser);
			List<Integer> results = new ArrayList<>();
			for (int j = 0; j < rangeDates.size(); j++) {
				int result = 0;
				for (E type : typeLogs) {
					List<Integer> times = types.get(type);
					result += times.get(j);
				}
				results.add(result);

			}
			printer.print(selectedUser.getId());
			printer.print(selectedUser.getFullName());
			printer.printRecord(results);
		}
	}

	@Override
	protected String[] getCSVHeader() {
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<String> range = groupBy.getRangeString(dateStart, dateEnd);
		range.add(0, "userid");
		range.add(1, "fullname");
		return range.toArray(new String[0]);
	}

	@Override
	protected <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, typeLogs,
				dateStart, dateEnd);
		boolean hasId = hasId();
		for (EnrolledUser selectedUser : enrolledUsers) {
			Map<E, List<Integer>> types = userCounts.get(selectedUser);

			for (E type : typeLogs) {
				List<Integer> times = types.get(type);
				printer.print(selectedUser.getId());
				printer.print(selectedUser.getFullName());
				if (hasId) {
					printer.print(type.hashCode());
				}

				printer.print(type);
				printer.printRecord(times);
			}

		}

	}

	@Override
	protected String[] getCSVDesglosedHeader() {
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<String> list = new ArrayList<>();
		list.add("userid");
		list.add("fullname");
		String selectedTab = tabPaneSelection.getSelectionModel()
				.getSelectedItem()
				.getText();
		if (hasId()) {
			list.add(selectedTab + "_id");
		}
		list.add(selectedTab);
		list.addAll(groupBy.getRangeString(dateStart, dateEnd));
		return list.toArray(new String[0]);
	}
}
