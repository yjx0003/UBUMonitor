package es.ubu.lsi.ubumonitor.view.chart;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.VisualizationController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.activitystatus.ActivitiesStatusTable;
import es.ubu.lsi.ubumonitor.view.chart.gradeitems.BoxPlot;
import es.ubu.lsi.ubumonitor.view.chart.gradeitems.CalificationBar;
import es.ubu.lsi.ubumonitor.view.chart.gradeitems.GradeReportTable;
import es.ubu.lsi.ubumonitor.view.chart.gradeitems.Line;
import es.ubu.lsi.ubumonitor.view.chart.gradeitems.Radar;
import es.ubu.lsi.ubumonitor.view.chart.gradeitems.Violin;
import es.ubu.lsi.ubumonitor.view.chart.logs.BoxplotLog;
import es.ubu.lsi.ubumonitor.view.chart.logs.BoxplotLogTime;
import es.ubu.lsi.ubumonitor.view.chart.logs.CumLine;
import es.ubu.lsi.ubumonitor.view.chart.logs.Heatmap;
import es.ubu.lsi.ubumonitor.view.chart.logs.MeanDiff;
import es.ubu.lsi.ubumonitor.view.chart.logs.Scatter;
import es.ubu.lsi.ubumonitor.view.chart.logs.ScatterUser;
import es.ubu.lsi.ubumonitor.view.chart.logs.SessionChart;
import es.ubu.lsi.ubumonitor.view.chart.logs.Stackedbar;
import es.ubu.lsi.ubumonitor.view.chart.logs.TableLog;
import es.ubu.lsi.ubumonitor.view.chart.logs.TotalBar;
import es.ubu.lsi.ubumonitor.view.chart.logs.ViolinLog;
import es.ubu.lsi.ubumonitor.view.chart.logs.ViolinLogTime;
import javafx.concurrent.Worker.State;
import javafx.scene.control.Tab;
import javafx.scene.web.WebEngine;

public class VisualizationJavaConnector {

	private static final ChartType DEFAULT_LOG_CHART = ChartType.getDefaultLogs();
	private static final ChartType DEFAULT_GRADE_CHART = ChartType.getDefaultGrades();
	private static final ChartType DEFAULT_ACTIVITY_COMPLETION_CHART = ChartType.getDefaultActivityCompletion();

	private WebEngine webViewChartsEngine;

	private Tab tabLogs;

	private Tab tabGrades;
	private Tab tabActivityCompletion;

	private Chart currentTypeLogs;

	private Chart currentTypeGrades;
	private Chart currentTypeActivityCompletion;
	private Chart currentType;

	private Map<ChartType, Chart> mapChart;

	private MainController mainController;
	private VisualizationController visualizationController;

	public VisualizationJavaConnector(VisualizationController visualizationController) {
		this.visualizationController = visualizationController;
		this.mainController = visualizationController.getMainController();
		webViewChartsEngine = visualizationController.getWebViewChartsEngine();
		tabLogs = mainController.getSelectionController()
				.getTabUbuLogs();
		tabGrades = mainController.getSelectionController()
				.getTabUbuGrades();
		tabActivityCompletion = mainController.getSelectionController()
				.getTabActivity();
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
		addChart(new BoxplotLogTime(mainController));
		addChart(new ViolinLogTime(mainController));
		addChart(new TableLog(mainController));
		addChart(new BoxplotLog(mainController));
		addChart(new ViolinLog(mainController));
		currentType = mapChart.get(DEFAULT_LOG_CHART);
	}

	private void addChart(Chart chart) {
		chart.setChoiceBoxDate(visualizationController.getChoiceBoxDate());
		chart.setDatePickerStart(visualizationController.getDatePickerStart());
		chart.setDatePickerEnd(visualizationController.getDatePickerEnd());
		chart.setWebView(visualizationController.getWebViewCharts());
		chart.setWebViewChartsEngine(visualizationController.getWebViewChartsEngine());
		mapChart.put(chart.getChartType(), chart);
	}

