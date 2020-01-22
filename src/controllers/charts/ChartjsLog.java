package controllers.charts;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.MainController;
import controllers.datasets.DataSet;
import controllers.datasets.DataSetComponent;
import controllers.datasets.DataSetComponentEvent;
import controllers.datasets.DataSetSection;
import controllers.datasets.DatasSetCourseModule;
import controllers.ubulogs.GroupByAbstract;
import model.EnrolledUser;
import util.UtilMethods;

public abstract class ChartjsLog extends Chartjs{
	private static final Logger LOGGER = LoggerFactory.getLogger(ChartjsLog.class);

	public ChartjsLog(MainController mainController, ChartType chartType) {
		super(mainController, chartType, Tabs.LOGS);
		
	}
	
	public StringJoiner createLabels(List<String> rangeDates) {
		StringJoiner labels = JSArray();
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

	public abstract <T> String createData(List<EnrolledUser> enrolledUsers, List<EnrolledUser> selectedUsers, List<T> typeLogs,
			GroupByAbstract<?> groupBy, LocalDate dateStart, LocalDate dateEnd, DataSet<T> dataSet);
}
