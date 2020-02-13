package es.ubu.lsi.controllers.charts;

import java.util.StringJoiner;

import es.ubu.lsi.controllers.MainController;
import es.ubu.lsi.controllers.configuration.MainConfiguration;

public abstract class Chartjs extends Chart {

	public Chartjs(MainController mainController, ChartType chartType, Tabs tabName) {
		super(mainController, chartType, tabName);

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
		StringJoiner jsObject = JSObject();
		addKeyValue(jsObject, "display", (boolean) mainConfiguration.getValue(MainConfiguration.GENERAL, "displayXScaleTitle"));
		addKeyValueWithQuote(jsObject, "labelString", getXAxisTitle());
		addKeyValue(jsObject, "fontColor", colorToRGB(mainConfiguration.getValue(MainConfiguration.GENERAL, "fontColorXScaleTitle")));
		addKeyValueWithQuote(jsObject, "fontStyle", "bold");

		return  "scaleLabel:" +jsObject.toString();

	}

	public String getYScaleLabel() {
		MainConfiguration mainConfiguration = controller.getMainConfiguration();
		StringJoiner jsObject = JSObject();
		addKeyValue(jsObject, "display",
				(boolean) mainConfiguration.getValue(MainConfiguration.GENERAL, "displayYScaleTitle"));
		addKeyValueWithQuote(jsObject, "labelString", getYAxisTitle());
		addKeyValueWithQuote(jsObject, "fontSize", 14);
		addKeyValue(jsObject, "fontColor",
				colorToRGB(mainConfiguration.getValue(MainConfiguration.GENERAL, "fontColorYScaleTitle")));
		addKeyValueWithQuote(jsObject, "fontStyle", "bold");

		return "scaleLabel:" + jsObject.toString();

	}

	

}
