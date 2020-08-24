package es.ubu.lsi.ubumonitor.controllers;

import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.view.chart.Chart;
import es.ubu.lsi.ubumonitor.view.chart.ForumConnector;
import es.ubu.lsi.ubumonitor.view.chart.JavaConnector;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

public class ForumController extends WebViewController {

	private ForumConnector javaConnector;

	@Override
	public void init(MainController mainController, Tab tab, Course actualCourse, MainConfiguration mainConfiguration,
			Stage stage) {
		javaConnector = new ForumConnector(webViewCharts, mainConfiguration, mainController, actualCourse);
		init(tab, actualCourse, mainConfiguration, stage, javaConnector);

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
	public Chart getCurrentChart() {
		return javaConnector.getCurrentChart();
	}

	@Override
	public JavaConnector getJavaConnector() {
		return javaConnector;
	}

}
