package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.awt.Color;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;

public class Stackedbar extends ChartjsLog {

	public Stackedbar(MainController mainController) {
		super(mainController, ChartType.STACKED_BAR);
		useLegend = true;
		useGroupBy = true;
	}

	@Override
	public String calculateMax() {

		long maxYAxis = 1L;
		List<EnrolledUser> users = getUsers();
		if (tabComponent.isSelected()) {
			maxYAxis = choiceBoxDate.getValue().getComponents().getMaxElement(users,
					listViewComponent.getSelectionModel().getSelectedItems(), datePickerStart.getValue(),
					datePickerEnd.getValue());
		} else if (tabEvent.isSelected()) {
			maxYAxis = choiceBoxDate.getValue().getComponentsEvents().getMaxElement(users,
					listViewEvent.getSelectionModel().getSelectedItems(), datePickerStart.getValue(),
					datePickerEnd.getValue());
		} else if (tabSection.isSelected()) {
			maxYAxis = choiceBoxDate.getValue().getSections().getMaxElement(users,
					listViewSection.getSelectionModel().getSelectedItems(), datePickerStart.getValue(),
					datePickerEnd.getValue());
		} else if (tabCourseModule.isSelected()) {
			maxYAxis = choiceBoxDate.getValue().getCourseModules().getMaxElement(users,
					listViewCourseModule.getSelectionModel().getSelectedItems(), datePickerStart.getValue(),
					datePickerEnd.getValue());
		}
		return Long.toString(maxYAxis);
	}

	@Override
	public int onClick(int userid) {

		EnrolledUser user = Controller.getInstance().getDataBase().getUsers().getById(userid);
		return getUsers().indexOf(user);
	}

	@Override
	public String getOptions(JSObject jsObject) {

		long suggestedMax = getSuggestedMax(textFieldMax.getText());

		jsObject.putWithQuote("typeGraph", "bar");

		jsObject.put("tooltips",
				"{position:\"nearest\",mode:\"x\",callbacks:{label:function(a,e){return e.datasets[a.datasetIndex].label+\" : \"+Math.round(100*a.yLabel)/100},afterTitle:function(a,e){return e.datasets[a[0].datasetIndex].name}}}");
		jsObject.put("scales", "{yAxes:[{" + getYScaleLabel() + ",stacked:!0,ticks:{suggestedMax:" + suggestedMax
				+ ",stepSize:0}}],xAxes:[{" + getXScaleLabel() + "}]}");
		jsObject.put("legend", "{labels:{filter:function(e,t){return\"line\"==t.datasets[e.datasetIndex].type}}}");
		jsObject.put("onClick",
				"function(t,a){let e=myChart.getElementAtEvent(t)[0];e&&javaConnector.dataPointSelection(myChart.data.datasets[e._datasetIndex].stack)}");

		jsObject.put("elements", "{line:{fill:!1}}");
		return jsObject.toString();
	}

	@Override
	public String getXAxisTitle() {
		return MessageFormat.format(I18n.get(getChartType() + ".xAxisTitle"),
				I18n.get(choiceBoxDate.getValue().getTypeTime()));
	}

	protected <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> selecteds) throws IOException {

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
			for (int j = 0; j < rangeDates.size(); j++) {
				long result = 0;
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
	public <E> String createData(List<E> typeLogs, DataSet<E> dataSet) {
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		List<EnrolledUser> enrolledUsers = getUsers();

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, typeLogs,
				dateStart, dateEnd);
		
		Map<E, Color> colors = UtilMethods.getRandomColors(typeLogs);
 
		Map<E, List<Double>> means = dataSet.getMeans(groupBy, enrolledUsers, typeLogs, dateStart, dateEnd);

		JSObject data = new JSObject();
		data.put("labels", "[" + UtilMethods.joinWithQuotes(groupBy.getRangeString(dateStart, dateEnd)) + "]");
		JSArray datasets = new JSArray();
		

		for (E typeLog : typeLogs) {

			List<Double> datas = means.get(typeLog);

			boolean anyNotZero = datas.stream().anyMatch(value -> value != 0.0);

			if (anyNotZero) {
				JSObject dataset = new JSObject();

				dataset.putWithQuote("label", I18n.get("chart.mean") + " " + dataSet.translate(typeLog));
				dataset.put("type", "'line'");
				Color c = colors.get(typeLog);
				int r = c.getRed();
				int g = c.getGreen();
				int b = c.getBlue();
			
				dataset.put("backgroundColor", String.format("'rgba(%d,%d,%d,0.4)'", r,g,b));
				dataset.put("borderColor", String.format("'#%02x%02x%02x'", r, g, b));
				dataset.put("data", "[" + UtilMethods.join(datas) + "]");
				datasets.add(dataset);
			}
		}

		for (EnrolledUser user : selectedUsers) {

			Map<E, List<Integer>> elementDataset = userCounts.get(user);
			for (E typeLog : typeLogs) {

				List<Integer> datas = elementDataset.get(typeLog);

				boolean anyNotZero = datas.stream().anyMatch(value -> value != 0);

				if (anyNotZero) {
					JSObject dataset = new JSObject();
					dataset.putWithQuote("label", dataSet.translate(typeLog));
					dataset.putWithQuote("name", user);
					dataset.put("stack", user.getId());
					Color c = colors.get(typeLog);
					int r = c.getRed();
					int g = c.getGreen();
					int b = c.getBlue();
		
					dataset.put("backgroundColor", String.format("'rgba(%d,%d,%d,0.4)'", r,g,b));
					dataset.put("borderColor", String.format("'#%02x%02x%02x'", r, g, b));
					dataset.put("data", "[" + UtilMethods.join(datas) + "]");
					datasets.add(dataset);
				}

			}
		}
		data.put("datasets", datasets);
		return data.toString();
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
	protected <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> selecteds)
			throws IOException {
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, selecteds,
				dateStart, dateEnd);
		boolean hasId = hasId();
		for (EnrolledUser selectedUser : enrolledUsers) {
			Map<E, List<Integer>> types = userCounts.get(selectedUser);

			for (E type : selecteds) {
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
		String selectedTab = tabPaneSelection.getSelectionModel().getSelectedItem().getText();
		if (hasId()) {
			list.add(selectedTab + "_id");
		}
		list.add(selectedTab);
		list.addAll(groupBy.getRangeString(dateStart, dateEnd));
		return list.toArray(new String[0]);
	}

}
