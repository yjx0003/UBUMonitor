package controllers;
import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.control.PropertySheet;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

public class ConfigurationController implements Initializable{

	private Stage stage;
	@FXML 
	PropertySheet propertySheet;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		propertySheet.getItems().addAll(Controller.getInstance().getMainConfiguration().getProperties());
	}

	/**
	 * @param stage the stage to set
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}


}
