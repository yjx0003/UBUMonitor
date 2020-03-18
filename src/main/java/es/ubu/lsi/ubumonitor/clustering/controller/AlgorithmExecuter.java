package es.ubu.lsi.ubumonitor.clustering.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

import com.jujutsu.tsne.PrincipalComponentAnalysis;

import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.Distance;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;

public class AlgorithmExecuter {

	private Clusterer<UserData> clusterer;

	private List<UserData> usersData;

	public AlgorithmExecuter(Clusterer<UserData> clusterer, List<EnrolledUser> enrolledUsers,
			List<DataCollector> dataCollectors) {
		this.clusterer = clusterer;
		usersData = enrolledUsers.stream().map(UserData::new).collect(Collectors.toList());
		dataCollectors.forEach(collector -> collector.collect(usersData));
	}

	public List<ClusterWrapper> execute(int dimension) {

		if (usersData.isEmpty())
			throw new IllegalStateException("clustering.error.notUsers");

		if (usersData.get(0).getData().isEmpty())
			throw new IllegalStateException("clustering.error.notData");

		PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis();
		double[][] matrix = usersData.stream().map(UserData::getPoint).toArray(double[][]::new);
		if (dimension > 0) {
			matrix = pca.pca(matrix, dimension);
			for (int i = 0; i < matrix.length; i++) {
				usersData.get(i).setData(matrix[i]);
			}
		}
		try {
			List<? extends Cluster<UserData>> clusters = clusterer.cluster(usersData);
			List<ClusterWrapper> users = new ArrayList<>();
			for (int i = 0; i < clusters.size(); i++) {
				ClusterWrapper clusterWrapper = new ClusterWrapper(i, clusters.get(i));
				for (UserData user : clusters.get(i).getPoints()) {
					user.setCluster(clusterWrapper);
				}
				users.add(clusterWrapper);
			}
			return users;
		} catch (NumberIsTooSmallException e) {
			throw new IllegalStateException("clustering.error.lessUsersThanClusters", e);
		}
	}

	public Clusterer<UserData> getClusterer() {
		return clusterer;
	}

	public List<UserData> getUserData() {
		return usersData;
	}

	public static List<Map<UserData, double[]>> clustersTo2D(List<ClusterWrapper> clusters) {
		double[][] matrix = clusters.stream().flatMap(ClusterWrapper::stream).map(UserData::getPoint)
				.toArray(double[][]::new);
		PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis();
		List<Map<UserData, double[]>> points = new ArrayList<>();
		if (matrix[0].length > 2) {
			matrix = pca.pca(matrix, 2);
		}
		int i = 0;
		for (List<UserData> list : clusters) {
			Map<UserData, double[]> map = new LinkedHashMap<>();
			for (UserData userData : list) {
				map.put(userData, matrix[i++]);
			}
			points.add(map);
		}
		return points;
	}

	public static Map<UserData, Double> silhouette(List<ClusterWrapper> clusters, Distance distanceType) {

		List<UserData> users = clusters.stream().flatMap(ClusterWrapper::stream).collect(Collectors.toList());
		Map<UserData, Double> ai = new LinkedHashMap<>(users.size());
		Map<UserData, Double> bi = new LinkedHashMap<>(users.size());
		DistanceMeasure distance = distanceType.getInstance();

		for (ClusterWrapper cluster : clusters) {
			for (UserData userData : cluster) {
				double selfDissimilarity = 0.0;
				for (UserData userData2 : cluster) {
					selfDissimilarity += distance.compute(userData.getPoint(), userData2.getPoint());
				}
				selfDissimilarity /= cluster.size();
				ai.put(userData, selfDissimilarity);

				for (ClusterWrapper otherCluster : clusters) {
					if (!otherCluster.equals(userData.getCluster())) {
						double otherDissimilarity = 0.0;
						for (UserData userData3 : otherCluster) {
							otherDissimilarity += distance.compute(userData.getPoint(), userData3.getPoint());
						}
						otherDissimilarity /= otherCluster.size();
						if (otherDissimilarity < bi.getOrDefault(userData, Double.POSITIVE_INFINITY)) {
							bi.put(userData, otherDissimilarity);
						}
					}
				}
			}
		}

		Map<UserData, Double> sihouette = new LinkedHashMap<>(users.size());
		for (UserData userData : users) {
			double a = ai.get(userData);
			double b = bi.getOrDefault(userData, Double.NaN);
			double s;
			if (Double.isNaN(b)) {
				s = 1.0;
			} else {
				s = (b - a) / Math.max(a, b);
			}
			sihouette.put(userData, s);
		}

		return sihouette;
	}
}
