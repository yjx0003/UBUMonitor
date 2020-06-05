package es.ubu.lsi.ubumonitor.clustering.algorithm.smile;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ml.clustering.Clusterer;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import smile.clustering.CentroidClustering;

public class KMeans extends Algorithm {

	private static final String NAME = "K-Means";
	private static final String LIBRARY = "Smile";

	public KMeans() {
		super(NAME, LIBRARY);
		addParameter(ClusteringParameter.NUM_CLUSTER, 3);
		addParameter(ClusteringParameter.MAX_ITERATIONS, 50);
		addParameter(ClusteringParameter.TOLERANCE, 0.001);
	}

	@Override
	public Clusterer<UserData> getClusterer() {
		int k = getParameters().getValue(ClusteringParameter.NUM_CLUSTER);
		int max = getParameters().getValue(ClusteringParameter.MAX_ITERATIONS);
		double tol = getParameters().getValue(ClusteringParameter.TOLERANCE);

		checkParameter(ClusteringParameter.NUM_CLUSTER, k);
		checkParameter(ClusteringParameter.MAX_ITERATIONS, max);
		checkParameter(ClusteringParameter.TOLERANCE, tol);

		return new KMeansAdapter(k, max, tol);
	}

	private class KMeansAdapter extends SmileAdapter {

		private int k;
		private int maxIter;
		private double tol;

		private KMeansAdapter(int k, int maxIter, double tol) {
			this.k = k;
			this.maxIter = maxIter;
			this.tol = tol;
		}

		@Override
		protected CentroidClustering<double[], double[]> execute(double[][] data) {
			if (data.length < k)
				throw new NumberIsTooSmallException(data.length, k, true);
			return smile.clustering.KMeans.fit(data, k, maxIter, tol);
		}
	}
}
