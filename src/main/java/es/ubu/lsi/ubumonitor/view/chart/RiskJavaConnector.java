package es.ubu.lsi.ubumonitor.view.chart;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.RiskController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.risk.Bubble;
import es.ubu.lsi.ubumonitor.view.chart.risk.BubbleLogarithmic;
import es.ubu.lsi.ubumonitor.view.chart.risk.RiskBar;
import es.ubu.lsi.ubumonitor.view.chart.risk.RiskBarTemporal;
import es.ubu.lsi.ubumonitor.view.chart.risk.RiskEvolution;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;

public class RiskJavaConnector {

	private static final ChartType DEFAULT_CHART = ChartType.getDefaultRisk();
	
	private Chart currentType;
	private WebEngine webViewChartsEngine;
	private RiskController riskController;
	private Map<ChartType, Chart> mapChart;
	

	public RiskJavaConnector(RiskController riskController) {
		this.riskController = riskController;
		webViewChartsEngine = riskController.getWebViewChartsEngine();

		mapChart = new EnumMap<>(ChartType.class);
		addChart(new Bubble(riskController.getMainController()));
		addChart(new BubbleLogarithmic(riskController.getMainController()));
		addChart(new RiskBar(riskController.getMainController()));
		addChart(new RiskBarTemporal(riskController.getMainController(), riskController.getDatePickerStart(),
				riskController.getDatePickerEnd()));
		addChart(new RiskEvolution(riskController.getMainController(), riskController.getDatePickerStart(),
				riskController.getDatePickerEnd(), riskController.getChoiceBoxDate()));

	}

	public void addChart(Chart chart) {
		chart.setWebViewChartsEngine(webViewChartsEngine);
		chart.setWebView(riskController.getWebViewCharts());
		mapChart.put(chart.chartType, chart);
	}

	private void manageOptions() {
		riskController.getGridPaneOptionLogs()
				.setVisible(currentType.isUseGroupBy());
		riskController.getDateGridPane()
				.setVisible(currentType.isUseRangeDate());
		riskController.getOptionsUbuLogs()
				.setVisible(currentType.isUseOptions());
	}

	public void updateChart() {
		if (webViewChartsEngine.getLoadWorker()
				.getState() != State.SUCCEEDED) {
			return;
		}
		manageOptions();
		currentType.update();

	}

	public void updateCharts(String typeChart) {
		Chart chart = mapChart.get(ChartType.valueOf(typeChart));
		if (currentType.getChartType() != chart.getChartType()) {
			currentType.clear();
			currentType = chart;
		}
		manageOptions();
		currentType.update();
	}

	public void setDefaultValues() {
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
		webViewChartsEngine.executeScript("setLocale('" + Locale.getDefault()
				.toLanguageTag() + "')");
		currentType = mapChart.get(DEFAULT_CHART);
	}

	public void hideLegend() {

		currentType.hideLegend();
	}

	public void clear() {
		currentType.clear();

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

	public void save(File file) throws IOException {
		currentType.export(file);

	}

	public Chart getCurrentType() {
		return currentType;
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

	public void dataPointSelection(int selectedIndex) {

		int index = currentType.onClick(selectedIndex);
		if (index >= 0) {
			currentType.mainController.getSelectionUserController()
					.getListParticipants()
					.scrollTo(index);
			currentType.mainController.getSelectionUserController()
					.getListParticipants()
					.getFocusModel()
					.focus(index);
		}

	}

	public void export(File file) throws IOException {
		currentType.export(file);
		
	}

}
