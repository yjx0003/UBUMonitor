package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.io.File;
import java.io.IOException;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
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
		webViewChartsEngine.executeScript("clearTabulator()");

	}

	@Override
	public void hideLegend() {
		// do nothing

	}

	@Override
	public void export(File file) throws IOException {
		UtilMethods.snapshotNode(file, webView);
	}

}
