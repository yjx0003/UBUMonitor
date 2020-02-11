package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import util.UtilMethods;

public class AboutAppController implements Initializable {

	private static final StringBuilder USER_INFO = new StringBuilder();
	static {
		USER_INFO.append("Installation directory for Java Runtime Environment (JRE): " + AppInfo.JAVA_HOME + "\n");
		USER_INFO.append("JRE vendor name: " + AppInfo.JAVA_VENDOR + "\n");
		USER_INFO.append("JRE version number: " + AppInfo.JAVA_VERSION + "\n");
		USER_INFO.append("Operating system architecture: " + AppInfo.OS_ARCH + "\n");
		USER_INFO.append("Operating system name: " + AppInfo.OS_NAME + "\n");
		USER_INFO.append("Operating system version: " + AppInfo.OS_VERSION + "\n");
		USER_INFO.append("User working directory: " + AppInfo.USER_DIR + "\n");
		USER_INFO.append("User home directory: " + AppInfo.USER_HOME + "\n");
		USER_INFO.append("User account name: " + AppInfo.USER_NAME);
	}

	@FXML
	private Label labelJre;

	@FXML
	private TextArea textArea;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		textArea.setText(USER_INFO.toString());
		Platform.runLater(() -> textArea.requestFocus());
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
