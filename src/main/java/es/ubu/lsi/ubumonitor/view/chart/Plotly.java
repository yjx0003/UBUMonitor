package es.ubu.lsi.ubumonitor.view.chart;

import java.io.File;
import java.io.IOException;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public abstract class Plotly extends Chart {

	private WebView webView;

	public Plotly(MainController mainController, ChartType chartType, WebView webView) {
		super(mainController, chartType);
		this.webView = webView;
	}

	@Override
	public void clear() {
		clear(webViewChartsEngine);
	}

	public static void clear(WebEngine webEngine) {
		webEngine.executeScript("Plotly.purge('plotlyDiv')");
	}

	public static void exportImage(File file, WebView webView) throws IOException {
		UtilMethods.snapshotNode(file, webView);
		UtilMethods.showExportedFile(file);
	}

	@Override
	public void exportImage(File file) throws IOException {

		exportImage(file, webView);
	}

	@Override
	public JSObject getOptions(JSObject jsObject) {
		return jsObject;
	}

	@Override
	public void update() {
		JSArray data = new JSArray();
		JSObject layout = new JSObject();
		JSArray frames = new JSArray();
		createData(data);
		createLayout(layout);
		createFrames(frames);
		
		JSObject plot = new JSObject();

		plot.put("data", data);
		plot.put("layout", layout);
		plot.put("frames", frames);
		webViewChartsEngine.executeScript("updatePlotly(" + plot + "," + getOptions() + ")");
		
	}


	public abstract void createData(JSArray data);

	public void createLayout(JSObject layout) {
	}

	public void createFrames(JSArray frames) {
	}
}
