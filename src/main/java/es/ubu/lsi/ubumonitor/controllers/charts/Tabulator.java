package es.ubu.lsi.ubumonitor.controllers.charts;

import es.ubu.lsi.ubumonitor.controllers.MainController;

public abstract class Tabulator extends Chart{

	public Tabulator(MainController mainController, ChartType chartType, Tabs tabName) {
		super(mainController, chartType, tabName);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hideLegend() {
		// do nothing
		
	}

	@Override
	public String export() {
		webViewChartsEngine.executeScript("genericExport('tabulatorDiv')");
		return null;
	}

}
