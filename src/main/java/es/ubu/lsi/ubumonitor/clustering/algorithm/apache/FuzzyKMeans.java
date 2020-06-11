package es.ubu.lsi.ubumonitor.clustering.algorithm.apache;

import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.FuzzyKMeansClusterer;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.Distance;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;

/**
 * Algoritmo FuzzyKMeans de Apache.
 * 
 * @author Xing Long Ji
 *
 */
public class FuzzyKMeans extends Algorithm {

	private static final String NAME = "FuzzyKMeans";
	private static final String LIBRARY = "Apache";

	/**
	 * Constructor del algoritmo FuzzyKMeans.
	 */
	public FuzzyKMeans() {
		super(NAME, LIBRARY);
		addParameter(ClusteringParameter.NUM_CLUSTER, 3);
		addParameter(ClusteringParameter.FUZZINESS, 2.0);
		addParameter(ClusteringParameter.MAX_ITERATIONS, 50);
		addParameter(ClusteringParameter.DISTANCE_TYPE, Distance.MANHATTAN_DISTANCE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Clusterer<UserData> getClusterer() {
		int k = getParameters().getValue(ClusteringParameter.NUM_CLUSTER);
		double fuzziness = getParameters().getValue(ClusteringParameter.FUZZINESS);
		int max = getParameters().getValue(ClusteringParameter.MAX_ITERATIONS);
		Distance distance = getParameters().getValue(ClusteringParameter.DISTANCE_TYPE);

		checkParameter(ClusteringParameter.NUM_CLUSTER, k);
		checkParameter(ClusteringParameter.FUZZINESS, fuzziness);
		checkParameter(ClusteringParameter.MAX_ITERATIONS, max);

		return new FuzzyKMeansClusterer<>(k, fuzziness, max, distance.getInstance());
	}
}
