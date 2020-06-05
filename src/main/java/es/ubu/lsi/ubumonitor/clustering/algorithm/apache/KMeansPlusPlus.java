package es.ubu.lsi.ubumonitor.clustering.algorithm.apache;

import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.Distance;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;

public class KMeansPlusPlus extends Algorithm {

	private static final String NAME = "K-Means++";
	private static final String LIBRARY = "Apache";

	public KMeansPlusPlus() {
		super(NAME, LIBRARY);
		addParameter(ClusteringParameter.NUM_CLUSTER, 3);
		addParameter(ClusteringParameter.MAX_ITERATIONS, 50);
		addParameter(ClusteringParameter.DISTANCE_TYPE, Distance.MANHATTAN_DISTANCE);
	}

	@Override
	public Clusterer<UserData> getClusterer() {
		int k = getParameters().getValue(ClusteringParameter.NUM_CLUSTER);
		int max = getParameters().getValue(ClusteringParameter.MAX_ITERATIONS);
		Distance distance = getParameters().getValue(ClusteringParameter.DISTANCE_TYPE);

		checkParameter(ClusteringParameter.NUM_CLUSTER, k);
		checkParameter(ClusteringParameter.MAX_ITERATIONS, max);

		return new KMeansPlusPlusClusterer<>(k, max, distance.getInstance());
	}
}
