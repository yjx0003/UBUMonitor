package controllers.charts;

import controllers.MainController;

public abstract class ApexCharts extends Chart {
	

	public ApexCharts(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
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
