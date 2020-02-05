package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import util.UtilMethods;

public class AboutAppController implements Initializable {

	@FXML
	private Label labelJre;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		labelJre.setText("JRE: " + AppInfo.JAVA_VENDOR + " " + AppInfo.JAVA_VERSION);

		Platform.runLater(() -> labelJre.requestFocus());
	}

	public void openLicense() {
		UtilMethods.openURL("https://github.com/yjx0003/UBUMonitor/blob/master/LICENSE");
	}

	public void openGithub() {
		UtilMethods.openURL(AppInfo.GITHUB);
	}

	public void openAuthor1() {
		UtilMethods.mailTo("ypji@ubu.es");
	}

	public void openAuthor2() {
		UtilMethods.mailTo("rmartico@ubu.es");
	}

	public void openAuthor3() {
		UtilMethods.mailTo("cpardo@ubu.es");
	}
}
