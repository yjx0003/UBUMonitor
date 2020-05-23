package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.util.List;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;

public abstract class ChartjsLog extends ChartLogs {


	public ChartjsLog(MainController mainController, ChartType chartType) {
		super(mainController, chartType);

	}

	protected JSArray createLabels(List<String> rangeDates) {
		JSArray labels = new JSArray();
		for (String date : rangeDates) {
			labels.add("'" + UtilMethods.escapeJavaScriptText(date) + "'");
		}
		return labels;
	}



	@Override
	public void clear() {
		webViewChartsEngine.executeScript("clearChartjs()");

	}

	@Override
	public void hideLegend() {
		webViewChartsEngine.executeScript("hideLegendChartjs()");

	}

	@Override
	public String export() {
		return (String) webViewChartsEngine.executeScript("exportChartjs()");
	}
	

	public String getXScaleLabel() {
		MainConfiguration mainConfiguration = controller.getMainConfiguration();
		JSObject jsObject = new JSObject();
		jsObject.put("display", mainConfiguration.getValue(MainConfiguration.GENERAL, "displayXScaleTitle"));
		jsObject.putWithQuote("labelString", getXAxisTitle());
		jsObject.put("fontColor",
				colorToRGB(mainConfiguration.getValue(MainConfiguration.GENERAL, "fontColorXScaleTitle")));
		jsObject.putWithQuote("fontStyle", "bold");

		return "scaleLabel:" + jsObject;

	}


	public String getYScaleLabel() {
		MainConfiguration mainConfiguration = controller.getMainConfiguration();
		JSObject jsObject = new JSObject();
		jsObject.put("display", mainConfiguration.getValue(MainConfiguration.GENERAL, "displayYScaleTitle"));
		jsObject.putWithQuote("labelString", getYAxisTitle());
		jsObject.putWithQuote("fontSize", 14);
		jsObject.put("fontColor",
				colorToRGB(mainConfiguration.getValue(MainConfiguration.GENERAL, "fontColorYScaleTitle")));
		jsObject.putWithQuote("fontStyle", "bold");

		return "scaleLabel:" + jsObject;

	}

	@Override
	protected String getJSFunction(String dataset, String options) {
		return "updateChartjs" + "(" + dataset + "," + options + ")";
	}
}
