package es.ubu.lsi.ubumonitor.controllers.charts;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSet;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetComponent;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetComponentEvent;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetSection;
import es.ubu.lsi.ubumonitor.controllers.datasets.DatasSetCourseModule;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.GroupByAbstract;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.UtilMethods;

public abstract class ChartjsLog extends Chartjs {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChartjsLog.class);

	private String max;

	public ChartjsLog(MainController mainController, ChartType chartType) {
		super(mainController, chartType, Tabs.LOGS);

	}

	public JSArray createLabels(List<String> rangeDates) {
		JSArray labels = new JSArray();
		for (String date : rangeDates) {
			labels.add("'" + UtilMethods.escapeJavaScriptText(date) + "'");
		}
		return labels;
	}

	@Override
	public void update() {
		String dataset = null;
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		List<EnrolledUser> enrolledUsers = new ArrayList<>(listParticipants.getItems());

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();

		if (tabUbuLogsComponent.isSelected()) {

			dataset = createData(enrolledUsers, selectedUsers,
					listViewComponents.getSelectionModel().getSelectedItems(), choiceBoxDate.getValue(), dateStart,
					dateEnd, DataSetComponent.getInstance());
		} else if (tabUbuLogsEvent.isSelected()) {

			dataset = createData(enrolledUsers, selectedUsers, listViewEvents.getSelectionModel().getSelectedItems(),
					choiceBoxDate.getValue(), dateStart, dateEnd, DataSetComponentEvent.getInstance());
		} else if (tabUbuLogsSection.isSelected()) {

			dataset = createData(enrolledUsers, selectedUsers, listViewSection.getSelectionModel().getSelectedItems(),
					choiceBoxDate.getValue(), dateStart, dateEnd, DataSetSection.getInstance());
		} else if (tabUbuLogsCourseModule.isSelected()) {

			dataset = createData(enrolledUsers, selectedUsers,
					listViewCourseModule.getSelectionModel().getSelectedItems(), choiceBoxDate.getValue(), dateStart,
					dateEnd, DatasSetCourseModule.getInstance());
		}

		String options = getOptions();
		LOGGER.info("Dataset en JS: {}", dataset);
		LOGGER.info("Opciones para el stacked bar en JS: {}", options);
		webViewChartsEngine.executeScript(String.format("updateChartjs(%s,%s)", dataset, options));

	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public abstract <T> String createData(List<EnrolledUser> enrolledUsers, List<EnrolledUser> selectedUsers,
			List<T> typeLogs, GroupByAbstract<?> groupBy, LocalDate dateStart, LocalDate dateEnd, DataSet<T> dataSet);

}
