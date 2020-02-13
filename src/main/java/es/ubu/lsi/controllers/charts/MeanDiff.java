package es.ubu.lsi.controllers.charts;

import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.controllers.Controller;
import es.ubu.lsi.controllers.I18n;
import es.ubu.lsi.controllers.MainController;
import es.ubu.lsi.controllers.configuration.MainConfiguration;
import es.ubu.lsi.controllers.datasets.DataSet;
import es.ubu.lsi.controllers.datasets.DataSetComponent;
import es.ubu.lsi.controllers.datasets.DataSetComponentEvent;
import es.ubu.lsi.controllers.datasets.DataSetSection;
import es.ubu.lsi.controllers.datasets.DatasSetCourseModule;
import es.ubu.lsi.controllers.ubulogs.GroupByAbstract;
import es.ubu.lsi.model.EnrolledUser;

public class MeanDiff extends ChartjsLog {

	public MeanDiff(MainController mainController) {
		super(mainController, ChartType.MEAN_DIFF);
		useLegend = true;
		useNegativeValues = true;
	}

	@Override
	public <T> String createData(List<EnrolledUser> enrolledUsers, List<EnrolledUser> selectedUsers, List<T> typeLogs,
			GroupByAbstract<?> groupBy, LocalDate dateStart, LocalDate dateEnd, DataSet<T> dataSet) {

		Map<EnrolledUser, Map<T, List<Long>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, typeLogs,
				dateStart, dateEnd);

		Map<T, List<Double>> means = dataSet.getMeans(groupBy, enrolledUsers, typeLogs, dateStart, dateEnd);

		List<String> rangeDates = groupBy.getRangeString(dateStart, dateEnd);

		StringJoiner data = JSObject();

		addKeyValue(data, "labels", createLabels(rangeDates));

		StringJoiner datasets = JSArray();
		List<Double> listMeans = createMeanList(typeLogs, means, rangeDates);
		createEnrolledUsersDatasets(selectedUsers, typeLogs, userCounts, listMeans, rangeDates, datasets);

		addKeyValue(data, "datasets", datasets);

