package es.ubu.lsi.ubumonitor.controllers.charts;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.util.JSObject;

public abstract class ApexCharts extends Chart {

	public ApexCharts(MainController mainController, ChartType chartType, Tabs tabName) {
		super(mainController, chartType, tabName);
		useLegend = true;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hideLegend() {
		webViewChartsEngine.executeScript("hideLegendApexCharts(" + getOptions() + ")");

	}

	@Override
	public String export() {
		webViewChartsEngine.executeScript("exportApexcharts()");
		return null;
	}
	
	public String getXScaleLabel() {
		MainConfiguration mainConfiguration = controller.getMainConfiguration();
		JSObject jsObject = new JSObject();

		boolean display = mainConfiguration.getValue(MainConfiguration.GENERAL, "displayXScaleTitle");
		if (!display) {
			return "title:{}";
		}
		jsObject.putWithQuote("text", getXAxisTitle());
		JSObject style = new JSObject();
		style.putWithQuote("fontSize", 14);
		style.put("color",
				colorToRGB(mainConfiguration.getValue(MainConfiguration.GENERAL, "fontColorXScaleTitle")));
		style.putWithQuote("cssClass", "apexcharts");
		jsObject.put("style", style);

		return "title:" + jsObject;

	}

	public String getYScaleLabel() {
		MainConfiguration mainConfiguration = controller.getMainConfiguration();
		JSObject jsObject = new JSObject();

		boolean display = mainConfiguration.getValue(MainConfiguration.GENERAL, "displayYScaleTitle");
		if (!display) {
			return "title:{}";
		}
		jsObject.putWithQuote("text", getYAxisTitle());
		JSObject style = new JSObject();
		style.putWithQuote("fontSize", 14);
		style.put("color", colorToRGB(mainConfiguration.getValue(MainConfiguration.GENERAL, "fontColorYScaleTitle")));
		jsObject.put("style", style.toString());
		return "title:" + jsObject.toString();

	}

}
