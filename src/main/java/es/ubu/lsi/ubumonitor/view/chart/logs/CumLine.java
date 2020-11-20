package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;

public class CumLine extends ChartjsLog {

	public CumLine(MainController mainController) {
		super(mainController, ChartType.CUM_LINE);
		useGeneralButton = true;
		useLegend = true;
		useGroupBy = true;
	}

	@Override
	public <E> String createData(List<E> typeLogs, DataSet<E> dataSet) {

		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		List<EnrolledUser> enrolledUsers = getUsers();

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
		dataset.put("borderDash", "[" + mainConfiguration.getValue(MainConfiguration.GENERAL, "borderLength") + ","
				+ mainConfiguration.getValue(MainConfiguration.GENERAL, "borderSpace") + "]");
		dataset.put("hidden", !(boolean) mainConfiguration.getValue(MainConfiguration.GENERAL, "generalActive"));
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
		List<EnrolledUser> users = getUsers();
		if (tabComponent.isSelected()) {
			maxYAxis = choiceBoxDate.getValue()
					.getComponents()
					.getCumulativeMax(users, listViewComponent.getSelectionModel()
							.getSelectedItems(), datePickerStart.getValue(), datePickerEnd.getValue());
		} else if (tabEvent.isSelected()) {
			maxYAxis = choiceBoxDate.getValue()
					.getComponentsEvents()
					.getCumulativeMax(users, listViewEvent.getSelectionModel()
							.getSelectedItems(), datePickerStart.getValue(), datePickerEnd.getValue());
		} else if (tabSection.isSelected()) {
			maxYAxis = choiceBoxDate.getValue()
					.getSections()
					.getCumulativeMax(users, listViewSection.getSelectionModel()
							.getSelectedItems(), datePickerStart.getValue(), datePickerEnd.getValue());
		} else if (tabCourseModule.isSelected()) {
			maxYAxis = choiceBoxDate.getValue()
					.getCourseModules()
					.getCumulativeMax(users, listViewCourseModule.getSelectionModel()
							.getSelectedItems(), datePickerStart.getValue(), datePickerEnd.getValue());
		}
		return Long.toString(maxYAxis);
	}

	@Override
	public void fillOptions(JSObject jsObject) {

		jsObject.putWithQuote("typeGraph", "line");

		jsObject.put("scales", "{yAxes:[{" + getYScaleLabel() + ",ticks:{suggestedMax:"
				+ getSuggestedMax(textFieldMax.getText()) + ",stepSize:0}}],xAxes:[{" + getXScaleLabel() + "}]}");
		jsObject.put("tooltips",
				"{callbacks:{label:function(a,t){return t.datasets[a.datasetIndex].label+' : '+Math.round(100*a.yLabel)/100}}}");
		
	}

	@Override
	public String getXAxisTitle() {
		return MessageFormat.format(I18n.get(getChartType() + ".xAxisTitle"), I18n.get(choiceBoxDate.getValue()
				.getTypeTime()));
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
			List<Long> results = new ArrayList<>();
			long result = 0;
			for (int j = 0; j < rangeDates.size(); j++) {

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
				long sum = 0;
				for (long result : times) {
					sum += result;
					printer.print(sum);
				}
				printer.println();
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
