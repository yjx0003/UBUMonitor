package controllers.charts;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.Controller;
import controllers.MainController;
import controllers.datasets.DataSetComponent;
import controllers.datasets.DataSetComponentEvent;
import controllers.datasets.DataSetSection;
import controllers.datasets.DatasSetCourseModule;
import controllers.datasets.StackedBarDataSet;
import model.Component;
import model.ComponentEvent;
import model.CourseModule;
import model.EnrolledUser;
import model.Section;

public class Stackedbar extends ChartjsGradeItem {
	private static final Logger LOGGER = LoggerFactory.getLogger(Stackedbar.class);

	private StackedBarDataSet<Component> stackedBarComponent = new StackedBarDataSet<>();
	private StackedBarDataSet<ComponentEvent> stackedBarEvent = new StackedBarDataSet<>();
	private StackedBarDataSet<Section> stackedBarSection = new StackedBarDataSet<>();
	private StackedBarDataSet<CourseModule> stackedBarCourseModule = new StackedBarDataSet<>();

	public Stackedbar(MainController mainController) {
		super(mainController, ChartType.STACKED_BAR);
		useGeneralButton = false;
		useGroupButton = false;
		optionsVar = "stackedbarOptions";

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

		LOGGER.info("Dataset para el stacked bar en JS: {}", stackedbardataset);

		webViewChartsEngine.executeScript(String.format("updateChartjs(%s,%s)", stackedbardataset, "stackedbarOptions"));

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

	@Override
	public int onClick(int userid) {

		EnrolledUser user = Controller.getInstance().getDataBase().getUsers().getById(userid);
		return listParticipants.getItems().indexOf(user);
	}

}
