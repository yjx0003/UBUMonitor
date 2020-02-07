package clustering.algorithm;

import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

public class DBSCAN extends Algorithm {

	public static final String NAME = "DBSCAN";

	public DBSCAN() {
		super(NAME);
		addParameter("clustering.eps", 1.5);
		addParameter("clustering.minPts", 5);
		addParameter("clustering.distance", Algorithms.DISTANCES_LIST.get(0));
	}

	@Override
	public <T extends Clusterable> Clusterer<T> getClusterer() {
		double eps = getParameters().getValue("clustering.eps");
		int minPts = getParameters().getValue("clustering.minPts");
		DistanceMeasure distance = getParameters().getValue("clustering.distance");
		return new DBSCANClusterer<>(eps, minPts, distance);
	}
}
