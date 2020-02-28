package es.ubu.lsi.ubumonitor.controllers.charts;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSet;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetComponent;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetComponentEvent;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetSection;
import es.ubu.lsi.ubumonitor.controllers.datasets.DatasSetCourseModule;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.GroupByAbstract;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;

public class Heatmap extends ApexCharts {

	private static final Logger LOGGER = LoggerFactory.getLogger(Heatmap.class);
	private String max;
	private DescriptiveStatistics descriptiveStatistics;

	public Heatmap(MainController mainController) {
		super(mainController, ChartType.HEAT_MAP, Tabs.LOGS);
		descriptiveStatistics = new DescriptiveStatistics();
		useGroupBy = true;
	}

	@Override
	public void update() {
		String heatmapdataset = null;
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		List<EnrolledUser> enrolledUsers = new ArrayList<>(listParticipants.getItems());

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();

		if (tabUbuLogsComponent.isSelected()) {

			heatmapdataset = createSeries(enrolledUsers, selectedUsers,
					listViewComponents.getSelectionModel().getSelectedItems(), choiceBoxDate.getValue(), dateStart,
					dateEnd, DataSetComponent.getInstance());
		} else if (tabUbuLogsEvent.isSelected()) {

			heatmapdataset = createSeries(enrolledUsers, selectedUsers,
					listViewEvents.getSelectionModel().getSelectedItems(), choiceBoxDate.getValue(), dateStart, dateEnd,
					DataSetComponentEvent.getInstance());
		} else if (tabUbuLogsSection.isSelected()) {

			heatmapdataset = createSeries(enrolledUsers, selectedUsers,
					listViewSection.getSelectionModel().getSelectedItems(), choiceBoxDate.getValue(), dateStart,
					dateEnd, DataSetSection.getInstance());
		} else if (tabUbuLogsCourseModule.isSelected()) {

			heatmapdataset = createSeries(enrolledUsers, selectedUsers,
					listViewCourseModule.getSelectionModel().getSelectedItems(), choiceBoxDate.getValue(), dateStart,
					dateEnd, DatasSetCourseModule.getInstance());
		}

		String categories = createCategory(choiceBoxDate.getValue(), dateStart, dateEnd);
		LOGGER.info("Dataset para el heatmap en JS: {}", heatmapdataset);
		LOGGER.info("Categorias del heatmap en JS: {}", categories);
		LOGGER.info("Opciones del heatmap ne JS: {}", getOptions());

		webViewChartsEngine
				.executeScript(String.format("updateApexCharts(%s,%s, %s)", heatmapdataset, categories, getOptions()));
	}

