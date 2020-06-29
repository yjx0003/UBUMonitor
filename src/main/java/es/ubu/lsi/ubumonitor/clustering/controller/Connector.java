package es.ubu.lsi.ubumonitor.clustering.controller;

import java.util.List;

import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import javafx.scene.control.TableView;
import javafx.scene.web.WebEngine;

public class Connector {

	private ClusteringController clusteringController;
	private List<ClusterWrapper> clusters;

	public Connector(ClusteringController controller, WebEngine webEngine) {
		this.clusteringController = controller;
	}

	public void selectUser(int clusterIndex, int index) {
		if (clusterIndex < clusters.size()) {
			UserData userData = clusters.get(clusterIndex).get(index);
			TableView<UserData> tableView = clusteringController.getClusteringTable().getTableView();
			tableView.getSelectionModel().select(userData);
			tableView.scrollTo(userData);
		}
	}

	public void setClusters(List<ClusterWrapper> clusters) {
		this.clusters = clusters;
	}

}
