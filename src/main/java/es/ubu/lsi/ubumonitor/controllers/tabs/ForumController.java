package es.ubu.lsi.ubumonitor.controllers.tabs;

import es.ubu.lsi.ubumonitor.controllers.DateController;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.WebViewAction;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.view.chart.bridge.ForumConnector;
import es.ubu.lsi.ubumonitor.view.chart.bridge.JavaConnector;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ForumController extends WebViewAction {

	private ForumConnector javaConnector;
	
	@FXML
	private DateController dateController;

	@Override
	public void init(MainController mainController, Tab tab, Course actualCourse, MainConfiguration mainConfiguration,
			Stage stage) {
		javaConnector = new ForumConnector(webViewController.getWebViewCharts(), mainConfiguration, mainController,
				actualCourse, getDateGridPane(), getDatePickerStart(), getDatePickerEnd());
		init(tab, actualCourse, mainConfiguration, stage, javaConnector);
		mainController.getWebViewTabsController()
				.getVisualizationController()
				.bindDatePicker(this, getDatePickerStart(), getDatePickerEnd());
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
		return dateController.getDateGridPane();
	}

	public DatePicker getDatePickerStart() {
		return dateController.getDatePickerStart();
	}

	public DatePicker getDatePickerEnd() {
		return dateController.getDatePickerEnd();
	}

}
