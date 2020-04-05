package es.ubu.lsi.ubumonitor.clustering.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.Distance;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
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
	private Algorithm algorithm;
	private List<EnrolledUser> enrolledUsers;
	private List<DataCollector> collectors;

	public void init(int start, int end, Algorithm algorithm, List<EnrolledUser> enrolledUsers,
			List<DataCollector> collectors) {
		this.start = start;
		this.end = end;
		this.algorithm = algorithm;
		this.enrolledUsers = enrolledUsers;
		this.collectors = collectors;
		webEngine = webView.getEngine();
		webEngine.load(getClass().getResource("/graphics/OptimalChart.html").toExternalForm());
		start();
	}

	private void start() {
		List<Double> averages = new ArrayList<>();
		int initial = algorithm.getParameters().getValue(ClusteringParameter.NUM_CLUSTER);
		for (int i = start; i <= end; i++) {
			algorithm.getParameters().setParameter(ClusteringParameter.NUM_CLUSTER, i);
			AlgorithmExecuter executer = new AlgorithmExecuter(algorithm.getClusterer(), enrolledUsers, collectors);
			List<ClusterWrapper> clusters = executer.execute(0);
			Distance distanceType = algorithm.getParameters().getValue(ClusteringParameter.DISTANCE_TYPE);
			Map<UserData, Double> silhouette = AlgorithmExecuter.silhouette(clusters, distanceType);
			OptionalDouble average = silhouette.values().stream().mapToDouble(Double::doubleValue).average();
			averages.add(average.getAsDouble());
		}
		algorithm.getParameters().setParameter(ClusteringParameter.NUM_CLUSTER, initial);
		updateChart(averages);
	}

	private void updateChart(List<Double> averages) {
		JSObject root = new JSObject();
		JSArray datasets = new JSArray();
		JSObject data = new JSObject();
		JSArray array = new JSArray();
		array.addAll(averages);
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
