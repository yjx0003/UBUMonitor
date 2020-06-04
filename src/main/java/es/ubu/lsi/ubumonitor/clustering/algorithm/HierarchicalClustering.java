package es.ubu.lsi.ubumonitor.clustering.algorithm;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.ml.distance.DistanceMeasure;

import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;

import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.data.Distance;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.clustering.util.Tree;
import es.ubu.lsi.ubumonitor.clustering.util.Tree.Node;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;

public class HierarchicalClustering {

	private ClusteringAlgorithm algorithm = new DefaultClusteringAlgorithm();
	private DistanceMeasure distance;

	public void setDistance(Distance distance) {
		this.distance = distance.getInstance();
	}

	public Tree<String> execute(List<EnrolledUser> enrolledUsers, List<DataCollector> dataCollectors) {
		List<UserData> usersData = enrolledUsers.stream().map(UserData::new).collect(Collectors.toList());
		dataCollectors.forEach(collector -> collector.collect(usersData));

		String[] names = enrolledUsers.stream().map(EnrolledUser::getFullName).toArray(String[]::new);
		double[][] distances = new double[usersData.size()][usersData.size()];

		for (int i = 0; i < distances.length; i++) {
			for (int j = 0; j < distances.length; j++) {
				distances[i][j] = distance.compute(usersData.get(i).getPoint(), usersData.get(j).getPoint());
			}
		}

		Cluster cluster = algorithm.performClustering(distances, names, new AverageLinkageStrategy());

		Tree<String> tree = new Tree<>(cluster.getName());
		generateTree(cluster, tree.getRoot());
		return tree;
	}

	private void generateTree(Cluster cluster, Node<String> node) {
		node.putInfo("distance", cluster.getDistance().toString());
		for (Cluster children : cluster.getChildren()) {
			generateTree(children, node.addChildren(children.getName()));
		}
	}

}
