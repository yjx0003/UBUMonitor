package es.ubu.lsi.ubumonitor.clustering.algorithm;

import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.Distance;
import es.ubu.lsi.ubumonitor.clustering.exception.IllegalParamenterException;

public class KMeansPlusPlus extends Algorithm {

	public static final String NAME = "KMeansPlusPlus";

	public KMeansPlusPlus() {
		super(NAME);
		addParameter(ClusteringParameter.NUM_CLUSTER, 3);
		addParameter(ClusteringParameter.MAX_ITERATIONS, 10);
		addParameter(ClusteringParameter.DISTANCE_TYPE, Distance.MANHATTAN_DISTANCE);
	}

	@Override
	public <T extends Clusterable> Clusterer<T> getClusterer() {
		int k = getParameters().getValue(ClusteringParameter.NUM_CLUSTER);
		int max = getParameters().getValue(ClusteringParameter.MAX_ITERATIONS);
		Distance distance = getParameters().getValue(ClusteringParameter.DISTANCE_TYPE);
		
		if (!ClusteringParameter.NUM_CLUSTER.isValid(k))
			throw new IllegalParamenterException(ClusteringParameter.NUM_CLUSTER, k);
		
		if (!ClusteringParameter.MAX_ITERATIONS.isValid(max))
			throw new IllegalParamenterException(ClusteringParameter.MAX_ITERATIONS, max);
		
		return new KMeansPlusPlusClusterer<>(k, max, distance.getInstance());
	}
}
