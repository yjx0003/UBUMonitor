package es.ubu.lsi.ubumonitor.view.chart;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.util.JSObject;

public abstract class ApexCharts extends Chart {

	public ApexCharts(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
		useLegend = true;

	}

	@Override
	public void clear() {
		// dont clear

	}

	public String getXScaleLabel() {

		JSObject jsObject = new JSObject();

		boolean display = getGeneralConfigValue("displayXScaleTitle");
		if (!display) {
			return "title:{}";
		}
		jsObject.putWithQuote("text", getXAxisTitle());
		JSObject style = new JSObject();
		style.putWithQuote("fontSize", 14);
		style.put("color", colorToRGB(getGeneralConfigValue("fontColorXScaleTitle")));
		style.putWithQuote("cssClass", "apexcharts");
		jsObject.put("style", style);

		return "title:" + jsObject;

	}

	public String getYScaleLabel() {

		JSObject jsObject = new JSObject();

		boolean display = getGeneralConfigValue("displayYScaleTitle");
		if (!display) {
			return "title:{}";
		}
		jsObject.putWithQuote("text", getYAxisTitle());
		JSObject style = new JSObject();
		style.putWithQuote("fontSize", 14);
		style.put("color", colorToRGB(getGeneralConfigValue("fontColorYScaleTitle")));
		jsObject.put("style", style.toString());
		return "title:" + jsObject.toString();

	}

}
