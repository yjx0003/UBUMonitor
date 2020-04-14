package es.ubu.lsi.ubumonitor.clustering.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterer;

import com.jujutsu.tsne.PrincipalComponentAnalysis;

import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
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

	public static List<Map<UserData, double[]>> clustersTo(int dim, List<ClusterWrapper> clusters) {
		List<double[]> centers = new ArrayList<>();
		for (ClusterWrapper clusterWrapper : clusters) {
			double[] center = clusterWrapper.getCenter();
			if (center != null) {
				centers.add(center);
			}
		}
		double[][] matrix = Stream
				.concat(clusters.stream().flatMap(ClusterWrapper::stream).map(UserData::getPoint), centers.stream())
				.toArray(double[][]::new);

		PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis();
		List<Map<UserData, double[]>> points = new ArrayList<>();

		if (matrix[0].length > dim) {
			matrix = pca.pca(matrix, dim);
		}
		int i = 0;
		for (List<UserData> list : clusters) {
			Map<UserData, double[]> map = new LinkedHashMap<>();
			for (UserData userData : list) {
				map.put(userData, matrix[i++]);
			}
			points.add(map);
		}
		
		// Add centroides
		for (int j = 0; i < matrix.length; i++, j++) {
			points.get(j).put(null, matrix[i]);
		}

		return points;
	}
}
