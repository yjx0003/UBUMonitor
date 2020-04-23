package es.ubu.lsi.ubumonitor.clustering.algorithm;

import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFrame;

import org.apache.commons.math3.ml.distance.DistanceMeasure;

import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import com.apporiented.algorithm.clustering.visualization.DendrogramPanel;

import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.data.Distance;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.clustering.util.Tree;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;

public class HierarchicalClustering {

	private ClusteringAlgorithm algorithm = new DefaultClusteringAlgorithm();
	private DistanceMeasure distance = Distance.MANHATTAN_DISTANCE.getInstance();

	public Tree<String> execute(List<EnrolledUser> enrolledUsers, List<DataCollector> dataCollectors) {
		List<UserData> usersData = enrolledUsers.stream().map(UserData::new).collect(Collectors.toList());
		dataCollectors.forEach(collector -> collector.collect(usersData));

		String[] names = enrolledUsers.stream().map(EnrolledUser::getFirstname).toArray(String[]::new);
		double[][] distances = new double[usersData.size()][usersData.size()];

		for (int i = 0; i < distances.length; i++) {
			for (int j = 0; j < distances.length; j++) {
				distances[i][j] = distance.compute(usersData.get(i).getPoint(), usersData.get(j).getPoint());
			}
		}
		
		Cluster cluster = algorithm.performClustering(distances, names, new AverageLinkageStrategy());
		
		DendrogramPanel dp = new DendrogramPanel();
		dp.setModel(cluster);
		JFrame frame = new JFrame();
		frame.setSize(500, 500);
		frame.add(dp);
		frame.setVisible(true);
		frame.toFront();

		return generateTree(cluster);

	}

	private Tree<String> generateTree(Cluster cluster) {
		return null;
	}

}
