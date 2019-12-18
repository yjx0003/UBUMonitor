package controllers.charts;

import controllers.MainController;

public abstract class Tabulator extends Chart{

	public Tabulator(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
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
