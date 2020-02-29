package clustering.controller;

import java.util.List;

import clustering.data.ClusterWrapper;
import clustering.data.UserData;

public class Connector {

	private ClusteringController clusteringController;

	private List<ClusterWrapper> clusters;

	public Connector(ClusteringController controller) {
		clusteringController = controller;
	}

	public void selectUser(int clusterIndex, int index) {
		UserData userData = clusters.get(clusterIndex).get(index);
		clusteringController.getTableView().getSelectionModel().select(userData);
		clusteringController.getTableView().scrollTo(userData);
	}

	public void setClusters(List<ClusterWrapper> clusters) {
		this.clusters = clusters;
	}

}
