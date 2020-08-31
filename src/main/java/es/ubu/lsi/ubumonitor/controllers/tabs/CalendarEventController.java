package es.ubu.lsi.ubumonitor.controllers.tabs;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.WebViewAction;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.view.chart.bridge.CalendarEventConnector;
import es.ubu.lsi.ubumonitor.view.chart.bridge.JavaConnector;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

public class CalendarEventController extends WebViewAction{

	private CalendarEventConnector javaConnector;
	
	@Override
	public void init(MainController mainController, Tab tab, Course actualCourse, MainConfiguration mainConfiguration,
			Stage stage) {
		javaConnector = new CalendarEventConnector(webViewController.getWebViewCharts(), mainConfiguration, mainController, actualCourse);
		init(tab, actualCourse, mainConfiguration, stage, javaConnector);
		
	}
	
	@Override
	public void onWebViewTabChange() {
		updateChart();
		
	}

	@Override
	public void updateListViewEnrolledUser() {
		//do nothing
		
	}

	@Override
	public void updatePredicadeEnrolledList() {
		//do nothing
		
	}

	@Override
	public void applyConfiguration() {
		updateChart();
		
	}
	
	@Override
	public JavaConnector getJavaConnector() {
		return javaConnector;
	}


	@Override
	public void updateListViewCourseModule() {
		updateChart();
	}

}
