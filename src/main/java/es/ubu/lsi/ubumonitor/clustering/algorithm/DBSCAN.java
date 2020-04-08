package es.ubu.lsi.ubumonitor.clustering.algorithm;

import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;

import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.Distance;
import es.ubu.lsi.ubumonitor.clustering.exception.IllegalParamenterException;

public class DBSCAN extends Algorithm {

	private static final String NAME = "DBSCAN";

	public DBSCAN() {
		super(NAME);
		addParameter(ClusteringParameter.EPS, 0.4);
		addParameter(ClusteringParameter.MIN_POINTS, 1);
		addParameter(ClusteringParameter.DISTANCE_TYPE, Distance.MANHATTAN_DISTANCE);
	}

	@Override
	public <T extends Clusterable> Clusterer<T> getClusterer() {
		double eps = getParameters().getValue(ClusteringParameter.EPS);
		int minPts = getParameters().getValue(ClusteringParameter.MIN_POINTS);
		Distance distance = getParameters().getValue(ClusteringParameter.DISTANCE_TYPE);

		if (!ClusteringParameter.EPS.isValid(eps))
			throw new IllegalParamenterException(ClusteringParameter.EPS, eps);

		if (!ClusteringParameter.MIN_POINTS.isValid(minPts))
			throw new IllegalParamenterException(ClusteringParameter.MIN_POINTS, minPts);

		return new DBSCANClusterer<>(eps, minPts, distance.getInstance());
	}
}
