package controllers.charts;

import java.util.StringJoiner;

import controllers.MainController;
import controllers.configuration.MainConfiguration;

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
		webViewChartsEngine.executeScript("hideLegendApexCharts("+getOptions()+")");
		
	}

	@Override
	public String export() {
		webViewChartsEngine.executeScript("exportApexcharts()");
		return null;
	}
	
	public String getXScaleLabel() {
		MainConfiguration mainConfiguration = controller.getMainConfiguration();
		StringJoiner jsObject = JSObject();
		
		boolean display =  mainConfiguration.getValue(MainConfiguration.GENERAL, "displayXScaleTitle");
		if (!display) {
			return "title:{}";
		}
		addKeyValueWithQuote(jsObject, "text", getYAxisTitle());
		StringJoiner style= JSObject();
		addKeyValueWithQuote(style, "fontSize", 14);
		addKeyValue(style, "color",
				colorToRGB(mainConfiguration.getValue(MainConfiguration.GENERAL, "fontColorXScaleTitle")));
		addKeyValue(jsObject, "style", style.toString());
		return "title:" + jsObject.toString();

	}

	public String getYScaleLabel() {
		MainConfiguration mainConfiguration = controller.getMainConfiguration();
		StringJoiner jsObject = JSObject();
		
		boolean display =  mainConfiguration.getValue(MainConfiguration.GENERAL, "displayYScaleTitle");
		if (!display) {
			return "title:{}";
		}
		addKeyValueWithQuote(jsObject, "text", getYAxisTitle());
		StringJoiner style= JSObject();
		addKeyValueWithQuote(style, "fontSize", 14);
		addKeyValue(style, "color",
				colorToRGB(mainConfiguration.getValue(MainConfiguration.GENERAL, "fontColorYScaleTitle")));
		addKeyValue(jsObject, "style", style.toString());
		return "title:" + jsObject.toString();

	}


}
