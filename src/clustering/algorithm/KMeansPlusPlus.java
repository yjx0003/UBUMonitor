package clustering.algorithm;

import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

public class KMeansPlusPlus extends Algorithm {

	public static final String NAME = "KMeansPlusPlus";

	public KMeansPlusPlus() {
		super(NAME);
		addParameter("clustering.numberOfClusters", 4);
		addParameter("clustering.maxIterations", 10);
		addParameter("clustering.distance", Algorithms.DISTANCES_LIST.get(0));
	}

	@Override
	public <T extends Clusterable> Clusterer<T> getClusterer() {
		int k = getParameters().getValue("clustering.numberOfClusters");
		int max = getParameters().getValue("clustering.maxIterations");
		DistanceMeasure distance = getParameters().getValue("clustering.distance");
		return new KMeansPlusPlusClusterer<>(k, max, distance);
	}
}
