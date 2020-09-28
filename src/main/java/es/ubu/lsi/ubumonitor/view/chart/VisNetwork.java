package es.ubu.lsi.ubumonitor.view.chart;

import java.io.File;
import java.io.IOException;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.scene.web.WebView;

public abstract class VisNetwork extends Chart {
	private WebView webView;

	public VisNetwork(MainController mainController, ChartType chartType, WebView webView) {
		super(mainController, chartType);
		this.webView = webView;
	}

	@Override
	public void clear() {
		webViewChartsEngine.executeScript("clearVisNetwork()");

	}
	
	@Override
	public void exportImage(File file) throws IOException {
		UtilMethods.snapshotNode(file, webView);
		UtilMethods.showExportedFile(file);

	}
	
	public JSObject getNodesOptions() {
		return new JSObject();
	}
	
	public JSObject getEdgesOptions() {
		return new JSObject();
	}
	
	public JSObject getLayoutOptions() {
		return new JSObject();
	}
	
	public JSObject getPhysicsOptions() {
		return new JSObject();
	}

	public JSObject getInteractionOptions() {
		return new JSObject();
	}
	
	public enum Solver {
		BARNES_HUT("barnesHut"), FORCE_ATLAS_2_BASED("forceAtlas2Based"), REPULSION("repulsion");

		private String name;

		private Solver(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return I18n.get(name());
		}
	}
	
}
