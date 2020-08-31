package es.ubu.lsi.ubumonitor.clustering.algorithm.apache;

import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.Distance;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;

/**
 * Algoritmo DBSCAN de Apache.
 * 
 * @author Xing Long Ji
 *
 */
public class DBSCAN extends Algorithm {

	private static final String NAME = "DBSCAN";
	private static final String LIBRARY = "Apache";

	/**
	 * Constructor del algoritmo DBSCAN.
	 */
	public DBSCAN() {
		super(NAME, LIBRARY);
		addParameter(ClusteringParameter.EPS, 0.4);
		addParameter(ClusteringParameter.MIN_POINTS, 1);
		addParameter(ClusteringParameter.DISTANCE_TYPE, Distance.MANHATTAN_DISTANCE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Clusterer<UserData> getClusterer() {
		double eps = getParameters().getValue(ClusteringParameter.EPS);
		int minPts = getParameters().getValue(ClusteringParameter.MIN_POINTS);
		Distance distance = getParameters().getValue(ClusteringParameter.DISTANCE_TYPE);

		checkParameter(ClusteringParameter.EPS, eps);
		checkParameter(ClusteringParameter.MIN_POINTS, minPts);

		return new DBSCANClusterer<>(eps, minPts, distance.getInstance());
	}
}
