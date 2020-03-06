package clustering.controller;

import java.io.ByteArrayInputStream;

import clustering.data.Datum;
import clustering.data.UserData;
import controllers.AppInfo;
import controllers.I18n;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
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
	private TableView<Datum> tableView;

	@FXML
	private TableColumn<Datum, String> columnType;

	@FXML
	private TableColumn<Datum, ImageView> columnIcon;

	@FXML
	private TableColumn<Datum, String> columnItem;

	@FXML
	private TableColumn<Datum, Number> columnValue;

	public void init(UserData userData) {
		EnrolledUser enrolledUser = userData.getEnrolledUser();
		labelUser.setText(enrolledUser.getFullName());
		imageView.setImage(new Image(new ByteArrayInputStream(enrolledUser.getImageBytes())));
		tableView.getItems().setAll(userData.getData());
		columnType.setCellValueFactory(e -> new SimpleStringProperty(I18n.get(e.getValue().getType())));
		columnItem.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getItem()));
		columnValue.setCellValueFactory(e -> new SimpleDoubleProperty(e.getValue().getValue()));

		columnIcon.setCellValueFactory(
				e -> new SimpleObjectProperty<>(new ImageView(AppInfo.IMG_DIR + e.getValue().getIconFile() + ".png")));
	}

}
