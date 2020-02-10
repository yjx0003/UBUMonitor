package clustering.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterer;

import clustering.controller.collector.DataCollector;
import clustering.data.UserData;
import model.EnrolledUser;

public class AlgorithmExecuter {

	private Clusterer<UserData> clusterer;

	private List<UserData> userData;

	private List<EnrolledUser> enroledUsers;

	private int numClusters;

	public AlgorithmExecuter(Clusterer<UserData> clusterer, List<EnrolledUser> enrolledUsers) {
		this.clusterer = clusterer;
		this.enroledUsers = enrolledUsers;
		userData = enrolledUsers.stream().map(UserData::new).collect(Collectors.toList());
	}

	public List<UserData> execute(List<DataCollector> collectors) {
		for (DataCollector dataCollector : collectors) {
			dataCollector.collect(userData);
		}
		List<? extends Cluster<UserData>> clusters = clusterer.cluster(userData);
		for (int i = 0; i < clusters.size(); i++) {
			for (UserData user : clusters.get(i).getPoints()) {
				user.setCluster(i);
			}
		}
		numClusters = clusters.size();
		return userData;
	}

	public Clusterer<UserData> getClusterer() {
		return clusterer;
	}

	public List<UserData> getUserData() {
		return userData;
	}

	public List<EnrolledUser> getEnroledUsers() {
		return enroledUsers;
	}

	public int getNumClusters() {
		return numClusters;
	}
}
