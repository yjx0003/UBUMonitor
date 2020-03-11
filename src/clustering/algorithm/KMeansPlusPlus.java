package clustering.algorithm;

import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

import clustering.data.Distance;

public class KMeansPlusPlus extends Algorithm {

	public static final String NAME = "KMeansPlusPlus";

	public KMeansPlusPlus() {
		super(NAME);
		addParameter("clustering.numberOfClusters", 3);
		addParameter("clustering.maxIterations", 10);
		addParameter("clustering.distance", Distance.MANHATTAN_DISTANCE);
	}

	@Override
	public <T extends Clusterable> Clusterer<T> getClusterer() {
		int k = getParameters().getValue("clustering.numberOfClusters");
		int max = getParameters().getValue("clustering.maxIterations");
		Distance distance = getParameters().getValue("clustering.distance");
		return new KMeansPlusPlusClusterer<>(k, max, distance.getInstance());
	}
}
