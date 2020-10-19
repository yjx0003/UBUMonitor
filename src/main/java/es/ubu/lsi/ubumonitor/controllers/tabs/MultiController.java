package es.ubu.lsi.ubumonitor.controllers.tabs;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.WebViewAction;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.view.chart.bridge.JavaConnector;
import es.ubu.lsi.ubumonitor.view.chart.bridge.MultiConnector;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MultiController extends WebViewAction {

	private MultiConnector javaConnector;
	@FXML
	private GridPane dateGridPane;
	@FXML
	private DatePicker datePickerStart;
	@FXML
	private DatePicker datePickerEnd;

	@Override
	public void init(MainController mainController, Tab tab, Course actualCourse, MainConfiguration mainConfiguration,
			Stage stage) {
		
		javaConnector = new MultiConnector(webViewController.getWebViewCharts(), mainConfiguration, mainController,
				actualCourse, dateGridPane, datePickerStart, datePickerEnd);
		init(tab, actualCourse, mainConfiguration, stage, javaConnector);
		mainController.getWebViewTabsController()
				.getVisualizationController()
				.bindDatePicker(this, datePickerStart, datePickerEnd);
	}

	@Override
	public void onWebViewTabChange() {
		updateChart();

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
	public void updateTreeViewGradeItem() {
		updateChart();
	}
	
	@Override
	public void updateListViewActivity() {
		updateChart();

	}
	
	@Override
	public void onSetTabLogs() {
		updateChart();
		

	}

	

	@Override
	public void onSetTabGrades() {
		updateChart();

	}

	@Override
	public void onSetTabActivityCompletion() {
		updateChart();
	}

	@Override
	public void onSetSubTabLogs() {
		updateChart();

	}

	@Override
	public void updateListViewComponents() {
		updateChart();

	}

	@Override
	public void updateListViewEvents() {
		updateChart();

	}

	@Override
	public void updateListViewSection() {
		updateChart();

	}

	@Override
	public void updateListViewCourseModule() {
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
