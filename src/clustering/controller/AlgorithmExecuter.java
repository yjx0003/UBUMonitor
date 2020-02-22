package clustering.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterer;

import com.jujutsu.tsne.PrincipalComponentAnalysis;

import clustering.controller.collector.DataCollector;
import clustering.data.UserData;
import model.EnrolledUser;

public class AlgorithmExecuter {

	private Clusterer<UserData> clusterer;

	private List<UserData> usersData;

	private List<EnrolledUser> enroledUsers;

	private int numClusters;

	private List<Map<UserData, double[]>> pointsList;

	public AlgorithmExecuter(Clusterer<UserData> clusterer, List<EnrolledUser> enrolledUsers) {
		this.clusterer = clusterer;
		this.enroledUsers = enrolledUsers;
		usersData = enrolledUsers.stream().map(UserData::new).collect(Collectors.toList());
		pointsList = new ArrayList<>();
	}

	public List<UserData> execute(List<DataCollector> collectors) {
		return execute(collectors, 0);
	}

	public List<UserData> execute(List<DataCollector> collectors, int n) {
		for (DataCollector dataCollector : collectors) {
			dataCollector.collect(usersData);
		}
		PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis();
		double[][] matrix = usersData.stream().map(UserData::getPoint).toArray(double[][]::new);
		if (n > 0) {
			matrix = pca.pca(matrix, n);
			for (int i = 0; i < matrix.length; i++) {
				usersData.get(i).setData(matrix[i]);
			}
		}
		List<? extends Cluster<UserData>> clusters = clusterer.cluster(usersData);
		for (int i = 0; i < clusters.size(); i++) {
			for (UserData user : clusters.get(i).getPoints()) {
				user.setCluster(i);
			}
		}
		numClusters = clusters.size();
		
		double[][] points = pca.pca(matrix, 2);
		for (Cluster<UserData> clusterUsers : clusters) {
			Map<UserData, double[]> group = new HashMap<>();
			for (UserData user : clusterUsers.getPoints()) {
				double[] point = points[usersData.indexOf(user)];
				group.put(user, point);
			}
			pointsList.add(group);
		}
		return usersData;
	}
	
	public List<Map<UserData, double[]>> getPointsList() {
		return pointsList;
	}

	public Clusterer<UserData> getClusterer() {
		return clusterer;
	}

	public List<UserData> getUserData() {
		return usersData;
	}

	public List<EnrolledUser> getEnroledUsers() {
		return enroledUsers;
	}

	public int getNumClusters() {
		return numClusters;
	}
}
