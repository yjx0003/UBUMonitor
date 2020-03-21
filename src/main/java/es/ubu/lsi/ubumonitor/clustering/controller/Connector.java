package es.ubu.lsi.ubumonitor.clustering.controller;

import java.util.List;

import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import javafx.scene.web.WebEngine;

public class Connector {

	private ClusteringController clusteringController;
	private List<ClusterWrapper> clusters;

	public Connector(ClusteringController controller, WebEngine webEngine) {
		this.clusteringController = controller;
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
