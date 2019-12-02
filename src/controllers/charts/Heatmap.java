package controllers.charts;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.JavaConnector.ChartType;
import controllers.MainController;
import controllers.datasets.DataSetComponent;
import controllers.datasets.DataSetComponentEvent;
import controllers.datasets.DataSetSection;
import controllers.datasets.DatasSetCourseModule;
import controllers.datasets.HeatmapDataSet;
import model.Component;
import model.ComponentEvent;
import model.CourseModule;
import model.EnrolledUser;
import model.Section;

public class Heatmap extends Chart {

	private static final Logger LOGGER = LoggerFactory.getLogger(Heatmap.class);

	private HeatmapDataSet<Component> heatmapComponent = new HeatmapDataSet<>();
	private HeatmapDataSet<ComponentEvent> heatmapEvent = new HeatmapDataSet<>();
	private HeatmapDataSet<Section> heatmapSection = new HeatmapDataSet<>();
	private HeatmapDataSet<CourseModule> heatmapCourseModule = new HeatmapDataSet<>();

	public Heatmap(MainController mainController) {
		super(mainController, ChartType.HEAT_MAP);

	}

	public void update() {
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
		LOGGER.info("Dataset para el heatmap en JS: {}", heatmapdataset);
		LOGGER.info("Categorias del heatmap en JS: {}", categories);

		webViewChartsEngine.executeScript(String.format("updateHeatmap(%s,%s)", categories, heatmapdataset));
	}

	public void clear() {

	}

	@Override
	public void hideLegend() {
		// TODO Auto-generated method stub
		
	}

	
}
