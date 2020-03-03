package clustering.controller;

import java.io.ByteArrayInputStream;
import java.util.Map.Entry;

import clustering.data.UserData;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.EnrolledUser;

public class UserDataController {

	@FXML
	private Label labelUser;

	@FXML
	private ImageView imageView;

	@FXML
	private TableView<Entry<String, Double>> tableView;
	
	@FXML
	private TableColumn<Entry<String, Double>, String> columnComponent;
	
	@FXML
	private TableColumn<Entry<String, Double>, Number> columnValue;

	public void init(UserData userData) {
		EnrolledUser enrolledUser = userData.getEnrolledUser();
		labelUser.setText(enrolledUser.getFullName());
		imageView.setImage(new Image(new ByteArrayInputStream(enrolledUser.getImageBytes())));
		tableView.getItems().setAll(userData.getComponents().entrySet());
		columnComponent.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getKey()));
		columnValue.setCellValueFactory(e -> new SimpleDoubleProperty(e.getValue().getValue()));
	}

}
