package es.ubu.lsi.ubumonitor.controllers.charts;

import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

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
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;

public class CumLine extends ChartjsLog {

	public CumLine(MainController mainController) {
		super(mainController, ChartType.CUM_LINE);
		useGeneralButton = true;
		useLegend = true;
		useGroupBy = true;
	}

	@Override
	public <E> String createData( List<E> typeLogs, DataSet<E> dataSet) {

		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		List<EnrolledUser> enrolledUsers = new ArrayList<>(listParticipants.getItems());

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, typeLogs,
				dateStart, dateEnd);

		Map<E, List<Double>> means = dataSet.getMeans(groupBy, enrolledUsers, typeLogs, dateStart, dateEnd);

		List<String> rangeDates = groupBy.getRangeString(dateStart, dateEnd);

		JSObject data = new JSObject();

		data.put("labels", createLabels(rangeDates));

		JSArray datasets = new JSArray();

		createEnrolledUsersDatasets(selectedUsers, typeLogs, userCounts, rangeDates, datasets);

		createMean(typeLogs, means, rangeDates, datasets);

		data.put("datasets", datasets);

		return data.toString();
	}

	private <E> void createMean(List<E> typeLogs, Map<E, List<Double>> means, List<String> rangeDates,
			JSArray datasets) {
		JSObject dataset = new JSObject();
		String generalMeanTranslate = I18n.get("chartlabel.generalMean");
		dataset.putWithQuote("label", generalMeanTranslate);
		dataset.put("borderColor", hex(generalMeanTranslate));
		dataset.put("backgroundColor", rgba(generalMeanTranslate, OPACITY));
		dataset.put("borderDash", "["
				+ Controller.getInstance().getMainConfiguration().getValue(MainConfiguration.GENERAL, "borderLength")
				+ ","
				+ Controller.getInstance().getMainConfiguration().getValue(MainConfiguration.GENERAL, "borderSpace")
				+ "]");
		dataset.put("hidden", !(boolean) Controller.getInstance().getMainConfiguration()
				.getValue(MainConfiguration.GENERAL, "generalActive"));
		JSArray results = new JSArray();
		double cumResult = 0;
		for (int j = 0; j < rangeDates.size(); j++) {
			double result = 0;
			for (E typeLog : typeLogs) {
				List<Double> times = means.get(typeLog);
				result += times.get(j);
			}
			cumResult += result;
			results.add(cumResult);
		}
		dataset.put("data", results);
		datasets.add(dataset);

	}

	private <E> void createEnrolledUsersDatasets(List<EnrolledUser> selectedUsers, List<E> typeLogs,
			Map<EnrolledUser, Map<E, List<Integer>>> userCounts, List<String> rangeDates, JSArray datasets) {

		for (EnrolledUser selectedUser : selectedUsers) {
			JSObject dataset = new JSObject();
			dataset.putWithQuote("label", selectedUser.getFullName());
			dataset.put("borderColor", hex(selectedUser.getId()));
			dataset.put("backgroundColor", rgba(selectedUser.getId(), OPACITY));

			Map<E, List<Integer>> types = userCounts.get(selectedUser);
			JSArray results = new JSArray();
			long result = 0;
			for (int j = 0; j < rangeDates.size(); j++) {

				for (E typeLog : typeLogs) {
					List<Integer> times = types.get(typeLog);
					result += times.get(j);
				}

				results.add(Long.toString(result));
			}
			dataset.put("data", results);
			datasets.add(dataset);

		}

	}

	@Override
	public String calculateMax() {
		long maxYAxis = 1L;
		if (tabUbuLogsComponent.isSelected()) {
			maxYAxis = choiceBoxDate.getValue().getComponents().getCumulativeMax(listParticipants.getItems(),
					listViewComponents.getSelectionModel().getSelectedItems(), datePickerStart.getValue(),
					datePickerEnd.getValue());
		} else if (tabUbuLogsEvent.isSelected()) {
			maxYAxis = choiceBoxDate.getValue().getComponentsEvents().getCumulativeMax(listParticipants.getItems(),
					listViewEvents.getSelectionModel().getSelectedItems(), datePickerStart.getValue(),
					datePickerEnd.getValue());
		} else if (tabUbuLogsSection.isSelected()) {
			maxYAxis = choiceBoxDate.getValue().getSections().getCumulativeMax(listParticipants.getItems(),
					listViewSection.getSelectionModel().getSelectedItems(), datePickerStart.getValue(),
					datePickerEnd.getValue());
		} else if (tabUbuLogsCourseModule.isSelected()) {
			maxYAxis = choiceBoxDate.getValue().getCourseModules().getCumulativeMax(listParticipants.getItems(),
					listViewCourseModule.getSelectionModel().getSelectedItems(), datePickerStart.getValue(),
					datePickerEnd.getValue());
		}
		return Long.toString(maxYAxis);
	}

	@Override
	public String getOptions() {
		JSObject jsObject = getDefaultOptions();
		jsObject.putWithQuote("typeGraph", "line");

		jsObject.put("scales", "{yAxes:[{" + getYScaleLabel() + ",ticks:{suggestedMax:" + getSuggestedMax()
				+ ",stepSize:0}}],xAxes:[{" + getXScaleLabel() + "}]}");
		jsObject.put("tooltips",
				"{callbacks:{label:function(a,t){return t.datasets[a.datasetIndex].label+' : '+Math.round(100*a.yLabel)/100}}}");
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

	private <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> selecteds) throws IOException {

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<?> rangeDates = groupBy.getRange(dateStart, dateEnd);
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, selecteds,
				dateStart, dateEnd);
		for (EnrolledUser selectedUser : enrolledUsers) {
			Map<E, List<Integer>> types = userCounts.get(selectedUser);
			List<Long> results = new ArrayList<>();
			long result = 0;
			for (int j = 0; j < rangeDates.size(); j++) {

				for (E type : selecteds) {
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

	private <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> selecteds) throws IOException {

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, selecteds,
				dateStart, dateEnd);
		for (EnrolledUser selectedUser : enrolledUsers) {
			Map<E, List<Integer>> types = userCounts.get(selectedUser);

			for (E type : selecteds) {
				List<Integer> times = types.get(type);
				printer.print(selectedUser.getId());
				printer.print(selectedUser.getFullName());
				printer.print(type);
				long sum = 0;
				for (long result : times) {
					sum += result;
					printer.print(sum);
				}
				printer.println();
			}

		}

	}
}
