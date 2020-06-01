package es.ubu.lsi.ubumonitor.clustering.algorithm.smile;

import org.apache.commons.math3.ml.clustering.Clusterer;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import smile.clustering.PartitionClustering;

public class DENCLUE extends Algorithm {

	private static final String NAME = "DENCLUE (Smile)";

	public DENCLUE() {
		super(NAME);
		addParameter(ClusteringParameter.SMOOTH, 0.3);
		addParameter(ClusteringParameter.MAX_NUM_CLUSTER, 3);
		addParameter(ClusteringParameter.TOLERANCE, 0.01);
		addParameter(ClusteringParameter.MIN_POINTS, 1);

	}

	@Override
	public Clusterer<UserData> getClusterer() {
		double sigma = getParameters().getValue(ClusteringParameter.SMOOTH);
		int m = getParameters().getValue(ClusteringParameter.MAX_NUM_CLUSTER);
		double tol = getParameters().getValue(ClusteringParameter.TOLERANCE);
		int minPts = getParameters().getValue(ClusteringParameter.MIN_POINTS);

		checkParameter(ClusteringParameter.SMOOTH, sigma);
		checkParameter(ClusteringParameter.MAX_NUM_CLUSTER, m);
		checkParameter(ClusteringParameter.TOLERANCE, tol);
		checkParameter(ClusteringParameter.MIN_POINTS, minPts);

		return new DENCLUEAdapter(sigma, m, tol, minPts);
	}

	private class DENCLUEAdapter extends SmileAdapter {

		private double sigma;
		private int m;
		private double tol;
		private int minPts;

		private DENCLUEAdapter(double sigma, int m, double tol, int minPts) {
			this.sigma = sigma;
			this.m = m;
			this.tol = tol;
			this.minPts = minPts;
		}

		@Override
		protected PartitionClustering execute(double[][] data) {
			return smile.clustering.DENCLUE.fit(data, sigma, m, tol, minPts);
		}
	}
}
