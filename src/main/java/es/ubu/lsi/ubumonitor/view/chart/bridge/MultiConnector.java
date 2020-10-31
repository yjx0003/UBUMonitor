package es.ubu.lsi.ubumonitor.view.chart.bridge;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.controllers.tabs.MultiController;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Tabs;
import es.ubu.lsi.ubumonitor.view.chart.multi.BubbleComparison;
import es.ubu.lsi.ubumonitor.view.chart.multi.RankingTable;
import javafx.scene.web.WebView;

public class MultiConnector extends JavaConnectorAbstract {

	private MultiController multiController;

	public MultiConnector(WebView webViewCharts, MainConfiguration mainConfiguration, MainController mainController,
			Course actualCourse, MultiController multiController) {
		super(webViewCharts, mainConfiguration, mainController, actualCourse);
		this.multiController = multiController;
		addChart(new RankingTable(mainController, webView, mainController.getSelectionController()
				.getTvwGradeReport(),
				mainController.getSelectionController()
						.getListViewActivity(),
				multiController.getDatePickerStart(), multiController.getDatePickerEnd()));
		addChart(new BubbleComparison(mainController, webView, mainController.getSelectionController()
				.getTvwGradeReport(),
				mainController.getSelectionController()
						.getListViewActivity(),
				multiController.getChoiceBoxDate(), multiController.getDatePickerStart(),
				multiController.getDatePickerEnd()));
		currentChart = charts.get(ChartType.getDefault(Tabs.MULTI));
	}

	@Override
	public void manageOptions() {
		multiController.getGridPaneOptionLogs()
				.setVisible(currentChart.isUseGroupBy());
		multiController.getDateGridPane()
				.setVisible(currentChart.isUseRangeDate());

	}

}
