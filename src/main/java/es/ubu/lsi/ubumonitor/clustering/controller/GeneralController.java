package es.ubu.lsi.ubumonitor.clustering.controller;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import javafx.fxml.FXML;

public class GeneralController {

	@FXML
	private ClusteringController classicController;

	@FXML
	private HierarchicalController hierarchicalController;

	public void init(MainController mainController) {
		classicController.init(mainController);
		hierarchicalController.init(mainController);
	}

}
