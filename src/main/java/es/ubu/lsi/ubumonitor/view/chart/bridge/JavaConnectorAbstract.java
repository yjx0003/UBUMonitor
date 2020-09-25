package es.ubu.lsi.ubumonitor.view.chart.bridge;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.Chart;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public abstract class JavaConnectorAbstract implements JavaConnector {

	protected WebEngine webEngine;
	protected Map<ChartType, Chart> charts;
	protected MainConfiguration mainConfiguration;
	protected Chart currentChart;
	protected MainController mainController;
	protected WebView webView;
	protected Course actualCourse;

	public JavaConnectorAbstract(WebView webView, MainConfiguration mainConfiguration, MainController mainController,
			Course actualCourse) {
		this.webView = webView;
		this.webEngine = webView.getEngine();
		this.mainConfiguration = mainConfiguration;
		this.mainController = mainController;
		this.actualCourse = actualCourse;
		charts = new EnumMap<>(ChartType.class);
	}

	@Override
	public boolean toggleLegend() {
		return toggleButton(MainConfiguration.GENERAL, "legendActive");
	}

	@Override
	public boolean toggleGeneral() {
		return toggleButton(MainConfiguration.GENERAL, "generalActive");
	}

	@Override
	public boolean toggleGroup() {
		return toggleButton(MainConfiguration.GENERAL, "groupActive");
	}

	private boolean toggleButton(String category, String name) {
		boolean active = mainConfiguration.getValue(category, name);
		mainConfiguration.setValue(category, name, !active);
		return !active;
	}

	@Override
	public void updateOptionsImages() {
		boolean legendActive = mainConfiguration.getValue(MainConfiguration.GENERAL, "legendActive");
		boolean generalActive = mainConfiguration.getValue(MainConfiguration.GENERAL, "generalActive");
		boolean groupActive = mainConfiguration.getValue(MainConfiguration.GENERAL, "groupActive");
		webEngine.executeScript(String.format("imageButton('%s',%s)", "btnLegend", legendActive));
		webEngine.executeScript(String.format("imageButton('%s',%s)", "btnMean", generalActive));
		webEngine.executeScript(String.format("imageButton('%s',%s)", "btnGroupMean", groupActive));

	}

	@Override
	public void inititDefaultValues() {
		JSArray jsArray = new JSArray();
		for (ChartType chartType : charts.keySet()) {
			JSObject jsObject = new JSObject();
			jsObject.putWithQuote("id", chartType.toString());
			jsObject.putWithQuote("text", I18n.get(chartType));
			jsObject.putWithQuote("type", chartType.getTab());
			jsArray.add(jsObject);
		}

		webEngine.executeScript("generateButtons(" + jsArray + ")");
		webEngine.executeScript("createChartDivs()");
		webEngine.executeScript(String.format("translate(%s,'%s')", "'btnLegend'",
				UtilMethods.escapeJavaScriptText(I18n.get("btnLegend"))));
		webEngine.executeScript(String.format("translate(%s,'%s')", "'btnMean'",
				UtilMethods.escapeJavaScriptText(I18n.get("btnMean"))));
		webEngine.executeScript(String.format("translate(%s,'%s')", "'btnGroupMean'",
				UtilMethods.escapeJavaScriptText(I18n.get("btnGroupMean"))));
		webEngine.executeScript("setLocale('" + Locale.getDefault()
				.toLanguageTag() + "')");

		updateOptionsImages();

	}

	@Override
	public void showErrorWindow(String errorMessage) {
		UtilMethods.errorWindow(errorMessage);
	}

	@Override
	public void dataPointSelection(int selectedIndex) {

		int index = currentChart.onClick(selectedIndex);
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

	@Override
	public Chart getCurrentChart() {
		return currentChart;
	}

	@Override
	public void clear() {
		currentChart.clear();
	}

	@Override
	public void setCurrentChart(Chart chart) {
		currentChart = chart;
	}

	@Override
	public void addChart(Chart chart) {
		chart.setWebViewChartsEngine(webEngine);
		chart.setMainConfiguration(mainConfiguration);
		chart.setActualCourse(actualCourse);
		charts.put(chart.getChartType(), chart);
	}

	@Override
	public void updateCharts(String typeChart) {
		Chart chart = charts.get(ChartType.valueOf(typeChart));
		if (currentChart.getChartType() != chart.getChartType()) {
			currentChart.clear();
			currentChart = chart;
		}
		currentChart.update();
		manageOptions();
	}

	@Override
	public void updateChart() {
		if (webEngine.getLoadWorker()
				.getState() != State.SUCCEEDED) {
			return;
		}
		currentChart.update();
		manageOptions();

	}

}
