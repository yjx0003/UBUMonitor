package controllers.charts;

import controllers.JavaConnector.ChartType;
import controllers.MainController;

public abstract class ApexCharts extends Chart {
	private String optionsVar;

	public ApexCharts(MainController mainController, ChartType chartType, String optionsVar) {
		super(mainController, chartType);
		this.optionsVar = optionsVar;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void hideLegend() {
		webViewChartsEngine.executeScript("hideLegendApexCharts("+optionsVar+")");
		
	}

	@Override
	public String export() {
		webViewChartsEngine.executeScript("exportApexcharts()");
		return null;
	}

}
