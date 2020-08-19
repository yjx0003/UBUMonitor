package es.ubu.lsi.ubumonitor.view.chart;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.RiskController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.view.chart.risk.Bubble;
import es.ubu.lsi.ubumonitor.view.chart.risk.BubbleLogarithmic;
import es.ubu.lsi.ubumonitor.view.chart.risk.RiskBar;
import es.ubu.lsi.ubumonitor.view.chart.risk.RiskBarTemporal;
import es.ubu.lsi.ubumonitor.view.chart.risk.RiskEvolution;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebView;

public class RiskJavaConnector extends JavaConnectorAbstract{

	private static final ChartType DEFAULT_CHART = ChartType.DEFAULT_RISK;


	private RiskController riskController;


	public RiskJavaConnector(WebView webView, MainConfiguration mainConfiguration, MainController mainController, RiskController riskController, Course actualCourse) {
		super(webView, mainConfiguration, mainController, actualCourse);
		this.riskController = riskController;


		addChart(new Bubble(mainController));
		addChart(new BubbleLogarithmic(mainController));
		addChart(new RiskBar(mainController));
		addChart(new RiskBarTemporal(mainController, riskController.getDatePickerStart(),
				riskController.getDatePickerEnd()));
		addChart(new RiskEvolution(mainController, riskController.getDatePickerStart(),
				riskController.getDatePickerEnd(), riskController.getChoiceBoxDate()));
		
		currentChart = charts.get(DEFAULT_CHART);

	}
	
	@Override
	public void addChart(Chart chart) {
		if (Controller.getInstance()
				.getActualCourse()
				.getUpdatedLog() != null || !chart.isUseLogs()) {
			super.addChart(chart);
		}

	}

	private void manageOptions() {
		riskController.getGridPaneOptionLogs()
				.setVisible(currentChart.isUseGroupBy());
		riskController.getDateGridPane()
				.setVisible(currentChart.isUseRangeDate());
		riskController.getOptionsUbuLogs()
				.setVisible(currentChart.isUseOptions());
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
