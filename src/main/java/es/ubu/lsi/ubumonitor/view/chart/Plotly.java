package es.ubu.lsi.ubumonitor.view.chart;

import java.io.File;
import java.io.IOException;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.scene.web.WebView;

public abstract class Plotly extends Chart {

	private WebView webView;


	public Plotly(MainController mainController, ChartType chartType, WebView webView) {
		super(mainController, chartType);
		this.webView  = webView;
	}

	@Override
	public void clear() {
		webViewChartsEngine.executeScript("Plotly.purge('plotlyDiv')");
	}


	@Override
	public void exportImage(File file) throws IOException {
		UtilMethods.snapshotNode(file, webView);
		UtilMethods.showExportedFile(file);

	}

}
