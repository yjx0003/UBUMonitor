
package es.ubu.lsi.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.controlsfx.control.textfield.TextFields;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.controllers.configuration.Config;
import es.ubu.lsi.controllers.ubugrades.CreatorUBUGradesController;
import es.ubu.lsi.model.Course;
import es.ubu.lsi.model.MoodleUser;
import es.ubu.lsi.util.UtilMethods;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

/**
 * Clase controlador de la ventana de Login
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class LoginController implements Initializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

	private Controller controller = Controller.getInstance();

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

	@FXML
	private CheckBox chkOfflineMode;

	@FXML
	private ImageView insecureProtocol;

	/**
	 * Crea el selector de idioma.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		txtHost.textProperty().addListener((observable, oldValue, newValue) -> insecureProtocol
				.setVisible(Optional.ofNullable(newValue).orElse("").startsWith("http://")));

		initializeProperties();

		Platform.runLater(() -> {
			if (!Optional.ofNullable(txtUsername.getText()).orElse("").isEmpty()) {

				txtPassword.requestFocus(); // si hay texto cargado del usuario cambiamos el focus al texto de
											// password
			}
		});

		Tooltip.install(insecureProtocol, new Tooltip(I18n.get("tooltip.insecureprotocol")));
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
						Image countryImage = new Image(AppInfo.IMG_FLAGS + item.getFlag() + ".png");
						ImageView imageView = new ImageView(countryImage);
						setGraphic(imageView);
					} catch (Exception e) {
						LOGGER.warn("No disponible la foto de bandera para: {}", item.getFlag());
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
			LOGGER.info("[Bienvenido a " + AppInfo.APPLICATION_NAME_WITH_VERSION + "]");
			UtilMethods.changeScene(getClass().getResource("/view/Login.fxml"), controller.getStage());
		});

	}

	/**
	 * Inicializa el fichero properties con el nombre de usuario y host
	 * 
	 * @throws IOException
	 */
	private void initializeProperties() {

		txtHost.setText(Config.getProperty("host", ""));

		txtUsername.setText(Config.getProperty("username", ""));
		txtPassword.setText(System.getProperty(AppInfo.APPLICATION_NAME + ".password", ""));
		chkSaveUsername.setSelected(Boolean.parseBoolean(Config.getProperty("saveUsername")));
		chkSaveHost.setSelected(Boolean.parseBoolean(Config.getProperty("saveHost")));
		chkOfflineMode.setSelected(Boolean.parseBoolean(Config.getProperty("offlineMode")));
		String[] hosts = Config.getProperty("hosts", Config.getProperty("host", "")).split("\t");
		String[] usernames = Config.getProperty("usernames", Config.getProperty("username", "")).split("\t");
		TextFields.bindAutoCompletion(txtUsername, usernames);
		TextFields.bindAutoCompletion(txtHost, hosts);

	}

	/**
	 * Guarda las opciones del usuario en el fichero properties
	 */
	private void saveProperties() {

		String username = chkSaveUsername.isSelected() ? txtUsername.getText() : "";
		Config.setProperty("username", username);
		Config.setProperty("saveUsername", Boolean.toString(chkSaveUsername.isSelected()));

		String host = chkSaveHost.isSelected() ? txtHost.getText() : "";
		Config.setProperty("host", host);
		Config.setProperty("saveHost", Boolean.toString(chkSaveHost.isSelected()));

		Config.setProperty("offlineMode", Boolean.toString(chkOfflineMode.isSelected()));
		if (chkSaveUsername.isSelected()) {
			String[] usernames = Config.getProperty("usernames", "").split("\t");
			if (Arrays.stream(usernames).noneMatch(username::equals)) {
				Config.setProperty("usernames", username + "\t" + String.join("\t", usernames));
			}
		}
		if (chkSaveHost.isSelected()) {

			String[] hosts = Config.getProperty("hosts", "").split("\t");
			if (Arrays.stream(hosts).noneMatch(host::equals)) {
				Config.setProperty("hosts", host + "\t" + String.join("\t", hosts));
			}
		}

	}

	/**
	 * Hace el login de usuario al pulsar el botón Entrar. Si el usuario es
	 * incorrecto, muestra un mensaje de error.
	 * 
	 * @param event El ActionEvent.
	 */
	public void login(ActionEvent event) {
		if (txtHost.getText().isEmpty() || txtPassword.getText().isEmpty() || txtUsername.getText().isEmpty()) {
			lblStatus.setText(I18n.get("error.fields"));
		} else {
			controller.getStage().getScene().setCursor(Cursor.WAIT);
			if (chkOfflineMode.isSelected()) {
				try {
					offlineMode();
					UtilMethods.changeScene(getClass().getResource("/view/WelcomeOffline.fxml"), controller.getStage(),
							new WelcomeOfflineController());
				} catch (MalformedURLException | RuntimeException e) {
					lblStatus.setText(e.getMessage());
				}

			} else {
				onlineMode();
			}

		}
	}

	private List<Course> findCacheCourses() {

		File dir = controller.getDirectoryCache().toFile();
		if (!dir.exists() || !dir.isDirectory()) {
			return Collections.emptyList();
		}

		Pattern pattern = Pattern.compile("(.+)-(\\d+)$");
		List<Course> courses = new ArrayList<>();
		for (File cache : dir.listFiles()) {
			Matcher matcher = pattern.matcher(cache.getName());
			if (matcher.find()) {
				Course course = new Course(Integer.valueOf(matcher.group(2)));
				course.setFullName(matcher.group(1));
				courses.add(course);
			}
		}
		return courses;
	}

	private void offlineMode() throws MalformedURLException {

		controller.setURLHost(new URL(txtHost.getText()));

		controller.setUsername(txtUsername.getText());
		controller.setPassword(txtPassword.getText());
		MoodleUser user = new MoodleUser();
		user.setFullName(txtUsername.getText()); // because we have not a fullname of the user in offline mode
		controller.setUser(user);
		onSuccessLogin();

		List<Course> courses = findCacheCourses();
		if (courses.isEmpty()) {
			throw new IllegalArgumentException(I18n.get("error.nocacheavaible"));
		}
		user.getCourses().addAll(courses);

	}

	private void onlineMode() {
		Task<Void> loginTask = getUserDataWorker();

		loginTask.setOnSucceeded(s -> {
			onSuccessLogin();
			if (!controller.getDirectoryCache().toFile().exists()) {
				controller.getDirectoryCache().toFile().mkdirs();
			}
			UtilMethods.changeScene(getClass().getResource("/view/Welcome.fxml"), controller.getStage(),
					new WelcomeController());
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

	private void onSuccessLogin() {
		saveProperties();
		controller.getStage().getScene().setCursor(Cursor.DEFAULT);
		controller.setLoggedIn(LocalDateTime.now());
		controller.setDirectory();

		controller.setOfflineMode(chkOfflineMode.isSelected());
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
					controller.setDirectory();
			
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
	 * @param event El ActionEvent.
	 */
	public void clear(ActionEvent event) {
		txtUsername.setText("");
		txtPassword.setText("");
		txtHost.setText("");
	}

}
