package es.ubu.lsi.ubumonitor.clustering.controller;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import javafx.fxml.FXML;

/**
 * Controlador general de la parte de clustering.
 * 
 * @author Xing Long Ji
 *
 */
public class GeneralController {

	@FXML
	private ClusteringController classicController;

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
