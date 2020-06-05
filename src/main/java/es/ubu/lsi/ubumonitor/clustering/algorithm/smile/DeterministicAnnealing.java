package es.ubu.lsi.ubumonitor.clustering.algorithm.smile;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ml.clustering.Clusterer;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import smile.clustering.CentroidClustering;

public class DeterministicAnnealing extends Algorithm {

	private static final String NAME = "DeterministicAnnealing";
	private static final String LIBRARY = "Smile";

	public DeterministicAnnealing() {
		super(NAME, LIBRARY);
		addParameter(ClusteringParameter.MAX_NUM_CLUSTER, 5);
		addParameter(ClusteringParameter.ANNELING_CONTROL, 0.9);
		addParameter(ClusteringParameter.MAX_ITERATIONS, 50);
		addParameter(ClusteringParameter.TOLERANCE, 0.001);
		addParameter(ClusteringParameter.SPLIT_TOLERANCE, 0.01);
	}

	@Override
	public Clusterer<UserData> getClusterer() {
		int kmax = getParameters().getValue(ClusteringParameter.MAX_NUM_CLUSTER);
		double alpha = getParameters().getValue(ClusteringParameter.ANNELING_CONTROL);
		int max = getParameters().getValue(ClusteringParameter.MAX_ITERATIONS);
		double tol = getParameters().getValue(ClusteringParameter.TOLERANCE);
		double splitTol = getParameters().getValue(ClusteringParameter.SPLIT_TOLERANCE);

		checkParameter(ClusteringParameter.MAX_NUM_CLUSTER, kmax);
		checkParameter(ClusteringParameter.ANNELING_CONTROL, alpha);
		checkParameter(ClusteringParameter.MAX_ITERATIONS, max);
		checkParameter(ClusteringParameter.TOLERANCE, tol);
		checkParameter(ClusteringParameter.SPLIT_TOLERANCE, splitTol);

		return new DeterministicAnnealingAdapter(kmax, alpha, max, tol, splitTol);
	}

	private class DeterministicAnnealingAdapter extends SmileAdapter {

		private int kmax;
		private double alpha;
		private int maxIter;
		private double tol;
		private double splitTol;

		public DeterministicAnnealingAdapter(int kmax, double alpha, int maxIter, double tol, double splitTol) {
			this.kmax = kmax;
			this.alpha = alpha;
			this.maxIter = maxIter;
			this.tol = tol;
			this.splitTol = splitTol;
		}

		@Override
		protected CentroidClustering<double[], double[]> execute(double[][] data) {
			if (data.length < kmax)
				throw new NumberIsTooSmallException(data.length, kmax, true);
			return smile.clustering.DeterministicAnnealing.fit(data, kmax, alpha, maxIter, tol, splitTol);
		}
	}
}
