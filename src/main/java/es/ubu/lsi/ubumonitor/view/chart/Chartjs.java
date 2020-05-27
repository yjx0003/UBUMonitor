package es.ubu.lsi.ubumonitor.view.chart;

import java.io.File;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.util.JSObject;

public abstract class Chartjs extends Chart {

	public Chartjs(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
	}

	@Override
	public void clear() {
		//webViewChartsEngine.executeScript("clearChartjs()");

	}

	@Override
	public void hideLegend() {
		webViewChartsEngine.executeScript("hideLegendChartjs()");

	}

	@Override
	public String export(File file) {
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

}
