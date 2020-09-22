package es.ubu.lsi.ubumonitor.controllers.tabs;

import java.time.LocalDate;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.WebViewAction;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.view.chart.bridge.ForumConnector;
import es.ubu.lsi.ubumonitor.view.chart.bridge.JavaConnector;
import javafx.fxml.FXML;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ForumController extends WebViewAction {

	private ForumConnector javaConnector;
	@FXML
	private GridPane dateGridPane;
	@FXML
	private DatePicker datePickerStart;
	@FXML
	private DatePicker datePickerEnd;

	@Override
	public void init(MainController mainController, Tab tab, Course actualCourse, MainConfiguration mainConfiguration,
			Stage stage) {
		javaConnector = new ForumConnector(webViewController.getWebViewCharts(), mainConfiguration, mainController,
				actualCourse, dateGridPane, datePickerStart, datePickerEnd);
		init(tab, actualCourse, mainConfiguration, stage, javaConnector);
		initOptions(mainController.getWebViewTabsController()
				.getVisualizationController()
				.getDatePickerStart(),
				mainController.getWebViewTabsController()
						.getVisualizationController()
						.getDatePickerEnd());
	}

	private void initOptions(DatePicker visualizationDatePickerStart, DatePicker visualizationDatePickerEnd) {
		datePickerStart.setValue(actualCourse.getStart());
		datePickerEnd.setValue(actualCourse.getEnd());

		datePickerStart.setOnAction(event -> updateChart());
		datePickerEnd.setOnAction(event -> updateChart());

		datePickerStart.setDayCellFactory(picker -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				setDisable(empty || date.isAfter(datePickerEnd.getValue()));
			}
		});
		datePickerEnd.setDayCellFactory(picker -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				setDisable(empty || date.isBefore(datePickerStart.getValue()) || date.isAfter(LocalDate.now()));
			}
		});
		datePickerStart.valueProperty()
				.bindBidirectional(visualizationDatePickerStart.valueProperty());
		datePickerEnd.valueProperty()
				.bindBidirectional(visualizationDatePickerEnd.valueProperty());

	}

	@Override
	public void onWebViewTabChange() {

		javaConnector.updateOptionsImages();
		javaConnector.updateChart();

	}

	@Override
	public void updateListViewEnrolledUser() {
		updateChart();

	}

	@Override
	public void updatePredicadeEnrolledList() {
		updateChart();
	}

	@Override
	public void applyConfiguration() {
		updateChart();

	}

	@Override
	public void updateListViewForum() {
		updateChart();
	}

	@Override
	public JavaConnector getJavaConnector() {
		return javaConnector;
	}

	public GridPane getDateGridPane() {
		return dateGridPane;
	}

	public DatePicker getDatePickerStart() {
		return datePickerStart;
	}

	public DatePicker getDatePickerEnd() {
		return datePickerEnd;
	}

}
