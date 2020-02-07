package es.ubu.lsi.ubumonitor.controllers.charts;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSet;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.GroupByAbstract;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;

public class CumLine extends ChartjsLog {

	public CumLine(MainController mainController) {
		super(mainController, ChartType.CUM_LINE);
		useGeneralButton = true;
		useLegend = true;
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

		createEnrolledUsersDatasets(selectedUsers, typeLogs, userCounts, rangeDates, datasets);

		createMean(typeLogs, means, rangeDates, datasets);

		addKeyValue(data, "datasets", datasets);

		return data.toString();
	}

	private <T> void createMean(List<T> typeLogs, Map<T, List<Double>> means, List<String> rangeDates,
			StringJoiner datasets) {
		StringJoiner dataset = JSObject();
		String generalMeanTranslate = I18n.get("chartlabel.generalMean");
		addKeyValueWithQuote(dataset, "label", generalMeanTranslate);
		addKeyValue(dataset, "borderColor", hex(generalMeanTranslate));
		addKeyValue(dataset, "backgroundColor", rgba(generalMeanTranslate, OPACITY));
		addKeyValue(dataset, "borderDash",
				"[" + Controller.getInstance().getMainConfiguration().getValue(MainConfiguration.GENERAL, "borderLength") + ","
						+ Controller.getInstance().getMainConfiguration().getValue(MainConfiguration.GENERAL, "borderSpace") + "]");
		addKeyValue(dataset, "hidden",
				!(boolean) Controller.getInstance().getMainConfiguration().getValue(MainConfiguration.GENERAL, "generalActive"));
		StringJoiner results = JSArray();
		double cumResult = 0;
		for (int j = 0; j < rangeDates.size(); j++) {
			double result = 0;
			for (T typeLog : typeLogs) {
				List<Double> times = means.get(typeLog);
				result += times.get(j);
			}
			cumResult += result;
			results.add(Double.toString(cumResult));
		}
		addKeyValue(dataset, "data", results);
		datasets.add(dataset.toString());

	}

	private <T> void createEnrolledUsersDatasets(List<EnrolledUser> selectedUsers, List<T> typeLogs,
			Map<EnrolledUser, Map<T, List<Long>>> userCounts, List<String> rangeDates, StringJoiner datasets) {

		for (EnrolledUser selectedUser : selectedUsers) {
			StringJoiner dataset = JSObject();
			addKeyValueWithQuote(dataset, "label", selectedUser.getFullName());
			addKeyValue(dataset, "borderColor", hex(selectedUser.getId()));
			addKeyValue(dataset, "backgroundColor", rgba(selectedUser.getId(), OPACITY));

			Map<T, List<Long>> types = userCounts.get(selectedUser);
			StringJoiner results = JSArray();
			long cumResult = 0;
			for (int j = 0; j < rangeDates.size(); j++) {
				long result = 0;
				for (T typeLog : typeLogs) {
					List<Long> times = types.get(typeLog);
					result += times.get(j);
				}
				cumResult += result;

				results.add(Long.toString(cumResult));
			}
			addKeyValue(dataset, "data", results);
			datasets.add(dataset.toString());

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
		StringJoiner jsObject = getDefaultOptions();
		addKeyValueWithQuote(jsObject, "typeGraph", "line");

		addKeyValue(jsObject, "scales", "{yAxes:[{" + getYScaleLabel() + ",ticks:{suggestedMax:" + getSuggestedMax()
				+ ",stepSize:0}}],xAxes:[{" + getXScaleLabel() + "}]}");
		addKeyValue(jsObject, "tooltips",
				"{callbacks:{label:function(a,t){return t.datasets[a.datasetIndex].label+' : '+Math.round(100*a.yLabel)/100}}}");
		return jsObject.toString();
	}
	@Override
	public String getXAxisTitle() {
		return MessageFormat.format(I18n.get(getChartType() + ".xAxisTitle"),
				I18n.get(choiceBoxDate.getValue().getTypeTime()));
	}

	@Override
	public void exportCSV(String path) throws IOException {
		// TODO Auto-generated method stub
		
	}
}
