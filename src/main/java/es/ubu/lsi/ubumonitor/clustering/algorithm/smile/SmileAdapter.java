package es.ubu.lsi.ubumonitor.clustering.algorithm.smile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import smile.clustering.CentroidClustering;
import smile.clustering.PartitionClustering;

public abstract class SmileAdapter extends Clusterer<UserData> {

	protected SmileAdapter() {
		super(null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<? extends Cluster<UserData>> cluster(Collection<UserData> points)
			throws MathIllegalArgumentException, ConvergenceException {
		double[][] data = points.stream().map(UserData::getPoint).toArray(double[][]::new);
		PartitionClustering clustering = execute(data);
		if (clustering instanceof CentroidClustering) {
			return adaptSmile(points, (CentroidClustering<double[], double[]>) clustering);
		}
		return adaptSmile(points, clustering);
	}

	protected abstract PartitionClustering execute(double[][] data);

	private List<? extends Cluster<UserData>> adaptSmile(Collection<UserData> points,
			CentroidClustering<double[], double[]> centroidClustering) {
		List<UserData> users = new ArrayList<>(points);
		List<CentroidCluster<UserData>> result = new ArrayList<>();
		for (int i = 0; i < centroidClustering.k; i++) {
			CentroidCluster<UserData> cluster = new CentroidCluster<>(new DoublePoint(centroidClustering.centroids[i]));
			for (int j = 0; j < centroidClustering.y.length; j++) {
				if (i == centroidClustering.y[j])
					cluster.addPoint(users.get(j));
			}
			result.add(cluster);
		}
		return result;
	}

	private List<? extends Cluster<UserData>> adaptSmile(Collection<UserData> points, PartitionClustering clustering) {
		List<UserData> users = new ArrayList<>(points);
		List<Cluster<UserData>> result = new ArrayList<>();
		for (int i = 0; i < clustering.k; i++) {
			Cluster<UserData> cluster = new Cluster<>();
			for (int j = 0; j < clustering.y.length; j++) {
				if (i == clustering.y[j])
					cluster.addPoint(users.get(j));
			}
			result.add(cluster);
		}
		return result;
	}
}
