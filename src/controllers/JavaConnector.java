package controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.datasets.DataSetComponent;
import controllers.datasets.DataSetComponentEvent;
import controllers.datasets.DataSetSection;
import controllers.datasets.DatasSetCourseModule;
import controllers.datasets.heatmap.HeatmapDataSet;
import controllers.datasets.stackedbar.StackedBarDataSet;
import controllers.ubulogs.GroupByAbstract;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.web.WebEngine;
import model.Component;
import model.ComponentEvent;
import model.CourseModule;
import model.EnrolledUser;
import model.Section;

public class JavaConnector {

	private static final Logger LOGGER = LoggerFactory.getLogger(JavaConnector.class);

	private StackedBarDataSet<Component> stackedBarComponent;
	private StackedBarDataSet<ComponentEvent> stackedBarEvent;
	private StackedBarDataSet<Section> stackedBarSection;
	private StackedBarDataSet<CourseModule> stackedBarCourseModule;

	private HeatmapDataSet<Component> heatmapComponent;
	private HeatmapDataSet<ComponentEvent> heatmapEvent;
	private HeatmapDataSet<Section> heatmapSection;
	private HeatmapDataSet<CourseModule> heatmapCourseModule;

	private WebEngine webViewChartsEngine;

	private ListView<EnrolledUser> listParticipants;

	private Tab tabUbuLogs;

	private Tab tabUbuLogsComponent;

	private Tab tabUbuLogsEvent;

	private Tab tabUbuLogsSection;

	private Tab tabUbuLogsCourseModule;

	private ListView<Component> listViewComponents;

	private ListView<ComponentEvent> listViewEvents;

	private ListView<Section> listViewSection;

	private ListView<CourseModule> listViewCourseModule;

	private ChoiceBox<GroupByAbstract<?>> choiceBoxDate;

	private DatePicker datePickerStart;

	private DatePicker datePickerEnd;

	private ChartType currentTypeLogs = ChartType.STACKED_BAR;

	private ChartType currentTypeGrades = ChartType.LINE;


	public JavaConnector() {
		stackedBarComponent = new StackedBarDataSet<>();
		stackedBarEvent = new StackedBarDataSet<>();
		stackedBarSection = new StackedBarDataSet<>();
		stackedBarCourseModule = new StackedBarDataSet<>();

		heatmapComponent = new HeatmapDataSet<>();
		heatmapEvent = new HeatmapDataSet<>();
		heatmapSection = new HeatmapDataSet<>();
		heatmapCourseModule = new HeatmapDataSet<>();
	}

	public JavaConnector(MainController mainController) {
		this();
		this.webViewChartsEngine = mainController.webViewChartsEngine;
		this.listParticipants = mainController.listParticipants;
		this.tabUbuLogs = mainController.tabUbuLogs;
		this.tabUbuLogsComponent = mainController.tabUbuLogsComponent;
		this.tabUbuLogsEvent = mainController.tabUbuLogsEvent;
		this.tabUbuLogsSection = mainController.tabUbuLogsSection;
		this.tabUbuLogsCourseModule = mainController.tabUbuLogsCourseModule;
		this.listViewComponents = mainController.listViewComponents;
		this.listViewEvents = mainController.listViewEvents;
		this.listViewSection = mainController.listViewSection;
		this.listViewCourseModule = mainController.listViewCourseModule;
		this.choiceBoxDate = mainController.choiceBoxDate;
		this.datePickerStart = mainController.datePickerStart;
		this.datePickerEnd = mainController.datePickerEnd;
	}

	public void setCurrentTypeLogs(ChartType chartType) {
		this.currentTypeLogs = chartType;
	}

