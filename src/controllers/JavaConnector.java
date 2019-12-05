package controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

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

	private Map<ChartType, Chart> mapChart;
	
	private File file;
	
	private MainController mainController;

	public JavaConnector(MainController mainController) {
		
		this.mainController = mainController;
		webViewChartsEngine = mainController.getWebViewChartsEngine();
		tabLogs = mainController.getTabUbuLogs();
		tabGrades = mainController.getTabUbuGrades();

		mapChart = new EnumMap<>(ChartType.class);
		mapChart.put(ChartType.HEAT_MAP, new Heatmap(mainController));
		mapChart.put(ChartType.STACKED_BAR, new Stackedbar(mainController));
	}

	public void updateChart(Chart chart) {

		if (currentType.getChartType() != chart.getChartType()) {
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
		currentType.hideLegend();
	}

	public void clear() {
		currentType.clear();
		
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
		setCurrentTypeLogs(ChartType.STACKED_BAR);
		setCurrentTypeGrades(ChartType.LINE);
		if (tabLogs.isSelected()) {
			webViewChartsEngine.executeScript("manageButtons('" + "log" + "')");
			
			setCurrentType(getCurrentTypeLogs());
		} else if (tabGrades.isSelected()) {
			webViewChartsEngine.executeScript("manageButtons('" + "grade" + "')");
			
			setCurrentType(getCurrentTypeGrades());
		}

	}
	
	public String export(File file) {
		this.file = file;
		return currentType.export();
	}
	

	
	public void saveImage(String str) throws IOException {
		
		byte[] imgdata = DatatypeConverter.parseBase64Binary(str.substring(str.indexOf(',') + 1));
		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imgdata));
		ImageIO.write(bufferedImage, "png", file);
	}
	
	
	public void showErrorWindow(String errorMessage) {
		mainController.errorWindow("", false);
	}

}
