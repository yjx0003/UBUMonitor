package es.ubu.lsi.ubumonitor.view.chart;

import es.ubu.lsi.ubumonitor.controllers.MainController;

public abstract class VisTimeline extends Chart {
	

	public VisTimeline(MainController mainController, ChartType chartType) {
		super(mainController, chartType);

	}

	@Override
	public void clear() {
		webViewChartsEngine.executeScript("clearVisTimeline()");

	}

}
