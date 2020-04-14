package es.ubu.lsi.ubumonitor.clustering.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;

public class Clustering3DChart extends AbstractChart {

	private static final Logger LOGGER = LoggerFactory.getLogger(Clustering3DChart.class);

	private List<Map<UserData, double[]>> points;

	public Clustering3DChart(ClusteringController controller) {
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
				JSArray p = new JSArray();
				p.add(entry.getValue()[0]);
				p.add(entry.getValue()[1]);
				p.add(entry.getValue()[2]);
				data.add(p);
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
