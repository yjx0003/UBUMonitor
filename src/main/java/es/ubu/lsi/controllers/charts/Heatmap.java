package controllers.charts;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.Controller;
import controllers.I18n;
import controllers.MainController;
import controllers.configuration.MainConfiguration;
import controllers.datasets.DataSet;
import controllers.datasets.DataSetComponent;
import controllers.datasets.DataSetComponentEvent;
import controllers.datasets.DataSetSection;
import controllers.datasets.DatasSetCourseModule;
import controllers.ubulogs.GroupByAbstract;
import model.EnrolledUser;
import util.UtilMethods;

public class Heatmap extends ApexCharts {

	private static final Logger LOGGER = LoggerFactory.getLogger(Heatmap.class);
	private String max;
	private DescriptiveStatistics descriptiveStatistics;

	public Heatmap(MainController mainController) {
		super(mainController, ChartType.HEAT_MAP, Tabs.LOGS);
		descriptiveStatistics = new DescriptiveStatistics();
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

	public <T> String createSeries(List<EnrolledUser> enrolledUsers, List<EnrolledUser> selectedUsers,
			List<T> selecteds, GroupByAbstract<?> groupBy, LocalDate dateStart, LocalDate dateEnd, DataSet<T> dataSet) {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[");

		Map<EnrolledUser, Map<T, List<Long>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, selecteds,
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

			Map<T, List<Long>> types = userCounts.get(selectedUser);
			List<Long> results = new ArrayList<>();
			for (int j = 0; j < rangeDates.size(); j++) {
				long result = 0;
				for (T type : selecteds) {
					List<Long> times = types.get(type);
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

		StringJoiner jsObject = getDefaultOptions();
		addKeyValueWithQuote(jsObject, "typeGraph", "heatmap");
		addKeyValue(jsObject, "tooltip", "{x:{show:!0}}");

		addKeyValue(jsObject, "legend", "{position:'top'}");
		addKeyValue(jsObject, "chart",
				"{type:\"heatmap\",events:{dataPointSelection:function(e,t,n){javaConnector.dataPointSelection(n.w.config.series.length-1-n.seriesIndex)}},height:height,toolbar:{show:!1},animations:{enabled:!1}}");
		addKeyValue(jsObject, "dataLabels",
				"{formatter:function(r,t){return 0==r?\"\":r},style:{colors:[\"#000000\"]}}");

		addKeyValue(jsObject, "xaxis", "{" + getXScaleLabel() + "}");

		if ((boolean) mainConfiguration.getValue(getChartType(), "useQuartile")) {
			quartileColor(zeroValue, firstInterval, secondInterval, thirdInterval, fourthInterval, jsObject);
		} else {
			intervalColor(zeroValue, firstInterval, secondInterval, thirdInterval, fourthInterval, moreMax,
					getSuggestedMax(), jsObject);
		}

		return jsObject.toString();

	}

	private void intervalColor(String zeroValue, String firstInterval, String secondInterval, String thirdInterval,
			String fourthInterval, String moreMax, long maxValue, StringJoiner jsObject) {
		addKeyValue(jsObject, "plotOptions",
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
			String fourthInterval, StringJoiner jsObject) {
		long first = Math.max(1, Math.round(descriptiveStatistics.getPercentile(25)));
		long second = Math.max(1, Math.round(descriptiveStatistics.getPercentile(50)));
		long third = Math.max(1, Math.round(descriptiveStatistics.getPercentile(75)));
		long fourth = Math.max(1, Math.round(descriptiveStatistics.getPercentile(100)));
		addKeyValue(jsObject, "plotOptions",
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

}
