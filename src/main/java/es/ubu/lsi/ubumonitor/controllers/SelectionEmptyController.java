package es.ubu.lsi.ubumonitor.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SelectionEmptyController {
	@FXML
	private Label label;
	
	public void init(MainController mainController) {
		label.visibleProperty().bind(mainController.getWebViewTabsController().getRiskTab().selectedProperty());
	}
}
