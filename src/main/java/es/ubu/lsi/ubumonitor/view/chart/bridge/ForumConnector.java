package es.ubu.lsi.ubumonitor.view.chart.bridge;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Tabs;
import es.ubu.lsi.ubumonitor.view.chart.forum.ForumBar;
import es.ubu.lsi.ubumonitor.view.chart.forum.ForumNetwork;
import es.ubu.lsi.ubumonitor.view.chart.forum.ForumTable;
import es.ubu.lsi.ubumonitor.view.chart.forum.ForumUserPostBar;
import es.ubu.lsi.ubumonitor.view.chart.forum.ForumWordCloud;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;

public class ForumConnector extends JavaConnectorAbstract {

	
	public ForumConnector(WebView webView, MainConfiguration mainConfiguration, MainController mainController, Course actualCourse) {
		super(webView, mainConfiguration, mainController, actualCourse);
		ListView<CourseModule> listViewForum = mainController
				.getSelectionMainController()
				.getSelectionForumController()
				.getListViewForum();
		addChart(new ForumTable(mainController, webView,
				listViewForum));
		addChart(new ForumBar(mainController, listViewForum));
		
		addChart(new ForumNetwork(mainController, webView, listViewForum));
		addChart(new ForumWordCloud(mainController, listViewForum));
		addChart(new ForumUserPostBar(mainController, listViewForum));
		currentChart = charts.get(ChartType.getDefault(Tabs.FORUM));
	}

	




}
