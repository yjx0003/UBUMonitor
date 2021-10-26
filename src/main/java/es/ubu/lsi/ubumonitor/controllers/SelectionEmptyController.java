package es.ubu.lsi.ubumonitor.controllers;

import es.ubu.lsi.ubumonitor.model.Course;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SelectionEmptyController {
	@FXML
	private Label label;
	
	public void init(MainController mainController, Course actualCourse) {
		WebViewTabsController webViewTabsController = mainController.getWebViewTabsController();
		label.visibleProperty().bind(webViewTabsController.getRiskTab().selectedProperty().or(webViewTabsController.getEnrollmentTab().selectedProperty()));
	}

}
