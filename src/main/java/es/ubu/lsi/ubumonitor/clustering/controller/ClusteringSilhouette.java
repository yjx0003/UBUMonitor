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

import es.ubu.lsi.ubumonitor.clustering.analysis.methods.SilhouetteMethod;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.Distance;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.clustering.util.ExportUtil;
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
		silhouette = SilhouetteMethod.silhouette(clusters, distanceType);
		JSObject root = new JSObject();
		JSArray datasets = new JSArray();
		JSArray data = new JSArray();
		JSArray backgroundColor = new JSArray();
		int i = 0;
		for (ClusterWrapper cluster : clusters) {
			List<UserData> sortedCluster = cluster.stream()
					.sorted(Comparator.comparingDouble(e -> silhouette.get(e)).reversed()).collect(Collectors.toList());
			for (UserData userData : sortedCluster) {
				data.add(silhouette.get(userData));
			}
			i += cluster.size() + 1;
			backgroundColor.addAll(Collections.nCopies(cluster.size(), "colorHash.hex(" + cluster.getId() + ")"));
			data.add("null");
			backgroundColor.add("null");
		}
		JSObject dataset = new JSObject();
		dataset.put("data", data);
		dataset.put("backgroundColor", backgroundColor);
		datasets.add(dataset);
		root.put("datasets", datasets);
		JSArray labels = new JSArray();
		labels.addAll(Collections.nCopies(i, "null"));
		root.put("labels", labels);

		JSArray clustersName = new JSArray();
		clustersName.addAllWithQuote(clusters.stream().map(ClusterWrapper::getName).collect(Collectors.toList()));

		LOGGER.debug("Silhouette: {}", root);
		getWebEngine().executeScript("updateChart(" + root + "," + clustersName + ")");

	}

	@Override
	protected void exportData(File file) throws IOException {
		ExportUtil.exportSilhouette(file, silhouette);
	}
}
