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
import es.ubu.lsi.ubumonitor.view.chart.forum.ForumTreeMap;
import es.ubu.lsi.ubumonitor.view.chart.forum.ForumTreeMapUser;
import es.ubu.lsi.ubumonitor.view.chart.forum.ForumUserPostBar;
import es.ubu.lsi.ubumonitor.view.chart.forum.ForumWordCloud;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;

public class ForumConnector extends JavaConnectorAbstract {
	
	private GridPane dateGridPane;
	
	public ForumConnector(WebView webView, MainConfiguration mainConfiguration, MainController mainController, Course actualCourse, GridPane dateGridPane, DatePicker datePickerStart, DatePicker datePickerEnd) {
		super(webView, mainConfiguration, mainController, actualCourse);
		this.dateGridPane = dateGridPane;
		ListView<CourseModule> listViewForum = mainController
				.getSelectionMainController()
				.getSelectionForumController()
				.getListViewForum();
		addChart(new ForumTable(mainController, webView,
				listViewForum, datePickerStart, datePickerEnd));
		addChart(new ForumBar(mainController, listViewForum,datePickerStart, datePickerEnd));
		
		addChart(new ForumNetwork(mainController, webView, listViewForum,datePickerStart, datePickerEnd));
		addChart(new ForumWordCloud(mainController, listViewForum, webView,datePickerStart, datePickerEnd));
		addChart(new ForumUserPostBar(mainController, listViewForum,datePickerStart, datePickerEnd));
		addChart(new ForumTreeMap(mainController, webView, listViewForum,datePickerStart, datePickerEnd));
		addChart(new ForumTreeMapUser(mainController, webView, listViewForum,datePickerStart, datePickerEnd));
		
		currentChart = charts.get(ChartType.getDefault(Tabs.FORUM));
	}

	

	@Override
	public void manageOptions() {
		dateGridPane.setVisible(currentChart.isUseRangeDate());
	}


}