	public void updateCharts(String typeChart) {
		Chart chart = mapChart.get(ChartType.valueOf(typeChart));
		if (tabLogs.isSelected()) {
			currentTypeLogs.setMax(visualizationController.getTextFieldMax()
					.getText());
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
				visualizationController.getTextFieldMax()
						.setText(currentType.calculateMax());
			} else {
				visualizationController.getTextFieldMax()
						.setText(currentType.getMax());
			}

		}
		manageOptions();
		currentType.update();

	}

	private void manageOptions() {
		visualizationController.getOptionsUbuLogs()
				.setVisible(currentType.isUseRangeDate() || currentType.isUseGroupBy());
		visualizationController.getDateGridPane()
				.setVisible(currentType.isUseRangeDate()
						|| currentType.isUseGroupBy() && visualizationController.getChoiceBoxDate()
								.getValue()
								.useDatePicker());
		visualizationController.getGridPaneOptionLogs()
				.setVisible(currentType.isUseGroupBy());
	}

	public void updateChart(boolean calculateMax) {
		if (webViewChartsEngine.getLoadWorker()
				.getState() != State.SUCCEEDED) {
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
		webViewChartsEngine.executeScript("setLocale('" + Locale.getDefault()
				.toLanguageTag() + "')");

		JSArray jsArray = new JSArray();
		for (ChartType chartType : mapChart.keySet()) {
			JSObject jsObject = new JSObject();
			jsObject.putWithQuote("id", chartType.toString());
			jsObject.putWithQuote("text", I18n.get(chartType));
			jsObject.putWithQuote("type", chartType.getTab());
			jsArray.add(jsObject);
		}
	
		webViewChartsEngine.executeScript("generateButtons(" + jsArray + ")");
		webViewChartsEngine.executeScript(String.format("translate(%s,'%s')", "'btnLegend'",UtilMethods.escapeJavaScriptText( I18n.get("btnLegend"))));
		webViewChartsEngine.executeScript(String.format("translate(%s,'%s')", "'btnMean'",UtilMethods.escapeJavaScriptText( I18n.get("btnMean"))));
		webViewChartsEngine.executeScript(String.format("translate(%s,'%s')", "'btnGroupMean'", UtilMethods.escapeJavaScriptText(I18n.get("btnGroupMean"))));
		
		setCurrentTypeLogs(DEFAULT_LOG_CHART);
		setCurrentTypeGrades(DEFAULT_GRADE_CHART);
		setCurrentTypeActivityCompletion(DEFAULT_ACTIVITY_COMPLETION_CHART);
		if (tabLogs.isSelected()) {

			setCurrentType(getCurrentTypeLogs());
		} else if (tabGrades.isSelected()) {

			setCurrentType(getCurrentTypeGrades());
		} else if (tabActivityCompletion.isSelected()) {
			setCurrentType(getCurrentTypeActivityCompletion());
		}
		updateTabImages();

	}

	public void export(File file) throws IOException {
		currentType.export(file);
	}

	public void showErrorWindow(String errorMessage) {
		UtilMethods.errorWindow(errorMessage);
	}

	public void dataPointSelection(int selectedIndex) {

		int index = currentType.onClick(selectedIndex);
		if (index >= 0) {
			mainController.getSelectionUserController()
					.getListParticipants()
					.scrollTo(index);
			mainController.getSelectionUserController()
					.getListParticipants()
					.getFocusModel()
					.focus(index);
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
		boolean active = Controller.getInstance()
				.getMainConfiguration()
				.getValue(category, name);
		Controller.getInstance()
				.getMainConfiguration()
				.setValue(category, name, !active);
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
			visualizationController.getTextFieldMax()
					.setText(null);
		} else if (currentType.isCalculateMaxActivated()) {

			visualizationController.getTextFieldMax()
					.setText(currentType.calculateMax());
		}

	}

	public Chart getCurrentTypeActivityCompletion() {
		return currentTypeActivityCompletion;
	}

	public void updateTabImages() {
		MainConfiguration mainConfiguration = Controller.getInstance()
				.getMainConfiguration();
		boolean legendActive = mainConfiguration.getValue(MainConfiguration.GENERAL, "legendActive");
		boolean generalActive = mainConfiguration.getValue(MainConfiguration.GENERAL, "generalActive");
		boolean groupActive = mainConfiguration.getValue(MainConfiguration.GENERAL, "groupActive");
		webViewChartsEngine.executeScript(String.format("imageButton('%s',%s)", "btnLegend", legendActive));
		webViewChartsEngine.executeScript(String.format("imageButton('%s',%s)", "btnMean", generalActive));
		webViewChartsEngine.executeScript(String.format("imageButton('%s',%s)", "btnGroupMean", groupActive));

	}
	
	

}
