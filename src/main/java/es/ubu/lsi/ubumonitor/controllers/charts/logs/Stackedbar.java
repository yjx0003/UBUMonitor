package es.ubu.lsi.ubumonitor.controllers.charts.logs;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.charts.ChartType;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSet;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetComponent;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetComponentEvent;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetSection;
import es.ubu.lsi.ubumonitor.controllers.datasets.DatasSetCourseModule;
import es.ubu.lsi.ubumonitor.controllers.datasets.StackedBarDataSet;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.GroupByAbstract;
import es.ubu.lsi.ubumonitor.model.Component;
import es.ubu.lsi.ubumonitor.model.ComponentEvent;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.Section;
import es.ubu.lsi.ubumonitor.util.JSObject;

public class Stackedbar extends ChartjsLog {
	private static final Logger LOGGER = LoggerFactory.getLogger(Stackedbar.class);

	private StackedBarDataSet<Component> stackedBarComponent = new StackedBarDataSet<>();
	private StackedBarDataSet<ComponentEvent> stackedBarEvent = new StackedBarDataSet<>();
	private StackedBarDataSet<Section> stackedBarSection = new StackedBarDataSet<>();
	private StackedBarDataSet<CourseModule> stackedBarCourseModule = new StackedBarDataSet<>();

	public Stackedbar(MainController mainController) {
		super(mainController, ChartType.STACKED_BAR);
		useLegend = true;
		useGroupBy = true;
	}

	@Override
	public void update() {
		String stackedbardataset = null;
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		List<EnrolledUser> enrolledUsers = new ArrayList<>(listParticipants.getItems());

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();

		if (tabUbuLogsComponent.isSelected()) {

			stackedbardataset = stackedBarComponent.createData(enrolledUsers, selectedUsers,
					listViewComponents.getSelectionModel().getSelectedItems(), choiceBoxDate.getValue(), dateStart,
					dateEnd, DataSetComponent.getInstance());

		} else if (tabUbuLogsEvent.isSelected()) {
			stackedbardataset = stackedBarEvent.createData(enrolledUsers, selectedUsers,
					listViewEvents.getSelectionModel().getSelectedItems(), choiceBoxDate.getValue(), dateStart, dateEnd,
					DataSetComponentEvent.getInstance());

		} else if (tabUbuLogsSection.isSelected()) {
			stackedbardataset = stackedBarSection.createData(enrolledUsers, selectedUsers,
					listViewSection.getSelectionModel().getSelectedItems(), choiceBoxDate.getValue(), dateStart,
					dateEnd, DataSetSection.getInstance());

		} else if (tabUbuLogsCourseModule.isSelected()) {
			stackedbardataset = stackedBarCourseModule.createData(enrolledUsers, selectedUsers,
					listViewCourseModule.getSelectionModel().getSelectedItems(), choiceBoxDate.getValue(), dateStart,
					dateEnd, DatasSetCourseModule.getInstance());
		}

		String options = getOptions();
		LOGGER.info("Dataset para el stacked bar en JS: {}", stackedbardataset);
		LOGGER.info("Opciones para el stacked bar en JS: {}", options);

		webViewChartsEngine.executeScript("updateChartjs(" + stackedbardataset + "," + options + ")");

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
	public int onClick(int userid) {

		EnrolledUser user = Controller.getInstance().getDataBase().getUsers().getById(userid);
		return listParticipants.getItems().indexOf(user);
	}

	@Override
	public String getOptions() {
		JSObject jsObject = getDefaultOptions();

		long suggestedMax = getSuggestedMax();

		jsObject.putWithQuote("typeGraph", "bar");

		jsObject.put("tooltips",
				"{position:\"nearest\",mode:\"x\",callbacks:{label:function(a,e){return e.datasets[a.datasetIndex].label+\" : \"+Math.round(100*a.yLabel)/100},afterTitle:function(a,e){return e.datasets[a[0].datasetIndex].name}}}");
		jsObject.put("scales", "{yAxes:[{" + getYScaleLabel() + ",stacked:!0,ticks:{suggestedMax:" + suggestedMax
				+ ",stepSize:0}}],xAxes:[{" + getXScaleLabel() + "}]}");
		jsObject.put("legend", "{labels:{filter:function(e,t){return\"line\"==t.datasets[e.datasetIndex].type}}}");
		jsObject.put("onClick",
				"function(t,a){let e=myChart.getElementAtEvent(t)[0];e&&javaConnector.dataPointSelection(myChart.data.datasets[e._datasetIndex].stack)}");
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
		return null;
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
		for (EnrolledUser selectedUser : enrolledUsers) {
			Map<E, List<Integer>> types = userCounts.get(selectedUser);

			for (E type : selecteds) {
				List<Integer> times = types.get(type);
				printer.print(selectedUser.getId());
				printer.print(selectedUser.getFullName());
				printer.print(type.hashCode());
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
		List<String> range = groupBy.getRangeString(dateStart, dateEnd);
		range.add(0, "userid");
		range.add(1, "fullname");
		String selectedTab = mainController.getTabPaneUbuLogs().getSelectionModel().getSelectedItem().getText();
		range.add(2, selectedTab + "_id");
		range.add(3, selectedTab);
		return range.toArray(new String[0]);
	}

}