	public void updateHeatmap() {
		if (!tabUbuLogs.isSelected()) {
			return;
		}
		String heatmapdataset = null;
		List<EnrolledUser> selectedUsers = new ArrayList<>(listParticipants.getSelectionModel().getSelectedItems());
		List<EnrolledUser> enrolledUsers = new ArrayList<>(listParticipants.getItems());
		selectedUsers.removeAll(Collections.singletonList(null));

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();

		if (tabUbuLogsComponent.isSelected()) {

			heatmapdataset = heatmapComponent.createSeries(enrolledUsers, selectedUsers,
					listViewComponents.getSelectionModel().getSelectedItems(), choiceBoxDate.getValue(), dateStart,
					dateEnd, DataSetComponent.getInstance());
		} else if (tabUbuLogsEvent.isSelected()) {

			heatmapdataset = heatmapEvent.createSeries(enrolledUsers, selectedUsers,
					listViewEvents.getSelectionModel().getSelectedItems(), choiceBoxDate.getValue(), dateStart, dateEnd,
					DataSetComponentEvent.getInstance());
		} else if (tabUbuLogsSection.isSelected()) {

			heatmapdataset = heatmapSection.createSeries(enrolledUsers, selectedUsers,
					listViewSection.getSelectionModel().getSelectedItems(), choiceBoxDate.getValue(), dateStart,
					dateEnd, DataSetSection.getInstance());
		} else if (tabUbuLogsCourseModule.isSelected()) {

			heatmapdataset = heatmapCourseModule.createSeries(enrolledUsers, selectedUsers,
					listViewCourseModule.getSelectionModel().getSelectedItems(), choiceBoxDate.getValue(), dateStart,
					dateEnd, DatasSetCourseModule.getInstance());
		}

		String categories = HeatmapDataSet.createCategory(choiceBoxDate.getValue(), dateStart, dateEnd);
		webViewChartsEngine.executeScript(String.format("updateHeatmap(%s,%s)", categories, heatmapdataset));
		currentTypeLogs = ChartType.HEAT_MAP;
	}

	public void updateStackedBar() {
		if (!tabUbuLogs.isSelected()) {
			return;
		}
		String stackedbardataset = null;
		List<EnrolledUser> selectedUsers = new ArrayList<>(listParticipants.getSelectionModel().getSelectedItems());
		List<EnrolledUser> enrolledUsers = new ArrayList<>(listParticipants.getItems());
		selectedUsers.removeAll(Collections.singletonList(null));

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
					dateEnd,
					DataSetSection.getInstance());
		} else if (tabUbuLogsCourseModule.isSelected()) {
			stackedbardataset = stackedBarCourseModule.createData(enrolledUsers, selectedUsers,
					listViewCourseModule.getSelectionModel().getSelectedItems(), choiceBoxDate.getValue(), dateStart,
					dateEnd, DatasSetCourseModule.getInstance());
		}

		LOGGER.info("Dataset para el stacked bar en JS: {}", stackedbardataset);
		
		webViewChartsEngine.executeScript("updateStackedChart(" + stackedbardataset+")");
		currentTypeLogs = ChartType.STACKED_BAR;

	}

	public void updateLogsChart() {
		switch (currentTypeLogs) {
		case HEAT_MAP:
			updateHeatmap();
			break;
		case STACKED_BAR:
			updateStackedBar();
			break;
		default:
			throw new IllegalArgumentException("Not avaible log chart for: " + currentTypeLogs);

		}
	}
	
	public void updateGradesChart() {
		
		switch (currentTypeGrades) {
		case GENERAL_BOXPLOT:
			break;
		case GROUP_BOXPLOT:
			break;
		case LINE:
			break;
		case RADAR:
			break;
		case TABLE:
			break;
		default:
			throw new IllegalArgumentException("Not avaible grade chart for: " + currentTypeLogs);
		
		}
	}
	
	
	public enum ChartType {
		HEAT_MAP, STACKED_BAR, LINE, RADAR, GENERAL_BOXPLOT, GROUP_BOXPLOT, TABLE;
	}


	public void updateMaxY(long max) {

	
			webViewChartsEngine.executeScript("changeYMaxHeatmap("+max+")");
	
			webViewChartsEngine.executeScript("changeYMaxStackedBar("+max+")");
		
			
		
	}
	
	


}
