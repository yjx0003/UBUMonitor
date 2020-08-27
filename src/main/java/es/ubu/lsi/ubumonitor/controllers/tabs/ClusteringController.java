package es.ubu.lsi.ubumonitor.controllers.tabs;

import es.ubu.lsi.ubumonitor.clustering.controller.HierarchicalController;
import es.ubu.lsi.ubumonitor.clustering.controller.PartitionalClusteringController;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import javafx.fxml.FXML;

/**
 * Controlador general de la parte de clustering.
 * 
 * @author Xing Long Ji
 *
 */
public class ClusteringController {

	@FXML
	private PartitionalClusteringController classicController;

	@FXML
	private HierarchicalController hierarchicalController;

	/**
	 * Inicializa las demas pestañade de clustering.
	 * 
	 * @param mainController controlador general de la aplicación
	 */
	public void init(MainController mainController) {
		classicController.init(mainController);
		hierarchicalController.init(mainController);
	}

}
