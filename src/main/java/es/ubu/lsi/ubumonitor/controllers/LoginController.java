
package es.ubu.lsi.ubumonitor.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Collator;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.controlsfx.control.textfield.TextFields;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.AppInfo;
import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.controllers.load.Constants;
import es.ubu.lsi.ubumonitor.controllers.load.Login;
import es.ubu.lsi.ubumonitor.controllers.load.PopulateCourse;
import es.ubu.lsi.ubumonitor.controllers.load.PopulateCourseCategories;
import es.ubu.lsi.ubumonitor.controllers.load.PopulateMoodleUser;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.MoodleUser;
import es.ubu.lsi.ubumonitor.persistence.Serialization;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.Languages;
import es.ubu.lsi.ubumonitor.util.Parsers;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.webservice.api.core.course.CoreCourseGetEnrolledCoursesByTimelineClassification.Classification;
import es.ubu.lsi.ubumonitor.webservice.api.core.webservice.CoreWebserviceGetSiteInfo;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.ButtonType;
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
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

/**
 * Clase controlador de la ventana de Login
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class LoginController implements Initializable {

	private static final String BETA_TESTER = "betaTester";

	private static final String APPLICATION_PATH = "applicationPath";

	private static final String ASK_AGAIN = "askAgain";

	private static final String HOSTS = "hosts";

	private static final String USERNAMES = "usernames";

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

	private Controller controller = Controller.getInstance();

	@FXML
	private AnchorPane anchorPane;
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

	@FXML
	private ImageView imageViewconfigurationHelper;

	/**
	 * Crea el selector de idioma.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		txtHost.textProperty()
				.addListener((observable, oldValue,
						newValue) -> insecureProtocol.setVisible(Optional.ofNullable(newValue)
								.orElse("")
								.startsWith("http://")));

		initializeProperties();

		Platform.runLater(() -> {
			if (!Optional.ofNullable(txtUsername.getText())
					.orElse("")
					.isEmpty()) {

				txtPassword.requestFocus(); // si hay texto cargado del usuario cambiamos el focus al texto de
											// password
			}
		});
		Tooltip.install(insecureProtocol, new Tooltip(I18n.get("tooltip.insecureprotocol")));
		initLanguagesList();
		initLauncherConfiguration();
		DataBase dataBase = new DataBase();
		controller.setDataBase(dataBase);
		controller.setDefautlDataBase(dataBase);
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
		languages.sort(Comparator.comparing(Languages::getDisplayLanguage, Collator.getInstance()));
		languageSelector.setItems(languages);
		languageSelector.setValue(controller.getSelectedLanguage());

		// Carga la interfaz con el idioma seleccionado
		languageSelector.getSelectionModel()
				.selectedItemProperty()
				.addListener((ov, value, newValue) -> {

					controller.setSelectedLanguage(newValue);
					LOGGER.info("Idioma de la aplicación: {}", newValue);
					LOGGER.info("Idioma cargado del resource bundle: {}", I18n.getResourceBundle()
							.getLocale());
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

		txtHost.setText(ConfigHelper.getProperty("host", ""));

		txtUsername.setText(ConfigHelper.getProperty("username", ""));
		txtPassword.setText(System.getProperty(AppInfo.APPLICATION_NAME + ".password", ""));
		chkSaveUsername.setSelected(ConfigHelper.getProperty("saveUsername", false));
		chkSaveHost.setSelected(ConfigHelper.getProperty("saveHost", false));
		chkOfflineMode.setSelected(ConfigHelper.getProperty("offlineMode", false));

		TextFields.bindAutoCompletion(txtUsername, ConfigHelper.getArray(USERNAMES)
				.toList())
				.setDelay(0);
		TextFields.bindAutoCompletion(txtHost, ConfigHelper.getArray(HOSTS)
				.toList())
				.setDelay(0);

	}

	/**
	 * Guarda las opciones del usuario en el fichero properties
	 */
	private void saveProperties() {

		String username = chkSaveUsername.isSelected() ? txtUsername.getText() : "";
		ConfigHelper.setProperty("username", username);
		ConfigHelper.setProperty("saveUsername", chkSaveUsername.isSelected());

		String host = chkSaveHost.isSelected() ? txtHost.getText() : "";
		ConfigHelper.setProperty("host", host);
		ConfigHelper.setProperty("saveHost", chkSaveHost.isSelected());

		ConfigHelper.setProperty("offlineMode", chkOfflineMode.isSelected());
		if (chkSaveUsername.isSelected()) {
			List<Object> array = ConfigHelper.getArray(USERNAMES)
					.toList();
			if (!array.contains(username)) {

				ConfigHelper.appendArray(USERNAMES, username);
			}
		}
		if (chkSaveHost.isSelected()) {

			List<Object> array = ConfigHelper.getArray(HOSTS)
					.toList();
			if (!array.contains(host)) {

				ConfigHelper.appendArray(HOSTS, host);
			}
		}

	}

	/**
	 * Hace el login de usuario al pulsar el botón Entrar. Si el usuario es
	 * incorrecto, muestra un mensaje de error.
	 * 
	 * @param event El ActionEvent.
	 */
	public void login() {
		if (txtHost.getText()
				.trim()
				.isEmpty()
				|| txtPassword.getText()
						.trim()
						.isEmpty()
				|| txtUsername.getText()
						.trim()
						.isEmpty()) {
			lblStatus.setText(I18n.get("error.fields"));
			return;
		}
		controller.getStage()
				.getScene()
				.setCursor(Cursor.WAIT);

		if (chkOfflineMode.isSelected()) {
			try {
				

				if (offlineMode()) {
					UtilMethods.changeScene(getClass().getResource("/view/WelcomeOffline.fxml"), controller.getStage(),
							new WelcomeOfflineController());
				}else {
					lblStatus.setText(I18n.get("label.offlinePasswordIncorrect"));
					controller.getStage()
					.getScene()
					.setCursor(Cursor.DEFAULT);
				}

			} catch (MalformedURLException | RuntimeException e) {
				LOGGER.error("Error en el login offline", e);
				lblStatus.setText(Parsers.parseHtmlToString(e.getMessage()));
			}

		} else {
			onlineMode();
		}

	}

	private boolean offlineMode() throws MalformedURLException {

		controller.setURLHost(new URL(txtHost.getText()));

		controller.setUsername(txtUsername.getText());
		controller.setPassword(txtPassword.getText());
		onSuccessLogin();
		try {

			Serialization.decrypt(controller.getPassword(), controller.getHostUserDir().resolve("dummyObject").toString());
		}catch (Exception e){
			return false;
		}
		

		File hostUserDir = controller.getHostUserDir().toFile();
		File hostUserModelversionDir = controller.getHostUserModelversionDir().toFile();
		if (hostUserDir.isDirectory()
				&& !hostUserModelversionDir.isDirectory() || hostUserModelversionDir.listFiles().length == 0) {

			ButtonType option = UtilMethods
					.confirmationWindow(I18n.get("text.modelversionchanged") + "\n" + I18n.get("text.wantonlinemode"));
			if (option == ButtonType.OK) {
				controller.getHostUserModelversionDir()
						.toFile()
						.mkdirs();
				chkOfflineMode.setSelected(false);

				login();

			}
			return false;
		}


		return true;
	}

	private void onlineMode() {
		Service<Void> service = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return getUserDataWorker();
			}
		};

		lblStatus.setText(null);
		service.setOnSucceeded(s -> {
			onSuccessLogin();
			Serialization.encrypt(controller.getPassword(), controller.getHostUserDir().resolve("dummyObject").toString(), "Dummy object to check if password is correct");
			UtilMethods.changeScene(getClass().getResource("/view/Welcome.fxml"), controller.getStage(),
					new WelcomeController());
		});

		service.setOnFailed(e -> {
			LOGGER.info("Failed task", e.getSource()
					.getException());
			controller.getStage()
					.getScene()
					.setCursor(Cursor.DEFAULT);

			lblStatus.setText(e.getSource()
					.getException()
					.getMessage());
		});
		anchorPane.disableProperty()
				.bind(service.runningProperty());
		service.start();
	}

	private void onSuccessLogin() {
		LOGGER.info("Login success");
		saveProperties();
		controller.getStage()
				.getScene()
				.setCursor(Cursor.DEFAULT);
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
					txtHost.setText(controller.getUrlHost()
							.toString());
					Login login = controller.getLogin();
					controller.setUsername(login.getUsername());
					controller.setPassword(login.getPassword());
					txtUsername.setText(login.getUsername());
					txtPassword.setText(login.getPassword());

				} catch (IOException e) {
					LOGGER.error("No se ha podido conectar con el host.", e);
					throw new IllegalArgumentException(I18n.get("error.host"));
				} catch (JSONException e) {
					LOGGER.error("Usuario y/o contraseña incorrectos", e);
					throw new IllegalArgumentException(I18n.get("error.login"));

				}

				try {
					LOGGER.info("Recogiendo info del usuario");
					String validUsername = UtilMethods
							.getJSONObjectResponse(controller.getWebService(), new CoreWebserviceGetSiteInfo())
							.getString(Constants.USERNAME);
					PopulateMoodleUser populateMoodleUser = new PopulateMoodleUser(controller.getWebService());
					MoodleUser moodleUser = populateMoodleUser.populateMoodleUser(validUsername, controller.getUrlHost()
							.toString());
					PopulateCourse populateCourse = new PopulateCourse(controller.getDataBase(),
							controller.getWebService());

					moodleUser.setCourses(populateCourse.createCourses(moodleUser.getId()));
					moodleUser.setRecentCourses(populateCourse.recentCourses());
					moodleUser.setPastCourses(populateCourse.coursesByTimelineClassification(Classification.PAST));
					moodleUser.setInProgressCourses(
							populateCourse.coursesByTimelineClassification(Classification.IN_PROGRESS));
					moodleUser.setFutureCourses(populateCourse.coursesByTimelineClassification(Classification.FUTURE));

					populateCourse.createCourseAdministrationOptions(moodleUser.getCourses()
							.stream()
							.map(Course::getId)
							.collect(Collectors.toList()));

					PopulateCourseCategories populateCourseCategories = new PopulateCourseCategories(
							controller.getDataBase(), controller.getWebService());
					populateCourseCategories.populateCourseCategories(moodleUser.getCourses()
							.stream()
							.map(c -> c.getCourseCategory()
									.getId())
							.collect(Collectors.toList()));
					controller.setUser(moodleUser);
					

				} catch (Exception e) {
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

	/**
	 * Abre en el navegador el repositorio del proyecto.
	 * 
	 */
	@FXML
	private void aboutApp() {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AboutApp.fxml"), I18n.getResourceBundle());

		UtilMethods.createDialog(loader, controller.getStage());

	}

	private void initLauncherConfiguration() {
		imageViewconfigurationHelper.setVisible(ConfigHelper.has(ASK_AGAIN) && ConfigHelper.has(APPLICATION_PATH));
		Tooltip.install(imageViewconfigurationHelper, new Tooltip(I18n.get("label.launcherconfiguration")));
	}

	@FXML
	private void openLauncherConfiguration() {

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/LauncherConfiguration.fxml"),
				I18n.getResourceBundle());
		try {
			fxmlLoader.load();
		} catch (IOException e) {
			LOGGER.error("cannot load /view/DownloadConfirmation.fxml", e);
			UtilMethods.errorWindow("Error loading LauncherConfiguration.fxml", e);
			return;
		}
		LauncherConfigurationController launcherConfigurationController = fxmlLoader.getController();

		File newPath = launcherConfigurationController.init(ConfigHelper.getProperty(ASK_AGAIN, true),
				ConfigHelper.getProperty(BETA_TESTER, false), ConfigHelper.getProperty(APPLICATION_PATH));

		ConfigHelper.setProperty(APPLICATION_PATH, newPath.getName());
		ConfigHelper.setProperty(ASK_AGAIN, launcherConfigurationController.isAskAgain());
		ConfigHelper.setProperty(BETA_TESTER, launcherConfigurationController.isBetaTester());

	}

}
