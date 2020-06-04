package es.ubu.lsi.ubumonitor.clustering.chart;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.clustering.analysis.methods.SilhouetteMethod;
import es.ubu.lsi.ubumonitor.clustering.controller.ClusteringController;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.Distance;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.clustering.util.ExportUtil;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;

public class SilhouetteChart extends ClusteringChart {

	private static final Logger LOGGER = LoggerFactory.getLogger(SilhouetteChart.class);
	private Map<UserData, Double> silhouette;
	private Distance distanceType;

	public SilhouetteChart(ClusteringController clusteringController) {
		super(clusteringController.getwebViewSilhouette());
		getWebEngine().load(getClass().getResource("/graphics/SilhouetteChart.html").toExternalForm());
	}

	public void setDistanceType(Distance distanceType) {
		this.distanceType = distanceType;
	}

	public void updateChart(List<ClusterWrapper> clusters) {
		silhouette = SilhouetteMethod.silhouette(clusters, distanceType);
		Map<ClusterWrapper, Color> colors = UtilMethods.getRandomColors(clusters);
		JSObject root = new JSObject();
		JSArray datasets = new JSArray();
		JSArray data = new JSArray();
		JSArray backgroundColor = new JSArray();
		JSArray labels = new JSArray();
		int i = 0;
		for (ClusterWrapper cluster : clusters) {
			List<UserData> sortedCluster = cluster.stream()
					.sorted(Comparator.comparingDouble(e -> silhouette.get(e)).reversed()).collect(Collectors.toList());
			for (UserData userData : sortedCluster) {
				data.add(silhouette.get(userData));
				labels.addWithQuote(userData.getEnrolledUser().getFullName());
			}
			i += cluster.size() + 1;
			backgroundColor.addAll(Collections.nCopies(cluster.size(), UtilMethods.colorToRGB(colors.get(cluster))));
			data.add("null");
			backgroundColor.add("null");
			labels.add("null");
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

		root.put("labels", labels);

		JSArray clustersName = new JSArray();
		int total = clusters.stream().mapToInt(ClusterWrapper::size).sum();
		List<String> names = clusters.stream().map(c -> getLegend(c, total)).collect(Collectors.toList());
		clustersName.addAllWithQuote(names);

		LOGGER.debug("Silhouette: {}", root);
		JSArray jsColors = new JSArray();
		jsColors.addAll(colors.values().stream().map(UtilMethods::colorToRGB).collect(Collectors.toList()));
		LOGGER.debug("Colors: {}", jsColors);
		getWebEngine().executeScript("updateChart(" + root + "," + clustersName + "," + jsColors + ",'"
				+ I18n.get("clustering.silhouetteWidth") + "')");

	}

	@Override
	protected void exportData(File file) throws IOException {
		String[] head = new String[] { "UserId", "FullName", "Cluster", "Silhouette width" };
		Comparator<UserData> id = Comparator.comparingInt(u -> u.getCluster().getId());
		Comparator<UserData> width = Comparator.comparingDouble((UserData u) -> silhouette.get(u)).reversed();
		List<UserData> usersData = silhouette.keySet().stream().sorted(id.thenComparing(width))
				.collect(Collectors.toList());

		List<List<Object>> data = new ArrayList<>();
		for (UserData userData : usersData) {
			EnrolledUser enrolledUser = userData.getEnrolledUser();
			List<Object> row = new ArrayList<>();
			row.add(enrolledUser.getId());
			row.add(enrolledUser.getFullName());
			row.add(userData.getCluster().getName());
			row.add(silhouette.get(userData));
			data.add(row);
		}
		ExportUtil.exportCSV(file, head, data);
	}

}
