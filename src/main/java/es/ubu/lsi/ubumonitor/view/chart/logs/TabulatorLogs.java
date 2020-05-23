package es.ubu.lsi.ubumonitor.view.chart.logs;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;

public abstract class TabulatorLogs extends ChartLogs {

	public TabulatorLogs(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
	}

	@Override
	protected String getJSFunction(String dataset, String options) {

		return "updateTabulator(" + dataset + "," + options + ")";

	}

	@Override
	public void clear() {
		// do nothing

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
