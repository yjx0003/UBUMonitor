package es.ubu.lsi.ubumonitor.clustering.algorithm;

import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.FuzzyKMeansClusterer;

import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.Distance;
import es.ubu.lsi.ubumonitor.clustering.exception.IllegalParamenterException;

public class FuzzyKMeans extends Algorithm {

	public static final String NAME = "FuzzyKMeans";

	public FuzzyKMeans() {
		super(NAME);
		addParameter(ClusteringParameter.NUM_CLUSTER, 3);
		addParameter(ClusteringParameter.FUZZINESS, 2.0);
		addParameter(ClusteringParameter.MAX_ITERATIONS, 10);
		addParameter(ClusteringParameter.DISTANCE_TYPE, Distance.MANHATTAN_DISTANCE);
	}

	@Override
	public <T extends Clusterable> Clusterer<T> getClusterer() {
		int k = getParameters().getValue(ClusteringParameter.NUM_CLUSTER);
		int fuzziness = getParameters().getValue(ClusteringParameter.FUZZINESS);
		int max = getParameters().getValue(ClusteringParameter.MAX_ITERATIONS);
		Distance distance = getParameters().getValue(ClusteringParameter.DISTANCE_TYPE);

		if (!ClusteringParameter.NUM_CLUSTER.isValid(k))
			throw new IllegalParamenterException(ClusteringParameter.NUM_CLUSTER, k);

		if (!ClusteringParameter.FUZZINESS.isValid(fuzziness))
			throw new IllegalParamenterException(ClusteringParameter.FUZZINESS, fuzziness);

		if (!ClusteringParameter.MAX_ITERATIONS.isValid(max))
			throw new IllegalParamenterException(ClusteringParameter.MAX_ITERATIONS, max);

		return new FuzzyKMeansClusterer<>(k, fuzziness, max, distance.getInstance());
	}
}
