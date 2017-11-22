
package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.*;
import webservice.*;

/**
 * Clase controlador de la ventana de Login
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class LoginController {

	static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@FXML
	private Label lblStatus;
	@FXML
	private TextField txtUsername;
	@FXML
	private PasswordField txtPassword;
	@FXML
	private TextField txtHost;
	@FXML
	private Button btnLogin;
	@FXML
	private ProgressBar progressBar;

	/**
	 * Hace el login de usuario al pulsar el botón Entrar. Si el usuario es
	 * incorrecto, muestra un mensaje de error.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void login(ActionEvent event) throws Exception {
		// Almacenamos los parámetros introducidos por el usuario:
		UBUGrades.init.getScene().setCursor(Cursor.WAIT);
		UBUGrades.host = txtHost.getText();
		UBUGrades.session = new Session(txtUsername.getText(), txtPassword.getText());

		Boolean correcto = true;
		progressBar.visibleProperty().set(false);

		try { // Establecemos el token
			logger.info("Obteniendo el token.");
			UBUGrades.session.setToken();
		} catch (Exception e) {
			correcto = false;
			logger.error("No se ha podido establecer el token d usuario. {}", e);
		}
		// Si el login es correcto
		if (correcto) {
			logger.info(" Login Correcto");

			progressBar.setProgress(0.0);
			Task<Object> task = createWorker();
			progressBar.progressProperty().unbind();
			progressBar.progressProperty().bind(task.progressProperty());
			progressBar.visibleProperty().set(true);
			task.messageProperty().addListener(new ChangeListener<String>() {
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					if (newValue.equals("end")) {
						// Cargamos la siguiente ventana
						try {
							// Accedemos a la siguiente ventana
							FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Welcome.fxml"));

							UBUGrades.stage = new Stage();

							Parent root = loader.load();
							Scene scene = new Scene(root);
							UBUGrades.stage.setScene(scene);
							UBUGrades.stage.getIcons().add(new Image("/img/logo_min.png"));
							UBUGrades.stage.setTitle("UBUGrades");
							UBUGrades.stage.setResizable(false);
							UBUGrades.init.close();
							UBUGrades.stage.show();
							lblStatus.setText("");
						} catch (Exception e) {
							e.printStackTrace();
							throw new RuntimeException("Loading Welcome.fxml");
						}
					} else {
						logger.info(newValue);
					}
				}
			});
			Thread th = new Thread(task);
			th.start();
		} else {
			lblStatus.setText("Usuario y/o contraseña incorrectos.");
			logger.info("Login Incorrecto");
			//txtUsername.setText("");
			txtPassword.setText("");
		}
	}

	/**
	 * Realiza las tareas mientras carga la barra de progreso
	 * 
	 * @return tarea
	 */
	private Task<Object> createWorker() {
		return new Task<Object>() {
			@Override
			protected Object call() throws Exception {
				MoodleUserWS.setMoodleUser(UBUGrades.session.getToken(), UBUGrades.session.getEmail(),
						UBUGrades.user = new MoodleUser());
				updateProgress(1, 3);
				MoodleUserWS.setCourses(UBUGrades.session.getToken(), UBUGrades.user);
				updateProgress(2, 3);
				updateProgress(3, 3);
				Thread.sleep(50);
				updateMessage("end");
				return true;
			}
		};
	}

	/**
	 * Borra los parámetros introducidos en los campos
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void clear(ActionEvent event) throws Exception {
		txtUsername.setText("");
		txtPassword.setText("");
		txtHost.setText("");
	}
}
