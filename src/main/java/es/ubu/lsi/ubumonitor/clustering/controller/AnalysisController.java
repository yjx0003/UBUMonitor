package es.ubu.lsi.ubumonitor.clustering.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.clustering.chart.AbstractChart;
import es.ubu.lsi.ubumonitor.clustering.util.ExportUtil;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class AnalysisController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisController.class);

	@FXML
	private ProgressIndicator progressIndicator;

	@FXML
	private WebView webView;
	private WebEngine webEngine;


	private List<Double> points;
	private int start;

	@FXML
	public void initialize() {
		webEngine = webView.getEngine();
		webEngine.load(getClass().getResource("/graphics/OptimalChart.html").toExternalForm());
		new AnalysisChart(webView);
	}

	public void updateChart(List<Double> points, int start) {
		this.points = points;
		this.start = start;
		JSObject root = new JSObject();
		JSArray datasets = new JSArray();
		JSObject data = new JSObject();
		JSArray array = new JSArray();
		array.addAll(points);
		data.put("data", array);
		data.putWithQuote("borderColor", "#3e95cd");
		data.putWithQuote("backgroundColor", "#3e95cd");
		datasets.add(data);
		root.put("datasets", datasets);

		JSArray labels = new JSArray();
		labels.addAll(IntStream.rangeClosed(start, start + points.size() - 1).boxed().collect(Collectors.toList()));
		root.put("labels", labels);

		LOGGER.debug("Optimal: {}", root);

		if (webEngine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
			showChart(root);
		} else {
			webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
				if (newState == Worker.State.SUCCEEDED) {
					showChart(root);
				}
			});
		}
	}

	private void showChart(JSObject root) {
		webEngine.executeScript("updateChart(" + root + ")");
		progressIndicator.setVisible(false);
		webView.toFront();
	}

	private class AnalysisChart extends AbstractChart {

		protected AnalysisChart(WebView webView) {
			super(webView);
		}

		@Override
		protected void exportData(File file) throws IOException {
			String[] head = new String[] { "Number of clusters", "Value" };
			List<List<Object>> data = new ArrayList<>();
			for (int i = 0; i < points.size(); i++) {
				List<Object> row = new ArrayList<>();
				row.add(start + i);
				row.add(points.get(i));
				data.add(row);
			}
			ExportUtil.exportCSV(file, head, data);
		}

	}
}
