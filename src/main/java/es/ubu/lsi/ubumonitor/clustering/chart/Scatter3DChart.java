package es.ubu.lsi.ubumonitor.clustering.chart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.clustering.controller.AlgorithmExecuter;
import es.ubu.lsi.ubumonitor.clustering.controller.PartitionalClusteringController;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.clustering.util.ExportUtil;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;

/**
 * Clase que gestiona una diagrama de dispersión 3D.
 * 
 * @author Xing Long Ji
 *
 */
public class Scatter3DChart extends ClusteringChart {

	private static final Logger LOGGER = LoggerFactory.getLogger(Scatter3DChart.class);

	private List<Map<UserData, double[]>> points;

	public Scatter3DChart(PartitionalClusteringController controller) {
		super(controller.getWebView3DScatter());
		getWebEngine().load(getClass().getResource("/graphics/Cluster3DChart.html").toExternalForm());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateChart(List<ClusterWrapper> clusters) {
		try { // FIX #137 catch exception generating an empty dataset
			points = AlgorithmExecuter.clustersTo(3, clusters);

			JSArray series = new JSArray();
			int total = clusters.stream().mapToInt(ClusterWrapper::size).sum();
			for (int i = 0; i < clusters.size(); i++) {
				ClusterWrapper cluster = clusters.get(i);
				JSObject serie = new JSObject();
				serie.putWithQuote("name", getLegend(cluster, total));
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
		} catch (Exception e) {
			LOGGER.error("Error updating chart 3D", e);
			getWebEngine().executeScript("updateChart()");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void exportData(File file) throws IOException {
		String[] head = new String[] { "UserId", "FullName", "Cluster", "X", "Y", "Z" };
		List<List<Object>> data = new ArrayList<>();
		for (Map<UserData, double[]> cluster : points) {
			for (Entry<UserData, double[]> entry : cluster.entrySet()) {
				UserData userData = entry.getKey();
				if (userData == null)
					continue;
				double[] point = entry.getValue();
				EnrolledUser enrolledUser = userData.getEnrolledUser();
				List<Object> row = new ArrayList<>();
				row.add(enrolledUser.getId());
				row.add(enrolledUser.getFullName());
				row.add(userData.getCluster().getName());
				row.add(point[0]);
				row.add(point.length > 1 ? point[1] : 0.0);
				row.add(point.length > 2 ? point[2] : 0.0);
				data.add(row);
			}
		}
		ExportUtil.exportCSV(file, head, data);
	}

}
