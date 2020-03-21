package es.ubu.lsi.ubumonitor.clustering.controller;

import java.io.File;
import java.io.IOException;
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
import es.ubu.lsi.ubumonitor.clustering.util.CSVClustering;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;

public class ClusteringSilhouette extends ClusteringChart {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClusteringSilhouette.class);
	private Map<UserData, Double> silhouette;

	public ClusteringSilhouette(ClusteringController clusteringController) {
		super(clusteringController.getwebViewSilhouette());
		getWebEngine().load(getClass().getResource("/graphics/SilhouetteChart.html").toExternalForm());
	}

	public void updateChart(List<ClusterWrapper> clusters, Distance distanceType) {
		silhouette = AlgorithmExecuter.silhouette(clusters, distanceType);
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
		getWebEngine().executeScript("updateChart(" + root + ")");
	}


	@Override
	protected void exportData(File file) throws IOException {
		CSVClustering.exportSilhouette(silhouette, file.toPath());
	}
}
