package es.ubu.lsi.ubumonitor.clustering.controller;

import java.awt.Color;
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
import es.ubu.lsi.ubumonitor.util.UtilMethods;

public class ClusteringSilhouette extends AbstractChart {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClusteringSilhouette.class);
	private Map<UserData, Double> silhouette;

	public ClusteringSilhouette(ClusteringController clusteringController) {
		super(clusteringController.getwebViewSilhouette());
		getWebEngine().load(getClass().getResource("/graphics/SilhouetteChart.html").toExternalForm());
	}

	public void updateChart(List<ClusterWrapper> clusters, Distance distanceType) {
		silhouette = SilhouetteMethod.silhouette(clusters, distanceType);
		Map<ClusterWrapper, Color> colors = UtilMethods.getRandomColors(clusters);
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
			backgroundColor.addAll(Collections.nCopies(cluster.size(), UtilMethods.colorToRGB(colors.get(cluster))));
			data.add("null");
			backgroundColor.add("null");
		}
		JSObject dataset = new JSObject();
		dataset.putWithQuote("type", "bar");
		dataset.put("data", data);
		dataset.put("backgroundColor", backgroundColor);
		
		// Average
		JSObject lineDataset = new JSObject();
		lineDataset.putWithQuote("borderColor", "rgba(0,0,0,0.5)");

		JSArray average = new JSArray();
		List<Double> list = Collections.nCopies(i,
				silhouette.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
		average.addAll(list);
		
		lineDataset.put("data", average);
		
		datasets.add(lineDataset);
		datasets.add(dataset);

		root.put("datasets", datasets);
		
		JSArray labels = new JSArray();
		labels.addAll(Collections.nCopies(i, "null"));
		root.put("labels", labels);

		JSArray clustersName = new JSArray();
		clustersName.addAllWithQuote(clusters.stream().map(ClusterWrapper::getName).collect(Collectors.toList()));

		LOGGER.debug("Silhouette: {}", root);
		JSArray jsColors = new JSArray();
		jsColors.addAll(colors.values().stream().map(UtilMethods::colorToRGB).collect(Collectors.toList()));
		LOGGER.debug("Colors: {}", jsColors);
		getWebEngine().executeScript("updateChart(" + root + "," + clustersName + "," + jsColors + ")");

	}

	@Override
	protected void exportData(File file) throws IOException {
		ExportUtil.exportSilhouette(file, silhouette);
	}
}
