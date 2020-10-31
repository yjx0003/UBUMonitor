package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.io.File;
import java.io.IOException;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;
import javafx.scene.web.WebView;

public abstract class PlotlyLog extends ChartLogs {

	private WebView webView;

	public PlotlyLog(MainController mainController, ChartType chartType, WebView webView) {
		super(mainController, chartType);
	}

	@Override
	protected String getJSFunction(String dataset, String options) {
		return "updatePlotly(" + dataset + "," + options + ")";
	}


	@Override
	public JSObject getOptions(JSObject jsObject) {
		return jsObject;
	}

	@Override
	public void clear() {
		Plotly.clear(webViewChartsEngine);

	}

	@Override
	public void exportImage(File file) throws IOException {
		Plotly.exportImage(file, webView);
	}

}
