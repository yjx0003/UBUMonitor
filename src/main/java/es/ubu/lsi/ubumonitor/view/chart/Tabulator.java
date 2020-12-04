package es.ubu.lsi.ubumonitor.view.chart;

import es.ubu.lsi.ubumonitor.controllers.MainController;

public abstract class Tabulator extends Chart {

	public Tabulator(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
	}

	@Override
	public void clear() {
		webViewChartsEngine.executeScript("clearTabulator()");

	}

}
