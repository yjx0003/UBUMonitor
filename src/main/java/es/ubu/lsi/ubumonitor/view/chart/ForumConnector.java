package es.ubu.lsi.ubumonitor.view.chart;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.view.chart.forum.ForumTable;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebView;

public class ForumConnector extends JavaConnectorAbstract {

	private static final ChartType DEFAULT_CHART = ChartType.DEFAULT_FORUM;

	public ForumConnector(WebView webView, MainConfiguration mainConfiguration, MainController mainController) {
		super(webView, mainConfiguration, mainController);
		
		addChart(new ForumTable(mainController, webView,
				mainController
						.getSelectionMainController()
						.getSelectionForumController()
						.getListViewForum()));
		currentChart = charts.get(DEFAULT_CHART);
	}

	private void addChart(Chart chart) {
		chart.setWebViewChartsEngine(webEngine);
		charts.put(chart.getChartType(), chart);
	}

	public void manageOptions() {
	}
	
	@Override
	public void updateChart() {
		if (webEngine.getLoadWorker()
				.getState() != State.SUCCEEDED) {
			return;
		}
		manageOptions();
		currentChart.update();

	}
	@Override
	public void updateCharts(String typeChart) {
		Chart chart = charts.get(ChartType.valueOf(typeChart));
		if (currentChart.getChartType() != chart.getChartType()) {
			currentChart.clear();
			currentChart = chart;
		}
		manageOptions();
		currentChart.update();
	}




	
	




}
