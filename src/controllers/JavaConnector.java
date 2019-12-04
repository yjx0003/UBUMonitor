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

	private Chart currentType;

	private Chart heatmap;

	private Chart stackedbar;

	private Map<ChartType, Chart> mapChart;

	public JavaConnector(MainController mainController) {
		webViewChartsEngine = mainController.getWebViewChartsEngine();
		tabLogs = mainController.getTabUbuLogs();
		tabGrades = mainController.getTabUbuGrades();

		heatmap = new Heatmap(mainController);
		stackedbar = new Stackedbar(mainController);

		mapChart = new EnumMap<>(ChartType.class);
		mapChart.put(ChartType.HEAT_MAP, heatmap);
		mapChart.put(ChartType.STACKED_BAR, stackedbar);
	}

	public void updateChart(Chart chart) {

		if (!currentType.equals(chart)) {
			currentType.clear();
			currentType = chart;
		}
		updateChart();

	}

	public void updateCharts(String typeChart) {
		Chart chart = mapChart.get(ChartType.valueOf(typeChart));
		if (tabLogs.isSelected()) {
			currentTypeLogs = chart;
		} else if (tabGrades.isSelected()) {
			currentTypeGrades = chart;
		}
		updateChart(chart);
	}

	public void updateChart() {
		if (webViewChartsEngine.getLoadWorker().getState() != State.SUCCEEDED) {
			return;
		}
		currentType.update();

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
		// currentTypeGrades.hideLegend();
	}

	public void clear() {
		currentTypeLogs.clear();
		// currentTypeGrades.clear();
	}

	public Chart getCurrentTypeLogs() {
		return currentTypeLogs;
	}

	public void setCurrentTypeLogs(Chart currentTypeLogs) {
		this.currentTypeLogs = currentTypeLogs;
	}

	public Chart getCurrentTypeGrades() {
		return currentTypeGrades;
	}

	public void setCurrentTypeGrades(Chart currentTypeGrades) {
		this.currentTypeGrades = currentTypeGrades;
	}

	public Chart getCurrentType() {
		return currentType;
	}

	public void setCurrentType(Chart currentType) {
		this.currentType = currentType;
	}

	public void setCurrentTypeGrades(ChartType chartType) {
		setCurrentTypeGrades(mapChart.get(chartType));

	}

	public void setCurrentTypeLogs(ChartType chartType) {
		setCurrentTypeLogs(mapChart.get(chartType));

	}

	public void setDefaultValues() {
		if (tabLogs.isSelected()) {
			webViewChartsEngine.executeScript("manageButtons('" + "log" + "')");
			setCurrentTypeLogs(ChartType.HEAT_MAP);
			setCurrentType(getCurrentTypeLogs());
		} else if (tabGrades.isSelected()) {
			webViewChartsEngine.executeScript("manageButtons('" + "grade" + "')");
			setCurrentTypeGrades(ChartType.LINE);
			setCurrentType(getCurrentTypeGrades());
		}

	}

}
