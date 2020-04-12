package es.ubu.lsi.ubumonitor.clustering.controller;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.clustering.util.ExportUtil;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;

public class ClusteringGraph extends ClusteringChart {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClusteringGraph.class);

	private Connector connector;
	private List<Map<UserData, double[]>> points;

	public ClusteringGraph(ClusteringController clusteringController) {
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

	public void updateChart(List<ClusterWrapper> clusters) {
		connector.setClusters(clusters);
		points = AlgorithmExecuter.clustersTo2D(clusters);

		Map<ClusterWrapper, Color> colors = UtilMethods.getRandomColors(clusters);

		JSObject root = new JSObject();
		JSArray datasets = new JSArray();
		JSObject centers = new JSObject();
		centers.putWithQuote("label", I18n.get("clustering.centroids"));
		centers.putWithQuote("backgroundColor", "black");
		JSArray centersData = new JSArray();
		for (int i = 0; i < points.size(); i++) {
			JSObject group = new JSObject();
			group.putWithQuote("label", clusters.get(i).getName());
			group.put("backgroundColor", UtilMethods.colorToRGB(colors.get(clusters.get(i))));
			JSArray data = new JSArray();
			for (Map.Entry<UserData, double[]> userEntry : points.get(i).entrySet()) {
				UserData user = userEntry.getKey();
				JSObject coord = new JSObject();
				coord.put("x", userEntry.getValue()[0]);
				if (userEntry.getValue().length == 2) {
					coord.put("y", userEntry.getValue()[1]);
				} else {
					coord.put("y", 0.0);
				}

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
		ExportUtil.exportPoints(file, points);
	}

}
