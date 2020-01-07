package controllers.charts;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

public class Heatmap extends ApexCharts {

	private static final Logger LOGGER = LoggerFactory.getLogger(Heatmap.class);


	public Heatmap(MainController mainController) {
		super(mainController, ChartType.HEAT_MAP);
		useGeneralButton = false;
		useGroupButton = false;
		this.optionsVar = "heatmapOptions";

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

		webViewChartsEngine.executeScript(String.format("updateApexCharts(%s,%s, %s)", heatmapdataset, categories, optionsVar));
	}


	public static <T> String createSeries(List<EnrolledUser> enrolledUsers, List<EnrolledUser> selectedUsers,
			List<T> selecteds, GroupByAbstract<?> groupBy, LocalDate dateStart, LocalDate dateEnd, DataSet<T> dataSet) {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[");

		Map<EnrolledUser, Map<T, List<Long>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, selecteds,
				dateStart, dateEnd);
		List<String> rangeDates = groupBy.getRangeString(dateStart, dateEnd);

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
	public String getMax() {
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
	
}
