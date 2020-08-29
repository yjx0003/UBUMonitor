package es.ubu.lsi.ubumonitor.view.chart.bridge;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Tabs;
import es.ubu.lsi.ubumonitor.view.chart.calendarevents.CalendarEventTimeline;
import javafx.scene.web.WebView;

public class CalendarEventConnector extends JavaConnectorAbstract {

	public CalendarEventConnector(WebView webView, MainConfiguration mainConfiguration, MainController mainController,
			Course actualCourse) {
		super(webView, mainConfiguration, mainController, actualCourse);
		addChart(new CalendarEventTimeline(mainController, webView, mainController.getSelectionMainController()
				.getSelectionCourseModuleController()
				.getListView()));
		currentChart = charts.get(ChartType.getDefault(Tabs.CALENDAR_EVENT));
	}

}
