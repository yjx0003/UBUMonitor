
package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.Properties;
import java.util.ResourceBundle;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.ubugrades.CreatorUBUGradesController;
import javafx.application.Platform;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.MoodleUser;

/**
 * Clase controlador de la ventana de Login
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class LoginController implements Initializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
	private static final String PROPERTIES_PATH = "config.properties";
	private Controller controller = Controller.getInstance();
	private Properties properties;
	@FXML
	private Label lblStatus;
	@FXML
	private TextField txtUsername;
	@FXML
	private PasswordField txtPassword;
	@FXML
	private TextField txtHost;

	@FXML
	private ComboBox<Languages> languageSelector;

	@FXML
	private CheckBox chkSaveUsername;

	@FXML
	private CheckBox chkSaveHost;

	/**
	 * Crea el selector de idioma.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			initializeProperties();
		} catch (IOException e) {
			LOGGER.error("No se ha podido inicializar el fichero properties");
		}

		Platform.runLater(() -> {
			if (txtUsername != null && !txtUsername.getText().isEmpty()) {
				txtPassword.requestFocus(); // si hay texto cargado del usuario cambiamos el focus al texto de
											// password
			}
		});

		initLanguagesList();
	}

	/**
	 * Initialize languages list choice box from Languages enum class.
	 */
	private void initLanguagesList() {

		Callback<ListView<Languages>, ListCell<Languages>> listCell = callback -> new ListCell<Languages>() {
			@Override
			protected void updateItem(Languages item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setGraphic(null);
					setText(null);
				} else {
					setText(item.getDisplayLanguage());
					try {
						Image countryImage = new Image(AppInfo.IMG_FLAGS + item.getCountry() + ".png");
						ImageView imageView = new ImageView(countryImage);
						imageView.setFitHeight(16);
						imageView.setFitWidth(24);
						setGraphic(imageView);
					} catch (Exception e) {
						LOGGER.warn("No disponible la foto de bandera para: {}", item);
						setGraphic(null);
					}

				}
			}
		};
		languageSelector.setCellFactory(listCell);
		languageSelector.setButtonCell(listCell.call(null));

		ObservableList<Languages> languages = FXCollections.observableArrayList(Languages.values());
		languages.sort(Comparator.comparing(Languages::getDisplayLanguage, String.CASE_INSENSITIVE_ORDER));
		languageSelector.setItems(languages);
		languageSelector.setValue(controller.getSelectedLanguage());

		// Carga la interfaz con el idioma seleccionado
		languageSelector.getSelectionModel().selectedItemProperty().addListener((ov, value, newValue) -> {

			controller.setSelectedLanguage(newValue);
			LOGGER.info("Idioma de la aplicación: {}", newValue);
			LOGGER.info("Idioma cargado del resource bundle: {}", I18n.getResourceBundle().getLocale());
			LOGGER.info("[Bienvenido a " + AppInfo.APPLICATION_NAME + "]");
			changeScene(getClass().getResource("/view/Login.fxml"));
		});

	}

	/**
	 * Inicializa el fichero properties con el nombre de usuario y host
	 * 
	 * @throws IOException
	 */
	private void initializeProperties() throws IOException {

		properties = new Properties();

		File file = new File(PROPERTIES_PATH);
		if (!file.isFile() && !file.createNewFile()) {
			LOGGER.error("No se ha podido crear el fichero properties: " + PROPERTIES_PATH);
		} else { // si existe el fichero properties inicializamos los valores
			try (InputStream in = new FileInputStream(file)) {

				properties.load(in);
				txtHost.setText(properties.getProperty("host", ""));
				txtUsername.setText(properties.getProperty("username", ""));
				chkSaveUsername.setSelected(Boolean.parseBoolean(properties.getProperty("saveUsername")));
				chkSaveHost.setSelected(Boolean.parseBoolean(properties.getProperty("saveHost")));

			} catch (IOException e) {
				LOGGER.error("No se ha podido cargar " + PROPERTIES_PATH);
			}
		}

	}

	/**
	 * Guarda las opciones del usuario en el fichero properties
	 */
	private void saveProperties() {

		String username = chkSaveUsername.isSelected() ? txtUsername.getText() : "";
		properties.setProperty("username", username);
		properties.setProperty("saveUsername", Boolean.toString(chkSaveUsername.isSelected()));

		String host = chkSaveHost.isSelected() ? txtHost.getText() : "";
		properties.setProperty("host", host);
		properties.setProperty("saveHost", Boolean.toString(chkSaveHost.isSelected()));

		File file = new File(PROPERTIES_PATH);
		try (FileOutputStream out = new FileOutputStream(file)) {
			properties.store(out, null);

		} catch (IOException e) {
			LOGGER.error("No se ha podido guardar el fichero {}", file.getAbsolutePath());
		}
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
			lblStatus.setText(I18n.get("error.fields"));
		} else {
			controller.getStage().getScene().setCursor(Cursor.WAIT);

			Task<Void> loginTask = getUserDataWorker();

			loginTask.setOnSucceeded(s -> {
				saveProperties();
				controller.getStage().getScene().setCursor(Cursor.DEFAULT);
				changeScene(getClass().getResource("/view/Welcome.fxml"));
			});

			loginTask.setOnFailed(e -> {
				LOGGER.error("Error al recuperar los datos: ", e.getSource().getException());
				controller.getStage().getScene().setCursor(Cursor.DEFAULT);
				txtPassword.clear();
				lblStatus.setText(e.getSource().getException().getMessage());
			});
			Thread th = new Thread(loginTask, "login");

			th.start();
		}
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
			FXMLLoader loader = new FXMLLoader(sceneFXML, I18n.getResourceBundle());
			controller.getStage().close();
			Stage stage = new Stage();
			Parent root = loader.load();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.getIcons().add(new Image("/img/logo_min.png"));
			stage.setTitle(AppInfo.APPLICATION_NAME);
			stage.setResizable(false);
			stage.show();
			controller.setStage(stage);
		} catch (IOException e) {
			LOGGER.info("No se ha podido cargar la ventana de bienvenida: {}", e);
		}
	}

	/**
	 * Realiza las tareas mientras carga la barra de progreso
	 * 
	 * @return tarea
	 */
	private Task<Void> getUserDataWorker() {
		return new Task<Void>() {
			@Override
			protected Void call() {
				try {

					controller.tryLogin(txtHost.getText(), txtUsername.getText(), txtPassword.getText());

				} catch (MalformedURLException e) {
					LOGGER.error("URL mal formada. ¿Has añadido protocolo http(s)?", e);
					throw new IllegalArgumentException(I18n.get("error.malformedurl"));
				} catch (IOException e) {
					LOGGER.error("No se ha podido conectar con el host.", e);
					throw new IllegalArgumentException(I18n.get("error.host"));
				} catch (JSONException e) {
					LOGGER.error("Usuario y/o contraseña incorrectos", e);
					throw new IllegalArgumentException(I18n.get("error.login"));

				}

				try {
					MoodleUser moodleUser = CreatorUBUGradesController.createMoodleUser(controller.getUsername());
					controller.setUser(moodleUser);
				} catch (IOException e) {
					LOGGER.error("Error al recuperar los datos del usuario.", e);
					throw new IllegalStateException(I18n.get("error.user"));
				}

				return null;
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
