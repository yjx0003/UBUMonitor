package es.ubu.lsi.ubumonitor.clustering.algorithm.smile;

import org.apache.commons.math3.ml.clustering.Clusterer;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import smile.clustering.PartitionClustering;

public class DBSCANSmile extends Algorithm {

	private static final String NAME = "DBSCAN (Smile)";

	public DBSCANSmile() {
		super(NAME);
		addParameter(ClusteringParameter.EPS, 0.4);
		addParameter(ClusteringParameter.MIN_POINTS, 1);
	}

	@Override
	public Clusterer<UserData> getClusterer() {
		double eps = getParameters().getValue(ClusteringParameter.EPS);
		int minPts = getParameters().getValue(ClusteringParameter.MIN_POINTS);

		checkParameter(ClusteringParameter.EPS, eps);
		checkParameter(ClusteringParameter.MIN_POINTS, minPts);

		return new DBSCANAdapter(eps, minPts);
	}

	private class DBSCANAdapter extends SmileAdapter {

		private double radius;
		private int minPts;

		public DBSCANAdapter(double radius, int minPts) {
			this.radius = radius;
			this.minPts = minPts;
		}

		@Override
		protected PartitionClustering execute(double[][] data) {
			return smile.clustering.DBSCAN.fit(data, minPts, radius);
		}
	}
}
