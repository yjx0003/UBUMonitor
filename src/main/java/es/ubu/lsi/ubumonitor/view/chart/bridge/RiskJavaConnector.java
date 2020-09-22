package es.ubu.lsi.ubumonitor.view.chart.bridge;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.controllers.tabs.RiskController;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.view.chart.Chart;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Tabs;
import es.ubu.lsi.ubumonitor.view.chart.risk.Bubble;
import es.ubu.lsi.ubumonitor.view.chart.risk.BubbleLogarithmic;
import es.ubu.lsi.ubumonitor.view.chart.risk.RiskBar;
import es.ubu.lsi.ubumonitor.view.chart.risk.RiskBarTemporal;
import es.ubu.lsi.ubumonitor.view.chart.risk.RiskEvolution;
import javafx.scene.web.WebView;

public class RiskJavaConnector extends JavaConnectorAbstract {

	

	private RiskController riskController;

	public RiskJavaConnector(WebView webView, MainConfiguration mainConfiguration, MainController mainController,
			RiskController riskController, Course actualCourse) {
		super(webView, mainConfiguration, mainController, actualCourse);
		this.riskController = riskController;

		addChart(new Bubble(mainController));
		addChart(new BubbleLogarithmic(mainController));
		addChart(new RiskBar(mainController));
		addChart(new RiskBarTemporal(mainController, riskController.getDatePickerStart(),
				riskController.getDatePickerEnd()));
		addChart(new RiskEvolution(mainController, riskController.getDatePickerStart(),
				riskController.getDatePickerEnd(), riskController.getChoiceBoxDate()));

		currentChart = charts.get(ChartType.getDefault(Tabs.RISK));

	}

	@Override
	public void addChart(Chart chart) {
		if (Controller.getInstance()
				.getActualCourse()
				.getUpdatedLog() != null || !chart.isUseLogs()) {
			super.addChart(chart);
		}

	}
	@Override
	public void manageOptions() {
		riskController.getGridPaneOptionLogs()
				.setVisible(currentChart.isUseGroupBy());
		riskController.getDateGridPane()
				.setVisible(currentChart.isUseRangeDate());
		riskController.getOptionsUbuLogs()
				.setVisible(currentChart.isUseOptions());
	}


}
