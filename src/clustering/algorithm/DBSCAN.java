package es.ubu.lsi.ubumonitor.clustering.algorithm;

import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;

import es.ubu.lsi.ubumonitor.clustering.data.Distance;

public class DBSCAN extends Algorithm {

	public static final String NAME = "DBSCAN";

	public DBSCAN() {
		super(NAME);
		addParameter("clustering.eps", 0.4);
		addParameter("clustering.minPts", 1);
		addParameter("clustering.distance", Distance.MANHATTAN_DISTANCE);
	}

	@Override
	public <T extends Clusterable> Clusterer<T> getClusterer() {
		double eps = getParameters().getValue("clustering.eps");
		int minPts = getParameters().getValue("clustering.minPts");
		Distance distance = getParameters().getValue("clustering.distance");
		return new DBSCANClusterer<>(eps, minPts, distance.getInstance());
	}
}
