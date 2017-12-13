
package controllers;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.json.JSONException;
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
public class LoginController implements Initializable {

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
	@FXML
	private ChoiceBox<String> languageSelector;
	private final List<String> locale = Arrays.asList("es_es", "en_en");
	
	/**
	 * Crea el selector de idioma.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		languageSelector.setItems(FXCollections.observableArrayList("Español", "English"));
		languageSelector.getSelectionModel().select(locale.indexOf(UBUGrades.resourceBundle.getLocale().toString().toLowerCase()));
		// Carga la interfaz con el idioma seleccionado
		languageSelector.getSelectionModel().selectedIndexProperty().addListener((ov, value, newValue) -> {			
			try {
				logger.info("idioma seleccionado " + locale.get(newValue.intValue()).toString());
				UBUGrades.resourceBundle = ResourceBundle.getBundle("Messages", new Locale(locale.get(newValue.intValue())));
				logger.info("idioma cargado " + UBUGrades.resourceBundle.getLocale().toString());
				logger.info("[Bienvenido a UBUGrades]");
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"), UBUGrades.resourceBundle);
				UBUGrades.stage.close();
				UBUGrades.stage = new Stage();
				Parent root = loader.load();
				Scene scene = new Scene(root);
				UBUGrades.stage.setScene(scene);
				UBUGrades.stage.getIcons().add(new Image("/img/logo_min.png"));
				UBUGrades.stage.setTitle("UBUGrades");
				UBUGrades.stage.setResizable(false);
				UBUGrades.stage.show();
			} catch (Exception e) {
				logger.error("Error al cambiar el idioma: {}", e);
			}
		});
	}
	
	/**
	 * Hace el login de usuario al pulsar el botón Entrar. Si el usuario es
	 * incorrecto, muestra un mensaje de error.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void login(ActionEvent event) {
		if(txtHost.getText().isEmpty() || txtPassword.getText().isEmpty() || txtUsername.getText().isEmpty()) {
			lblStatus.setText(UBUGrades.resourceBundle.getString("error.fields"));
		} else {
			
			// Almacenamos los parámetros introducidos por el usuario:
			UBUGrades.host = txtHost.getText();
			UBUGrades.session = new Session(txtUsername.getText(), txtPassword.getText());
	
			Boolean correcto = true;
			progressBar.visibleProperty().set(false);
	
			try { // Establecemos el token
				logger.info("Obteniendo el token.");
				UBUGrades.stage.getScene().setCursor(Cursor.WAIT);
				UBUGrades.session.setToken();
			} catch (IOException e) {
				correcto = false;
				logger.error("No se ha podido conectar con el host.", e);
				lblStatus.setText(UBUGrades.resourceBundle.getString("error.host"));
			}catch (JSONException e) {
				correcto = false;
				logger.error("Usuario y/o contraseña incorrectos", e);
				lblStatus.setText(UBUGrades.resourceBundle.getString("error.login"));
				txtPassword.setText("");
			} finally {
				UBUGrades.stage.getScene().setCursor(Cursor.DEFAULT);
			}
			
			// Si el login es correcto
			if (correcto) {
				logger.info("Login Correcto");
				lblStatus.setVisible(false);
	
				
				Task<Object> loginTask = getUserDataWorker();
				progressBar.progressProperty().unbind();
				progressBar.progressProperty().bind(loginTask.progressProperty());
				progressBar.visibleProperty().set(true);
				loginTask.messageProperty().addListener((ChangeListener<String>)(observable, oldValue, newValue) -> {
					if (newValue.equals("end")) {
						// Cargamos la siguiente ventana
						loadWellcome();
					} else if (newValue.equals("error")) {
						progressBar.setVisible(false);
						lblStatus.setVisible(true);
						lblStatus.setText("Error al obtener los datos del usuario.");
					}
				});
				Thread th = new Thread(loginTask, "login");
				th.start();
			}
		}
	}
	
	/**
	 * Carga la ventana de seleccion del curso.
	 */
	private void loadWellcome() {
		try {
			UBUGrades.stage.getScene().setCursor(Cursor.WAIT);
			// Accedemos a la siguiente ventana
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Welcome.fxml"), UBUGrades.resourceBundle);
			UBUGrades.stage.close();
			UBUGrades.stage = new Stage();
			Parent root = loader.load();
			Scene scene = new Scene(root);
			UBUGrades.stage.setScene(scene);
			UBUGrades.stage.getIcons().add(new Image("/img/logo_min.png"));
			UBUGrades.stage.setTitle("UBUGrades");
			UBUGrades.stage.setResizable(false);
			UBUGrades.stage.show();
			lblStatus.setText("");
		} catch (IOException e) {
			logger.info("No se ha podido cargar la ventana de bienvenida: {}", e);
		} finally {
			UBUGrades.stage.getScene().setCursor(Cursor.DEFAULT);
		}
	}
	
	/**
	 * Realiza las tareas mientras carga la barra de progreso
	 * 
	 * @return tarea
	 */
	private Task<Object> getUserDataWorker() {
		return new Task<Object>() {
			@Override
			protected Object call() {
				try {
					updateProgress(0, 3);
					MoodleUserWS.setMoodleUser(UBUGrades.session.getToken(), UBUGrades.session.getEmail(),
					UBUGrades.user = new MoodleUser());
					updateProgress(1, 3);
					MoodleUserWS.setCourses(UBUGrades.session.getToken(), UBUGrades.user);
					updateProgress(2, 3);
					updateProgress(3, 3);
					Thread.sleep(50);
					updateMessage("end");
				} catch (Exception e) {
					logger.error("Error al obtener los datos del usuario.", e);
					updateMessage("error");
				}
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
	public void clear(ActionEvent event) {
		txtUsername.setText("");
		txtPassword.setText("");
		txtHost.setText("");
	}
}
