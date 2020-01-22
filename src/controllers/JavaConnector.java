package controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import controllers.charts.ActivitiesStatusTable;
import controllers.charts.BoxPlot;
import controllers.charts.CalificationBar;
import controllers.charts.Chart;
import controllers.charts.ChartType;
import controllers.charts.CumLine;
import controllers.charts.GradeReportTable;
import controllers.charts.Heatmap;
import controllers.charts.Line;
import controllers.charts.MeanDiff;
import controllers.charts.Radar;
import controllers.charts.Stackedbar;
import controllers.charts.Tabs;
import controllers.charts.Violin;
import javafx.concurrent.Worker.State;
import javafx.scene.control.Tab;
import javafx.scene.web.WebEngine;
import util.UtilMethods;

public class JavaConnector {

	private WebEngine webViewChartsEngine;

	private Tab tabLogs;

	private Tab tabGrades;
	private Tab tabActivityCompletion;

	private Chart currentTypeLogs;

	private Chart currentTypeGrades;
	private Chart currentTypeActivityCompletion;
	private Chart currentType;

	private Map<ChartType, Chart> mapChart;

	private File file;

	private MainController mainController;

	private static final ChartType DEFAULT_LOG_CHART = ChartType.STACKED_BAR;
	private static final ChartType DEFAULT_GRADE_CHART = ChartType.LINE;

	private static final ChartType DEFAULT_ACTIVITY_COMPLETION_CHART = ChartType.ACTIVITIES_TABLE;

	public JavaConnector(MainController mainController) {

		this.mainController = mainController;
		webViewChartsEngine = mainController.getWebViewChartsEngine();
		tabLogs = mainController.getTabUbuLogs();
		tabGrades = mainController.getTabUbuGrades();
		tabActivityCompletion = mainController.getTabActivity();

		mapChart = new EnumMap<>(ChartType.class);
		addChart(new Heatmap(mainController));
		addChart(new Stackedbar(mainController));
		addChart(new Line(mainController));
		addChart(new Radar(mainController));

		addChart(new BoxPlot(mainController));

		addChart(new Violin(mainController));
		addChart(new GradeReportTable(mainController));
		addChart(new CumLine(mainController));
		addChart(new MeanDiff(mainController));
		addChart(new ActivitiesStatusTable(mainController));
		addChart(new CalificationBar(mainController));
	}

	private void addChart(Chart chart) {
		mapChart.put(chart.getChartType(), chart);
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
		} else if (tabActivityCompletion.isSelected()) {
			currentTypeActivityCompletion = chart;
		}

		updateChart(chart);
		
	}

	public void updateChart() {
		if (webViewChartsEngine.getLoadWorker().getState() != State.SUCCEEDED) {
			return;
		}
		setMax();
		currentType.update();

	}

	public void updateChartFromJS() {
		updateChart();
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

	private void setCurrentTypeActivityCompletion(ChartType chartType) {
		setCurrentTypeActivityCompletion(mapChart.get(chartType));

	}

	private void setCurrentTypeActivityCompletion(Chart chart) {
		this.currentTypeActivityCompletion = chart;

	}

	public void setDefaultValues() {
		webViewChartsEngine.executeScript("setLocale('" + Locale.getDefault().toLanguageTag() + "')");
		setCurrentTypeLogs(DEFAULT_LOG_CHART);
		setCurrentTypeGrades(DEFAULT_GRADE_CHART);
		setCurrentTypeActivityCompletion(DEFAULT_ACTIVITY_COMPLETION_CHART);
		if (tabLogs.isSelected()) {
			webViewChartsEngine.executeScript("manageButtons('" + Tabs.LOGS + "')");

			setCurrentType(getCurrentTypeLogs());
		} else if (tabGrades.isSelected()) {
			webViewChartsEngine.executeScript("manageButtons('" + Tabs.GRADES + "')");

			setCurrentType(getCurrentTypeGrades());
		} else if (tabActivityCompletion.isSelected()) {
			webViewChartsEngine.executeScript("manageButtons('" + Tabs.ACTIVITY_COMPLETION + "')");
			setCurrentType(getCurrentTypeActivityCompletion());
		}
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		boolean legendActive = mainConfiguration.getValue("General", "legendActive");
		boolean generalActive = mainConfiguration.getValue("General", "generalActive");
		boolean groupActive = mainConfiguration.getValue("General", "groupActive");
		webViewChartsEngine.executeScript(String.format("imageButton('%s',%s)", "btnLegend", legendActive));
		webViewChartsEngine.executeScript(String.format("imageButton('%s',%s)", "btnMean", generalActive));
		webViewChartsEngine.executeScript(String.format("imageButton('%s',%s)", "btnGroupMean", groupActive));

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
		UtilMethods.errorWindow(Controller.getInstance().getStage(), errorMessage);
	}

	public void dataPointSelection(int selectedIndex) {

		int index = currentType.onClick(selectedIndex);
		if (index >= 0) {
			mainController.getListParticipants().scrollTo(index);
			mainController.getListParticipants().getFocusModel().focus(index);
		}

	}

	public boolean swapLegend() {
		return swap("General", "legendActive");
	}

	public boolean swapGeneral() {
		return swap("General", "generalActive");
	}

	public boolean swapGroup() {
		return swap("General", "groupActive");
	}

	private boolean swap(String category, String name) {
		boolean active = Controller.getInstance().getMainConfiguration().getValue(category, name);
		Controller.getInstance().getMainConfiguration().setValue(category, name, !active); // toggle;
		return !active;
	}

	public String getI18n(String key) {

		return I18n.get(key);
	}

	public void setMax() {
		if (!tabLogs.isSelected()) {
			return;
		}

		if (currentType == null) {
			mainController.getTextFieldMax().setText("1");
		} else if (currentType.isCalculateMaxActivated()) {

			mainController.getTextFieldMax().setText(currentType.getMax());
		}

	}

	public Chart getCurrentTypeActivityCompletion() {
		return currentTypeActivityCompletion;
	}

	public void updateButtons() {
		updateButton("General", "legendActive", "btnLegend");
		updateButton("General", "generalActive", "btnMean");
		updateButton("General", "groupActive", "btnGroupMean");

		
	}

	private void updateButton(String category, String name, String button) {
		webViewChartsEngine.executeScript(String.format("imageButton('%s',  %s)", button,
				Controller.getInstance().getMainConfiguration().getValue(category, name)));
	}

}
