package es.ubu.lsi.ubumonitor.controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import es.ubu.lsi.ubumonitor.controllers.charts.Chart;
import es.ubu.lsi.ubumonitor.controllers.charts.ChartType;
import es.ubu.lsi.ubumonitor.controllers.charts.GradeReportTable;
import es.ubu.lsi.ubumonitor.controllers.charts.Tabs;
import es.ubu.lsi.ubumonitor.controllers.charts.activitystatus.ActivitiesStatusTable;
import es.ubu.lsi.ubumonitor.controllers.charts.gradeitems.BoxPlot;
import es.ubu.lsi.ubumonitor.controllers.charts.gradeitems.CalificationBar;
import es.ubu.lsi.ubumonitor.controllers.charts.gradeitems.Line;
import es.ubu.lsi.ubumonitor.controllers.charts.gradeitems.Radar;
import es.ubu.lsi.ubumonitor.controllers.charts.gradeitems.Violin;
import es.ubu.lsi.ubumonitor.controllers.charts.logs.CumLine;
import es.ubu.lsi.ubumonitor.controllers.charts.logs.Heatmap;
import es.ubu.lsi.ubumonitor.controllers.charts.logs.MeanDiff;
import es.ubu.lsi.ubumonitor.controllers.charts.logs.Scatter;
import es.ubu.lsi.ubumonitor.controllers.charts.logs.ScatterUser;
import es.ubu.lsi.ubumonitor.controllers.charts.logs.SessionChart;
import es.ubu.lsi.ubumonitor.controllers.charts.logs.Stackedbar;
import es.ubu.lsi.ubumonitor.controllers.charts.logs.TotalBar;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.concurrent.Worker.State;
import javafx.scene.control.Tab;
import javafx.scene.web.WebEngine;

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
	private VisualizationController visualizationController;

	private static final ChartType DEFAULT_LOG_CHART = ChartType.STACKED_BAR;
	private static final ChartType DEFAULT_GRADE_CHART = ChartType.LINE;

	private static final ChartType DEFAULT_ACTIVITY_COMPLETION_CHART = ChartType.ACTIVITIES_TABLE;

	public JavaConnector(VisualizationController visualizationController) {
		this.visualizationController = visualizationController;
		this.mainController = visualizationController.getMainController();
		webViewChartsEngine = visualizationController.getWebViewChartsEngine();
		tabLogs = mainController.getSelectionController().getTabUbuLogs();
		tabGrades = mainController.getSelectionController().getTabUbuGrades();
		tabActivityCompletion = mainController.getSelectionController().getTabActivity();

		mapChart = new EnumMap<>(ChartType.class);
		addChart(new Heatmap(mainController));
		addChart(new Stackedbar(mainController));
		addChart(new Line(mainController));
		addChart(new Radar(mainController));
		addChart(new Scatter(mainController));
		addChart(new ScatterUser(mainController));
		addChart(new BoxPlot(mainController));
		addChart(new TotalBar(mainController));
		addChart(new Violin(mainController));
		addChart(new GradeReportTable(mainController));
		addChart(new CumLine(mainController));
		addChart(new MeanDiff(mainController));
		addChart(new ActivitiesStatusTable(mainController));
		addChart(new CalificationBar(mainController));
		addChart(new SessionChart(mainController));
	}

	private void addChart(Chart chart) {
		mapChart.put(chart.getChartType(), chart);
	}

	public void updateCharts(String typeChart) {
		Chart chart = mapChart.get(ChartType.valueOf(typeChart));
		if (tabLogs.isSelected()) {
			currentTypeLogs.setMax(visualizationController.getTextFieldMax().getText());
			currentTypeLogs = chart;

		} else if (tabGrades.isSelected()) {
			currentTypeGrades = chart;
		} else if (tabActivityCompletion.isSelected()) {
			currentTypeActivityCompletion = chart;
		}

		if (currentType.getChartType() != chart.getChartType()) {
			currentType.clear();
			currentType = chart;
		}

		if (tabLogs.isSelected()) {
			if (currentType.isCalculateMaxActivated()) {
				visualizationController.getTextFieldMax().setText(currentType.calculateMax());
			} else {
				visualizationController.getTextFieldMax().setText(currentType.getMax());
			}

		}
		manageOptions();
		currentType.update();

	}

	private void manageOptions() {
		visualizationController.getOptionsUbuLogs()
				.setVisible(currentType.isUseRangeDate() || currentType.isUseGroupBy());
		visualizationController.getDateGridPane().setVisible(currentType.isUseRangeDate()
				|| currentType.isUseGroupBy() && visualizationController.getChoiceBoxDate().getValue().useDatePicker());
		visualizationController.getGridPaneOptionLogs().setVisible(currentType.isUseGroupBy());
	}

	public void updateChart(boolean calculateMax) {
		if (webViewChartsEngine.getLoadWorker().getState() != State.SUCCEEDED) {
			return;
		}
		if (calculateMax) {
			setMax();
		}
		manageOptions();
		currentType.update();

	}

	public void updateChart() {
		updateChart(true);

	}

	public void updateChartFromJS() {
		manageOptions();
		currentType.update();
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
		boolean legendActive = mainConfiguration.getValue(MainConfiguration.GENERAL, "legendActive");
		boolean generalActive = mainConfiguration.getValue(MainConfiguration.GENERAL, "generalActive");
		boolean groupActive = mainConfiguration.getValue(MainConfiguration.GENERAL, "groupActive");
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
		UtilMethods.infoWindow(I18n.get("message.export_png") + file.getAbsolutePath());
	}

	public void showErrorWindow(String errorMessage) {
		UtilMethods.errorWindow(errorMessage);
	}

	public void dataPointSelection(int selectedIndex) {

		int index = currentType.onClick(selectedIndex);
		if (index >= 0) {
			mainController.getSelectionUserController().getListParticipants().scrollTo(index);
			mainController.getSelectionUserController().getListParticipants().getFocusModel().focus(index);
		}

	}

	public boolean swapLegend() {
		return swap(MainConfiguration.GENERAL, "legendActive");
	}

	public boolean swapGeneral() {
		return swap(MainConfiguration.GENERAL, "generalActive");
	}

	public boolean swapGroup() {
		return swap(MainConfiguration.GENERAL, "groupActive");
	}

	private boolean swap(String category, String name) {
		boolean active = Controller.getInstance().getMainConfiguration().getValue(category, name);
		Controller.getInstance().getMainConfiguration().setValue(category, name, !active);
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
			visualizationController.getTextFieldMax().setText(null);
		} else if (currentType.isCalculateMaxActivated()) {

			visualizationController.getTextFieldMax().setText(currentType.calculateMax());
		}

	}

	public Chart getCurrentTypeActivityCompletion() {
		return currentTypeActivityCompletion;
	}

}
