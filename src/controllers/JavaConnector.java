package controllers;

import java.util.EnumMap;
import java.util.Map;

import controllers.charts.Chart;
import controllers.charts.Heatmap;
import controllers.charts.Stackedbar;
import javafx.concurrent.Worker.State;
import javafx.scene.control.Tab;
import javafx.scene.web.WebEngine;

public class JavaConnector {


	private WebEngine webViewChartsEngine;
	private Tab tabLogs;
	private Tab tabGrades;

	private Chart currentTypeLogs;

	private Chart currentTypeGrades;

	private Chart heatmap;

	private Chart stackedbar;

	private Map<ChartType, Chart> mapChart;
	

	public JavaConnector(MainController mainController) {
		webViewChartsEngine = mainController.getWebViewChartsEngine();
		tabLogs = mainController.getTabUbuLogs();
		tabGrades = mainController.getTabUbuGrades();
		
		heatmap = new Heatmap(mainController);
		stackedbar = new Stackedbar(mainController);
		
		currentTypeLogs = stackedbar;
		
		mapChart = new EnumMap<>(ChartType.class);
		mapChart.put(ChartType.HEAT_MAP, heatmap);
		mapChart.put(ChartType.STACKED_BAR, stackedbar);
	}

	public void updateChart(Chart chart) {
		if (webViewChartsEngine.getLoadWorker().getState() != State.SUCCEEDED) {
			return;
		}

		if (tabLogs.isSelected()) {
			updateLogsChart(chart);
		} else if (tabGrades.isSelected()) {
			updateGradesChart(chart);
		}

	}

	public void updateCharts(String typeChart) {
		updateChart(mapChart.get(ChartType.valueOf(typeChart)));
	}

	public void updateChart() {
		if (tabLogs.isSelected()) {
			updateChart(currentTypeLogs);
		} else if (tabGrades.isSelected()) {
			updateChart(currentTypeGrades);
		}
	}

	private void updateLogsChart(Chart chart) {
		if (!currentTypeLogs.equals(chart)) {
			currentTypeLogs.clear();
			currentTypeLogs = chart;
		}
		currentTypeLogs.update();
		
	}

	private void updateGradesChart(Chart chart) {
		if (!currentTypeGrades.equals(chart)) {
			currentTypeGrades.clear();
			currentTypeGrades = chart;
		}
		currentTypeGrades.update();
	}

	public enum ChartType {
		HEAT_MAP, STACKED_BAR, LINE, RADAR, GENERAL_BOXPLOT, GROUP_BOXPLOT, TABLE;
	}

	public void updateMaxY(long max) {

		webViewChartsEngine.executeScript("changeYMaxHeatmap(" + max + ")");

		webViewChartsEngine.executeScript("changeYMaxStackedBar(" + max + ")");

	}
	
	public void hideLegend() {
		
		currentTypeLogs.hideLegend();
	}

}
