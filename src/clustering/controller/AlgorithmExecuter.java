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

	public AlgorithmExecuter(Clusterer<UserData> clusterer, List<EnrolledUser> enrolledUsers,
			List<DataCollector> dataCollectors) {
		this.clusterer = clusterer;
		usersData = enrolledUsers.stream().map(UserData::new).collect(Collectors.toList());
		dataCollectors.forEach(collector -> collector.collect(usersData));
	}

	public List<List<UserData>> execute(int dimension) {
		PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis();
		double[][] matrix = usersData.stream().map(UserData::getPoint).toArray(double[][]::new);
		if (dimension > 0) {
			matrix = pca.pca(matrix, dimension);
			for (int i = 0; i < matrix.length; i++) {
				usersData.get(i).setData(matrix[i]);
			}
		}
		List<? extends Cluster<UserData>> clusters = clusterer.cluster(usersData);
		List<List<UserData>> users = new ArrayList<>();
		for (int i = 0; i < clusters.size(); i++) {
			List<UserData> list = new ArrayList<>();
			for (UserData user : clusters.get(i).getPoints()) {
				list.add(user);
				user.setCluster(i);
			}
			users.add(list);
		}
		return users;
	}

	public Clusterer<UserData> getClusterer() {
		return clusterer;
	}

	public List<UserData> getUserData() {
		return usersData;
	}

	public static List<Map<UserData, double[]>> clustersTo2D(List<List<UserData>> clusters) {
		double[][] matrix = clusters.stream().flatMap(List::stream).map(UserData::getPoint).toArray(double[][]::new);
		PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis();
		List<Map<UserData, double[]>> points = new ArrayList<>();
		matrix = pca.pca(matrix, 2);
		
		int i = 0;
		for (List<UserData> list : clusters) {
			Map<UserData, double[]> map = new HashMap<>();
			for (UserData userData : list) {
				double[] point = matrix[i++];
				map.put(userData, point);
			}
			points.add(map);
		}
		return points;
	}
}
