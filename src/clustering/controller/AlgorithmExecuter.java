package clustering.controller;

import java.util.List;
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

	public AlgorithmExecuter(Clusterer<UserData> clusterer, List<EnrolledUser> enrolledUsers) {
		this.clusterer = clusterer;
		this.enroledUsers = enrolledUsers;
		usersData = enrolledUsers.stream().map(UserData::new).collect(Collectors.toList());
	}

	public List<UserData> execute(List<DataCollector> collectors) {
		for (DataCollector dataCollector : collectors) {
			dataCollector.collect(usersData);
		}
		List<? extends Cluster<UserData>> clusters = clusterer.cluster(usersData);
		for (int i = 0; i < clusters.size(); i++) {
			for (UserData user : clusters.get(i).getPoints()) {
				user.setCluster(i);
			}
		}
		numClusters = clusters.size();
		return usersData;
	}

	public List<UserData> execute(List<DataCollector> collectors, int n) {
		for (DataCollector dataCollector : collectors) {
			dataCollector.collect(usersData);
		}
		PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis();
		double[][] matrix = usersData.stream().map(UserData::getPoint).toArray(double[][]::new);
		double[][] reduced = pca.pca(matrix, n);
		for (int i = 0; i < reduced.length; i++) {
			usersData.get(i).setData(reduced[i]);
		}
		List<? extends Cluster<UserData>> clusters = clusterer.cluster(usersData);
		for (int i = 0; i < clusters.size(); i++) {
			for (UserData user : clusters.get(i).getPoints()) {
				user.setCluster(i);
			}
		}
		numClusters = clusters.size();
		return usersData;
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