		return data.toString();
	}

	private <T> void createEnrolledUsersDatasets(List<EnrolledUser> selectedUsers, List<T> typeLogs,
			Map<EnrolledUser, Map<T, List<Long>>> userCounts, List<Double> listMeans, List<String> rangeDates,
			StringJoiner datasets) {
		for (EnrolledUser selectedUser : selectedUsers) {
			StringJoiner dataset = JSObject();
			addKeyValueWithQuote(dataset, "label", selectedUser.getFullName());
			addKeyValue(dataset, "borderColor", hex(selectedUser.getId()));
			addKeyValue(dataset, "backgroundColor", rgba(selectedUser.getId(), OPACITY));

			Map<T, List<Long>> types = userCounts.get(selectedUser);
			StringJoiner results = JSArray();
			long cum = 0;
			for (int j = 0; j < rangeDates.size(); j++) {
				long result = 0;
				for (T typeLog : typeLogs) {
					List<Long> times = types.get(typeLog);
					result += times.get(j);
				}
				cum += result;

				results.add(Double.toString(cum - listMeans.get(j)));
			}
			addKeyValue(dataset, "data", results);
			datasets.add(dataset.toString());

		}

	}

	private <T> List<Double> createMeanList(List<T> typeLogs, Map<T, List<Double>> means, List<String> rangeDates) {
		List<Double> results = new ArrayList<>();
		double cum = 0;
		for (int j = 0; j < rangeDates.size(); j++) {
			double result = 0;
			for (T typeLog : typeLogs) {
				List<Double> times = means.get(typeLog);
				result += times.get(j);
			}
			cum += result;
			results.add(cum);
		}
		return results;

	}

	@Override
	public String calculateMax() {
		long maxYAxis = 1L;
		if (tabUbuLogsComponent.isSelected()) {
			maxYAxis = choiceBoxDate.getValue().getComponents().getMeanDifferenceMax(listParticipants.getItems(),
					listViewComponents.getSelectionModel().getSelectedItems(), datePickerStart.getValue(),
					datePickerEnd.getValue());
		} else if (tabUbuLogsEvent.isSelected()) {
			maxYAxis = choiceBoxDate.getValue().getComponentsEvents().getMeanDifferenceMax(listParticipants.getItems(),
					listViewEvents.getSelectionModel().getSelectedItems(), datePickerStart.getValue(),
					datePickerEnd.getValue());
		} else if (tabUbuLogsSection.isSelected()) {
			maxYAxis = choiceBoxDate.getValue().getSections().getMeanDifferenceMax(listParticipants.getItems(),
					listViewSection.getSelectionModel().getSelectedItems(), datePickerStart.getValue(),
					datePickerEnd.getValue());
		} else if (tabUbuLogsCourseModule.isSelected()) {
			maxYAxis = choiceBoxDate.getValue().getCourseModules().getMeanDifferenceMax(listParticipants.getItems(),
					listViewCourseModule.getSelectionModel().getSelectedItems(), datePickerStart.getValue(),
					datePickerEnd.getValue());
		}
		return Long.toString(maxYAxis);
	}

	@Override
	public String getOptions() {
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();

		StringJoiner jsObject = getDefaultOptions();
		addKeyValueWithQuote(jsObject, "typeGraph", "line");
		addKeyValue(jsObject, "scales",
				"{yAxes:[{" + getYScaleLabel() + ",gridLines:{zeroLineColor:"
						+ colorToRGB(mainConfiguration.getValue(getChartType(), "zeroLineColor")) + ",zeroLineWidth:"
						+ mainConfiguration.getValue(getChartType(), "zeroLineWidth") + ",zeroLineBorderDash:["
						+ mainConfiguration.getValue(MainConfiguration.GENERAL, "borderLength") + ","
						+ mainConfiguration.getValue(MainConfiguration.GENERAL, "borderSpace")
						+ "]},ticks:{suggestedMax:" + getSuggestedMax() + ",suggestedMin:" + -getSuggestedMax()
						+ ",stepSize:0}}],xAxes:[{" + getXScaleLabel() + "}]}");
		addKeyValue(jsObject, "tooltips",
				"{callbacks:{label:function(a,t){return t.datasets[a.datasetIndex].label+\" : \"+Math.round(100*a.yLabel)/100}}}");
		return jsObject.toString();
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

		FileWriter out = new FileWriter(path);
		try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(range.toArray(new String[0])))) {
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
		List<String> rangeDates = groupBy.getRangeString(dateStart, dateEnd);
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		Map<T, List<Double>> means = dataSet.getMeans(groupBy, listParticipants.getItems(), selecteds, dateStart,
				dateEnd);

		Map<EnrolledUser, Map<T, List<Long>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, selecteds,
				dateStart, dateEnd);

		List<Double> listMeans = createMeanList(selecteds, means, rangeDates);
		for (EnrolledUser selectedUser : enrolledUsers) {
			Map<T, List<Long>> types = userCounts.get(selectedUser);
			List<Double> results = new ArrayList<>();
			long result = 0;
			for (int j = 0; j < rangeDates.size(); j++) {

				for (T type : selecteds) {
					List<Long> times = types.get(type);
					result += times.get(j);
				}
				results.add(result - listMeans.get(j));

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

		FileWriter out = new FileWriter(path);
		try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(range.toArray(new String[0])))) {
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
		Map<EnrolledUser, Map<T, List<Long>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, selecteds,
				dateStart, dateEnd);
		Map<T, List<Double>> means = dataSet.getMeans(groupBy, listParticipants.getItems(), selecteds, dateStart,
				dateEnd);
		for (EnrolledUser selectedUser : enrolledUsers) {
			Map<T, List<Long>> types = userCounts.get(selectedUser);

			for (T type : selecteds) {
				List<Long> times = types.get(type);
				List<Double> meanTimes = means.get(type);
				printer.print(selectedUser.getId());
				printer.print(selectedUser.getFullName());
				printer.print(type);
				long sum = 0;
				double meanSum = 0;
				for (int i = 0; i < times.size(); i++) {
					sum += times.get(i);
					meanSum += meanTimes.get(i);
					printer.print(sum - meanSum);
				}
				printer.println();

			}

		}

	}
}