	public <E> String createSeries(List<EnrolledUser> enrolledUsers, List<EnrolledUser> selectedUsers,
			List<E> selecteds, GroupByAbstract<?> groupBy, LocalDate dateStart, LocalDate dateEnd, DataSet<E> dataSet) {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[");

		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, selecteds,
				dateStart, dateEnd);
		List<String> rangeDates = groupBy.getRangeString(dateStart, dateEnd);

		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
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
				for (E type : selecteds) {
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

	public static String createCategory(GroupByAbstract<?> groupBy, LocalDate dateStart, LocalDate dateEnd) {
		return "[" + UtilMethods.joinWithQuotes(groupBy.getRangeString(dateStart, dateEnd)) + "]";
	}

	@Override
	public String calculateMax() {

		long maxYAxis = 1L;
		if (tabUbuLogsComponent.isSelected()) {
			maxYAxis = choiceBoxDate.getValue().getComponents().getMaxElement(listParticipants.getItems(),
					listViewComponents.getSelectionModel().getSelectedItems(), datePickerStart.getValue(),
					datePickerEnd.getValue());
		} else if (tabUbuLogsEvent.isSelected()) {
			maxYAxis = choiceBoxDate.getValue().getComponentsEvents().getMaxElement(listParticipants.getItems(),
					listViewEvents.getSelectionModel().getSelectedItems(), datePickerStart.getValue(),
					datePickerEnd.getValue());
		} else if (tabUbuLogsSection.isSelected()) {
			maxYAxis = choiceBoxDate.getValue().getSections().getMaxElement(listParticipants.getItems(),
					listViewSection.getSelectionModel().getSelectedItems(), datePickerStart.getValue(),
					datePickerEnd.getValue());
		} else if (tabUbuLogsCourseModule.isSelected()) {
			maxYAxis = choiceBoxDate.getValue().getCourseModules().getMaxElement(listParticipants.getItems(),
					listViewCourseModule.getSelectionModel().getSelectedItems(), datePickerStart.getValue(),
					datePickerEnd.getValue());
		}
		return Long.toString(maxYAxis);
	}

	@Override
	public String getOptions() {
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		String zeroValue = colorToRGB(mainConfiguration.getValue(getChartType(), "zeroValue"));
		String firstInterval = colorToRGB(mainConfiguration.getValue(getChartType(), "firstInterval"));
		String secondInterval = colorToRGB(mainConfiguration.getValue(getChartType(), "secondInterval"));
		String thirdInterval = colorToRGB(mainConfiguration.getValue(getChartType(), "thirdInterval"));
		String fourthInterval = colorToRGB(mainConfiguration.getValue(getChartType(), "fourthInterval"));
		String moreMax = colorToRGB(mainConfiguration.getValue(getChartType(), "moreMax"));

		JSObject jsObject = getDefaultOptions();
		jsObject.putWithQuote( "typeGraph", "heatmap");
		jsObject.put("tooltip", "{x:{show:!0}}");

		jsObject.put("legend", "{position:'top'}");
		jsObject.put( "chart",
				"{type:\"heatmap\",events:{dataPointSelection:function(e,t,n){javaConnector.dataPointSelection(n.w.config.series.length-1-n.seriesIndex)}},height:height,toolbar:{show:!1},animations:{enabled:!1}}");
		jsObject.put("dataLabels",
				"{formatter:function(r,t){return 0==r?\"\":r},style:{colors:[\"#000000\"]}}");

		jsObject.put("xaxis", "{" + getXScaleLabel() + "}");

		if ((boolean) mainConfiguration.getValue(getChartType(), "useQuartile")) {
			quartileColor(zeroValue, firstInterval, secondInterval, thirdInterval, fourthInterval, jsObject);
		} else {
			intervalColor(zeroValue, firstInterval, secondInterval, thirdInterval, fourthInterval, moreMax,
					getSuggestedMax(), jsObject);
		}

		return jsObject.toString();

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
	public String getMax() {
		return max;
	}

	@Override
	public void setMax(String max) {
		this.max = max;
	}

	@Override
	public String getXAxisTitle() {
		return MessageFormat.format(I18n.get(getChartType() + ".xAxisTitle"),
				I18n.get(choiceBoxDate.getValue().getTypeTime()));
	}

	public void exportCSV(String path) throws IOException {
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<String> range = groupBy.getRangeString(dateStart, dateEnd);
		range.add(0, "userid");
		range.add(1, "fullname");

		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader(range.toArray(new String[0])))) {
			if (tabUbuLogsComponent.isSelected()) {
				exportCSV(printer, DataSetComponent.getInstance(),
						listViewComponents.getSelectionModel().getSelectedItems());
			} else if (tabUbuLogsEvent.isSelected()) {
				exportCSV(printer, DataSetComponentEvent.getInstance(),
						listViewEvents.getSelectionModel().getSelectedItems());
			} else if (tabUbuLogsSection.isSelected()) {
				exportCSV(printer, DataSetSection.getInstance(),
						listViewSection.getSelectionModel().getSelectedItems());
			} else if (tabUbuLogsCourseModule.isSelected()) {
				exportCSV(printer, DatasSetCourseModule.getInstance(),
						listViewCourseModule.getSelectionModel().getSelectedItems());
			}
		}
	}

	private <T> void exportCSV(CSVPrinter printer, DataSet<T> dataSet, List<T> selecteds) throws IOException {

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<?> rangeDates = groupBy.getRange(dateStart, dateEnd);
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		Map<EnrolledUser, Map<T, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, selecteds,
				dateStart, dateEnd);
		for (EnrolledUser selectedUser : enrolledUsers) {
			Map<T, List<Integer>> types = userCounts.get(selectedUser);
			List<Integer> results = new ArrayList<>();
			for (int j = 0; j < rangeDates.size(); j++) {
				int result = 0;
				for (T type : selecteds) {
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
	public void exportCSVDesglosed(String path) throws IOException {
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<String> range = groupBy.getRangeString(dateStart, dateEnd);
		range.add(0, "userid");
		range.add(1, "fullname");
		range.add(2, "log");

		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader(range.toArray(new String[0])))) {
			if (tabUbuLogsComponent.isSelected()) {
				exportCSVDesglosed(printer, DataSetComponent.getInstance(),
						listViewComponents.getSelectionModel().getSelectedItems());
			} else if (tabUbuLogsEvent.isSelected()) {
				exportCSVDesglosed(printer, DataSetComponentEvent.getInstance(),
						listViewEvents.getSelectionModel().getSelectedItems());
			} else if (tabUbuLogsSection.isSelected()) {
				exportCSVDesglosed(printer, DataSetSection.getInstance(),
						listViewSection.getSelectionModel().getSelectedItems());
			} else if (tabUbuLogsCourseModule.isSelected()) {
				exportCSVDesglosed(printer, DatasSetCourseModule.getInstance(),
						listViewCourseModule.getSelectionModel().getSelectedItems());
			}
		}
	}

	private <T> void exportCSVDesglosed(CSVPrinter printer, DataSet<T> dataSet, List<T> selecteds) throws IOException {

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		Map<EnrolledUser, Map<T, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, selecteds,
				dateStart, dateEnd);
		for (EnrolledUser selectedUser : enrolledUsers) {
			Map<T, List<Integer>> types = userCounts.get(selectedUser);

			for (T type : selecteds) {
				List<Integer> times = types.get(type);
				printer.print(selectedUser.getId());
				printer.print(selectedUser.getFullName());
				printer.print(type);
				printer.printRecord(times);
			}

		}

	}
}
