package es.ubu.lsi.ubumonitor.view.chart.bridge;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Tabs;
import es.ubu.lsi.ubumonitor.view.chart.multi.RankingTable;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;

public class MultiConnector extends JavaConnectorAbstract {

	public MultiConnector(WebView webViewCharts, MainConfiguration mainConfiguration, MainController mainController,
			Course actualCourse, GridPane dateGridPane, DatePicker datePickerStart, DatePicker datePickerEnd) {
		super(webViewCharts, mainConfiguration, mainController, actualCourse);
		addChart(new RankingTable(mainController, webView, mainController.getSelectionController()
				.getTvwGradeReport(),
				mainController.getSelectionController()
						.getListViewActivity(),
				datePickerStart, datePickerEnd));
		currentChart = charts.get(ChartType.getDefault(Tabs.MULTI));
	}

}
