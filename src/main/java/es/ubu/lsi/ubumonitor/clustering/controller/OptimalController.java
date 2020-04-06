package es.ubu.lsi.ubumonitor.clustering.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class OptimalController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OptimalController.class);

	@FXML
	private ProgressIndicator progressIndicator;

	@FXML
	private WebView webView;
	private WebEngine webEngine;

	private int start;
	private int end;

	public void init(int start, int end) {
		this.start = start;
		this.end = end;
		webEngine = webView.getEngine();
		webEngine.load(getClass().getResource("/graphics/OptimalChart.html").toExternalForm());
	}

	public void updateChart(List<Double> points) {
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
		labels.addAll(IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList()));
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

}
