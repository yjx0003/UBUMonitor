package es.ubu.lsi.ubumonitor.view.chart;

import java.io.File;
import java.io.IOException;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.scene.web.WebView;

public abstract class VisTimeline extends Chart {
	
	private WebView webView;

	public VisTimeline(MainController mainController, ChartType chartType, WebView webView) {
		super(mainController, chartType);

	}

	@Override
	public void clear() {
		webViewChartsEngine.executeScript("clearVisTimeline()");

	}

	@Override
	public void exportImage(File file) throws IOException {
		UtilMethods.snapshotNode(file, webView);
		UtilMethods.showExportedFile(file);
	}

}
