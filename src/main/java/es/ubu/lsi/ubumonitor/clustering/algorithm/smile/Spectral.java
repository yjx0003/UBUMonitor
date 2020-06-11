package es.ubu.lsi.ubumonitor.clustering.algorithm.smile;

import org.apache.commons.math3.ml.clustering.Clusterer;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import smile.clustering.PartitionClustering;
import smile.clustering.SpectralClustering;

/**
 * Algoritmo Spectral Clustering de Smile.
 * 
 * @author Xing Long Ji
 *
 */
public class Spectral extends Algorithm {

	private static final String NAME = "Spectral Clustering";
	private static final String LIBRARY = "Smile";

	/**
	 * Constructor del algoritmo Spectral Clustering.
	 */
	public Spectral() {
		super(NAME, LIBRARY);
		addParameter(ClusteringParameter.NUM_CLUSTER, 3);
		addParameter(ClusteringParameter.SMOOTH, 0.5);
		addParameter(ClusteringParameter.MAX_ITERATIONS, 50);
		addParameter(ClusteringParameter.TOLERANCE, 0.001);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Clusterer<UserData> getClusterer() {
		int k = getParameters().getValue(ClusteringParameter.NUM_CLUSTER);
		double sigma = getParameters().getValue(ClusteringParameter.SMOOTH);
		int maxIter = getParameters().getValue(ClusteringParameter.MAX_ITERATIONS);
		double tol = getParameters().getValue(ClusteringParameter.TOLERANCE);

		checkParameter(ClusteringParameter.NUM_CLUSTER, k);
		checkParameter(ClusteringParameter.SMOOTH, sigma);
		checkParameter(ClusteringParameter.MAX_ITERATIONS, maxIter);
		checkParameter(ClusteringParameter.TOLERANCE, tol);

		return new SpectralAdapter(k, sigma, maxIter, tol);
	}

	private class SpectralAdapter extends SmileAdapter {

		private int k;
		private double sigma;
		private int maxIter;
		private double tol;

		private SpectralAdapter(int k, double sigma, int maxIter, double tol) {
			this.k = k;
			this.sigma = sigma;
			this.maxIter = maxIter;
			this.tol = tol;
		}

		@Override
		protected PartitionClustering execute(double[][] data) {
			return SpectralClustering.fit(data, k, sigma, maxIter, tol);
		}
	}
}
