package es.ubu.lsi.ubumonitor.clustering.chart;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.clustering.controller.AlgorithmExecuter;
import es.ubu.lsi.ubumonitor.clustering.controller.ClusteringController;
import es.ubu.lsi.ubumonitor.clustering.controller.Connector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.clustering.util.ExportUtil;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;

public class Scatter2DChart extends ClusteringChart {

	private static final Logger LOGGER = LoggerFactory.getLogger(Scatter2DChart.class);

	private Connector connector;
	private List<Map<UserData, double[]>> points;

	public Scatter2DChart(ClusteringController clusteringController) {
		super(clusteringController.getWebViewScatter());

		WebEngine webEngine = getWebEngine();
		connector = new Connector(clusteringController, webEngine);
		webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
			if (Worker.State.SUCCEEDED != newState)
				return;
			netscape.javascript.JSObject window = (netscape.javascript.JSObject) webEngine.executeScript("window");
			window.setMember("javaConnector", connector);
		});
		webEngine.load(getClass().getResource("/graphics/ClusterChart.html").toExternalForm());
	}

	@Override
	public void updateChart(List<ClusterWrapper> clusters) {
		connector.setClusters(clusters);
		LOGGER.debug("Clusters: {}", clusters);
		points = AlgorithmExecuter.clustersTo(2, clusters);

		Map<ClusterWrapper, Color> colors = UtilMethods.getRandomColors(clusters);

		JSObject root = new JSObject();
		JSArray datasets = new JSArray();
		JSObject centers = new JSObject();
		centers.putWithQuote("label", I18n.get("clustering.centroids"));
		centers.putWithQuote("backgroundColor", "black");
		JSArray centersData = new JSArray();

		int total = clusters.stream().mapToInt(ClusterWrapper::size).sum();

		for (int i = 0; i < points.size(); i++) {
			JSObject group = new JSObject();
			group.putWithQuote("label", getLegend(clusters.get(i), total));
			group.put("backgroundColor", UtilMethods.colorToRGB(colors.get(clusters.get(i))));
			JSArray data = new JSArray();
			for (Map.Entry<UserData, double[]> userEntry : points.get(i).entrySet()) {
				UserData user = userEntry.getKey();
				JSObject coord = new JSObject();
				double[] point = userEntry.getValue();
				coord.put("x", point[0]);
				coord.put("y", point.length == 2 ? point[1] : 0.0);

				if (user == null) {
					coord.putWithQuote("user", I18n.get("clustering.centroid"));
					centersData.add(coord);
				} else {
					coord.putWithQuote("user", user.getEnrolledUser().getFullName());
					data.add(coord);
				}
			}
			group.put("data", data);
			datasets.add(group);
		}

		if (!centersData.isEmpty()) {
			centers.put("data", centersData);
			datasets.add(centers);
		}
		root.put("datasets", datasets);
		LOGGER.debug("Data: {}", root);

		getWebEngine().executeScript("updateChart(" + root + ")");
	}

	@Override
	protected void exportData(File file) throws IOException {
		String[] head = new String[] { "UserId", "FullName", "Cluster", "X", "Y" };
		List<List<Object>> data = new ArrayList<>();
		for (Map<UserData, double[]> cluster : points) {
			for (Entry<UserData, double[]> entry : cluster.entrySet()) {
				UserData userData = entry.getKey();
				EnrolledUser enrolledUser = userData.getEnrolledUser();
				List<Object> row = new ArrayList<>();
				row.add(enrolledUser.getId());
				row.add(enrolledUser.getFullName());
				row.add(userData.getCluster().getName());
				row.add(entry.getValue()[0]);
				row.add(entry.getValue()[1]);
				data.add(row);
			}
		}
		ExportUtil.exportCSV(file, head, data);
	}

}
