package es.ubu.lsi.ubumonitor.clustering.chart;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.clustering.controller.AlgorithmExecuter;
import es.ubu.lsi.ubumonitor.clustering.controller.ClusteringController;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;

public class Scatter3DChart extends ClusteringChart {

	private static final Logger LOGGER = LoggerFactory.getLogger(Scatter3DChart.class);

	private List<Map<UserData, double[]>> points;

	public Scatter3DChart(ClusteringController controller) {
		super(controller.getWebView3DScatter());
		getWebEngine().load(getClass().getResource("/graphics/Cluster3DChart.html").toExternalForm());
	}

	public void updateChart(List<ClusterWrapper> clusters) {
		points = AlgorithmExecuter.clustersTo(3, clusters);

		JSArray series = new JSArray();
		for (int i = 0; i < clusters.size(); i++) {
			JSObject serie = new JSObject();
			serie.putWithQuote("name", clusters.get(i).getName());
			JSArray data = new JSArray();
			for (Entry<UserData, double[]> entry : points.get(i).entrySet()) {
				double[] value = entry.getValue();
				JSArray point = new JSArray();
				point.add(value[0]);
				point.add(value.length > 1 ? value[1] : 0.0);
				point.add(value.length > 2 ? value[2] : 0.0);
				data.add(point);
			}
			serie.put("data", data);
			series.add(serie);
		}

		LOGGER.debug("3D series: {}", series);
		getWebEngine().executeScript("updateChart(" + series + ")");
	}

	@Override
	protected void exportData(File file) throws IOException {

	}

}
