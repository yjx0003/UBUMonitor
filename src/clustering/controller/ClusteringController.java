package clustering.controller;

import org.controlsfx.control.PropertySheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;

public class ClusteringController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClusteringController.class);

	@FXML
	private PropertySheet propertySheet;

	@FXML
	private Button executeBtn;

	@FXML
	private WebView webView;

}
