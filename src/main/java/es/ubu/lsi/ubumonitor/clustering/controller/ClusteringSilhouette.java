package es.ubu.lsi.ubumonitor.clustering.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.Distance;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class ClusteringSilhouette {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClusteringSilhouette.class);

	private WebView webView;
	private WebEngine webEngine;

	public ClusteringSilhouette(ClusteringController clusteringController) {
		webView = clusteringController.getwebViewSilhouette();
		webEngine = webView.getEngine();
		init();
	}

	private void init() {
		webView.setContextMenuEnabled(false);

		webEngine.load(getClass().getResource("/graphics/SilhouetteChart.html").toExternalForm());
	}

	public void updateChart(List<ClusterWrapper> clusters, Distance distanceType) {
		Map<UserData, Double> silhouette = AlgorithmExecuter.silhouette(clusters, distanceType);
		JSObject root = new JSObject();
		JSArray datasets = new JSArray();
		int i = 0;
		for (ClusterWrapper clusterWrapper : clusters) {
			JSObject group = new JSObject();
			group.putWithQuote("label", clusterWrapper.getName());
			group.put("borderColor", "colorHash.hex(" + clusterWrapper.getId() + ")");
			group.put("backgroundColor", "colorHash.hex(" + clusterWrapper.getId() + ")");
			JSArray data = new JSArray();
			data.addAll(Collections.nCopies(i, "null"));
			List<UserData> sortedCluster = clusterWrapper.stream()
					.sorted(Comparator.comparingDouble(e -> silhouette.get(e)).reversed()).collect(Collectors.toList());
			for (UserData userData : sortedCluster) {
				data.add(silhouette.get(userData));
			}
			i += clusterWrapper.size();
			group.put("data", data);
			datasets.add(group);
		}
		root.put("datasets", datasets);
		JSArray labels = new JSArray();
		labels.addAll(Collections.nCopies(i, "null"));
		root.put("labels", labels);

		LOGGER.debug("Silhouette: {}", root);
		webEngine.executeScript("updateChart(" + root + ")");
	}
}
