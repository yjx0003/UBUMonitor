
package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.ubugrades.CreatorUBUGradesController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.MoodleUser;
import model.UBUGrades;
import webservice.WebService;

/**
 * Clase controlador de la ventana de Login
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class LoginController implements Initializable {

	static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	private UBUGrades ubuGrades = UBUGrades.getInstance();

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

	// Host por defecto
	private static final String HOST = "https://ubuvirtual.ubu.es/";

	// Lista de idiomas disponibles
	private final List<String> locale = Arrays.asList("es_es", "en_en");
	private final ObservableList<String> languages = FXCollections.observableArrayList("Español", "English");

	/**
	 * Crea el selector de idioma.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		txtHost.setText(HOST);
		languageSelector.setItems(languages);
		languageSelector.getSelectionModel()
				.select(locale.indexOf(ubuGrades.getResourceBundle().getLocale().toString().toLowerCase()));
		// Carga la interfaz con el idioma seleccionado
		languageSelector.getSelectionModel().selectedIndexProperty().addListener((ov, value, newValue) -> {
			ubuGrades.setResourceBundle(
					ResourceBundle.getBundle("messages/Messages", new Locale(locale.get(newValue.intValue()))));
			logger.info("Idioma cargado: {}", ubuGrades.getResourceBundle().getLocale().toString());
			logger.info("[Bienvenido a UBUGrades]");
			changeScene(getClass().getResource("/view/Login.fxml"));
		});
	}

	/**
	 * Hace el login de usuario al pulsar el botón Entrar. Si el usuario es
	 * incorrecto, muestra un mensaje de error.
	 * 
	 * @param event
	 *            El ActionEvent.
	 */
	public void login(ActionEvent event) {
		if (txtHost.getText().isEmpty() || txtPassword.getText().isEmpty() || txtUsername.getText().isEmpty()) {
			lblStatus.setText(ubuGrades.getResourceBundle().getString("error.fields"));
		} else {
			// Si el login es correcto
			if (checkLogin()) {
				logger.info("Login Correcto");
				lblStatus.setVisible(false);

				Task<Object> loginTask = getUserDataWorker();
				progressBar.visibleProperty().set(true);
				ubuGrades.getStage().getScene().setCursor(Cursor.WAIT);
				loginTask.setOnSucceeded(s->changeScene(getClass().getResource("/view/Welcome.fxml")));
				Thread th = new Thread(loginTask, "login");
				
				th.start();
			}
		}
	}

	/**
	 * Comprueba si los datos de login introducidos son correctos.
	 * 
	 * @return True si el login es correcto.Falso si no lo es.
	 */
	private boolean checkLogin() {
		// Almacenamos los parámetros introducidos por el usuario:

		progressBar.visibleProperty().set(false);

		try { // Establecemos el token
			logger.info("Obteniendo el token.");
			ubuGrades.getStage().getScene().setCursor(Cursor.WAIT);

			WebService.initialize(txtHost.getText(), txtUsername.getText(), txtPassword.getText());

			ubuGrades.setHost(txtHost.getText());

			ubuGrades.setUsername(txtUsername.getText());
			ubuGrades.setPassword(txtPassword.getText());

			return true;

		} catch (IOException e) {
			logger.error("No se ha podido conectar con el host.", e);
			lblStatus.setText(ubuGrades.getResourceBundle().getString("error.host"));
		} catch (JSONException e) {

			logger.error("Usuario y/o contraseña incorrectos", e);
			lblStatus.setText(ubuGrades.getResourceBundle().getString("error.login"));
			txtPassword.setText("");
		} finally {
			ubuGrades.getStage().getScene().setCursor(Cursor.DEFAULT);
		}
		return false;

	}

	/**
	 * Permite cambiar la ventana actual.
	 * 
	 * @param sceneFXML
	 *            La ventanan a la que se quiere cambiar.
	 *
	 * @throws IOException
	 */
	private void changeScene(URL sceneFXML) {
		try {
			// Accedemos a la siguiente ventana
			FXMLLoader loader = new FXMLLoader(sceneFXML, ubuGrades.getResourceBundle());
			ubuGrades.getStage().close();
			Stage stage = new Stage();
			Parent root = loader.load();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.getIcons().add(new Image("/img/logo_min.png"));
			stage.setTitle("UBUGrades");
			stage.setResizable(false);
			stage.show();
			ubuGrades.setStage(stage);
			ubuGrades.getStage().getScene().setCursor(Cursor.DEFAULT);
		} catch (IOException e) {
			logger.info("No se ha podido cargar la ventana de bienvenida: {}", e);
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
					MoodleUser moodleUser = CreatorUBUGradesController.createMoodleUser(ubuGrades.getUsername());
					ubuGrades.setUser(moodleUser);
			
				} catch (Exception e) {
					logger.error("Error al obtener los datos del usuario.", e);
					updateMessage("error");
					progressBar.setVisible(false);
					lblStatus.setVisible(true);
					lblStatus.setText("Error al obtener los datos del usuario.");
				}
				return true;
			}
		};
	}

	/**
	 * Borra los parámetros introducidos en los campos
	 * 
	 * @param event
	 *            El ActionEvent.
	 */
	public void clear(ActionEvent event) {
		txtUsername.setText("");
		txtPassword.setText("");
		txtHost.setText("");
	}
}
