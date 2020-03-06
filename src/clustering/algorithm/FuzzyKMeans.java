package clustering.algorithm;

import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.FuzzyKMeansClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

public class FuzzyKMeans extends Algorithm {

	public static final String NAME = "FuzzyKMeans";

	public FuzzyKMeans() {
		super(NAME);
		addParameter("clustering.numberOfClusters", 3);
		addParameter("clustering.fuzziness", 2);
		addParameter("clustering.maxIterations", 10);
		addParameter("clustering.distance", Algorithms.DISTANCES_LIST.get(0));
	}

	@Override
	public <T extends Clusterable> Clusterer<T> getClusterer() {
		int k = getParameters().getValue("clustering.numberOfClusters");
		int fuzziness = getParameters().getValue("clustering.fuzziness");
		int max = getParameters().getValue("clustering.maxIterations");
		DistanceMeasure distance = getParameters().getValue("clustering.distance");

		return new FuzzyKMeansClusterer<>(k, fuzziness, max, distance);
	}
}
