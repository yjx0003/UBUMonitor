package es.ubu.lsi.ubumonitor.controllers;

import java.io.File;
import java.io.InvalidClassException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.configuration.Config;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.persistence.Serialization;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Clase controlador de la pantalla de bienvenida en la que se muestran los
 * cursos del usuario logueado.
 * 
 * @author Claudia Martínez Herrero
 * @author Yi Peng Ji
 * @version 2.0
 * @since 1.0
 */
public class WelcomeOfflineController implements Initializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(WelcomeOfflineController.class);

	/**
	 * path con directorios de los ficheros cache
	 */
	private Path cacheFilePath;
	private Controller controller = Controller.getInstance();

	@FXML
	private AnchorPane anchorPane;
	@FXML
	private Label lblUser;
	@FXML
	private ListView<Course> listCourses;

	@FXML
	private TabPane tabPane;

	@FXML
	private Label labelLoggedIn;
	@FXML
	private Label labelHost;

	@FXML
	private Label lblNoSelect;
	@FXML
	private Button btnEntrar;
	@FXML
	private Button btnRemove;

	@FXML
	private Label lblDateUpdate;

	@FXML
	private Label conexionLabel;

	private boolean isBBDDLoaded;

	/**
	 * Función initialize. Muestra la lista de cursos del usuario introducido.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		try {
			conexionLabel.setText(I18n.get("text.online_" + !controller.isOfflineMode()));
			lblUser.setText(controller.getUser().getFullName());
			LOGGER.info("Cargando cursos...");

			anchorPane.disableProperty().bind(btnEntrar.visibleProperty().not());
			btnRemove.visibleProperty().bind(btnEntrar.visibleProperty());

			labelLoggedIn.setText(controller.getLoggedIn().format(Controller.DATE_TIME_FORMATTER));
			labelHost.setText(controller.getUrlHost().toString());

			initListViews();

			tabPane.getSelectionModel().selectedItemProperty().addListener((ov, value, newValue) -> {
				ListView<Course> listView = (ListView<Course>) value.getContent();
				listView.getSelectionModel().clearSelection();

			});
			tabPane.getSelectionModel().select(Config.getProperty("courseList", 0));

			Platform.runLater(() -> {
				ListView<Course> listView = (ListView<Course>) tabPane.getSelectionModel().getSelectedItem()
						.getContent();
				Course course = controller.getUser().getCourseById(Config.getProperty("actualCourse", -1));

				listView.getSelectionModel().select(course);
				listView.scrollTo(course);

			});

		} catch (Exception e) {
			LOGGER.error("Error al cargar los cursos", e);
		}

	}

	private void initListViews() {
		Comparator<Course> courseComparator = Comparator.comparing(Course::getFullName)
				.thenComparing(c -> c.getCourseCategory().getName());

		initListView(controller.getUser().getCourses(), listCourses, courseComparator);

	}

	private void initListView(List<Course> courseList, ListView<Course> listView, Comparator<Course> comparator) {
		ObservableList<Course> observableList = FXCollections.observableArrayList(courseList);
		if (comparator != null) {
			observableList.sort(comparator);
		}
		listView.setItems(observableList);
		listView.getSelectionModel().selectedItemProperty().addListener((ov, value, newValue) -> checkFile(newValue));

	}

	private Course getSelectedCourse() {
		@SuppressWarnings("unchecked")
		ListView<Course> listView = (ListView<Course>) tabPane.getSelectionModel().getSelectedItem().getContent();
		return listView.getSelectionModel().getSelectedItem();
	}

	private void checkFile(Course newValue) {
		if (newValue == null)
			return;
		cacheFilePath = controller.getDirectoryCache(newValue);

		LOGGER.debug("Buscando si existe {}", cacheFilePath);

		File f = cacheFilePath.toFile();
		if (f.exists() && f.isFile()) {
			long lastModified = f.lastModified();

			LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastModified),
					ZoneId.systemDefault());
			lblDateUpdate.setText(localDateTime.format(Controller.DATE_TIME_FORMATTER));
		} else {
			lblDateUpdate.setText(I18n.get("label.never"));
		}

	}

	/**
	 * Botón entrar, accede a la siguiente ventana
	 * 
	 * @param event El evento.
	 */

	public void enterCourse(ActionEvent event) {

		// Guardamos en una variable el curso seleccionado por el usuario

		Course selectedCourse = getSelectedCourse();
		if (selectedCourse == null) {
			lblNoSelect.setText(I18n.get("error.nocourse"));
			return;
		}

		LOGGER.info(" Curso seleccionado: {}", selectedCourse.getFullName());

		Config.setProperty("courseList", Integer.toString(tabPane.getSelectionModel().getSelectedIndex()));

		Config.setProperty("actualCourse", getSelectedCourse().getId());

		// if loading cache
		loadData(controller.getPassword());
		loadNextWindow();

	}

	public void removeCourse(ActionEvent event) {
		Course selectedCourse = getSelectedCourse();
		if (selectedCourse == null) {
			lblNoSelect.setText(I18n.get("error.nocourse"));
			return;
		}
		Alert alert = new Alert(AlertType.WARNING, I18n.get("text.confirmationtext"), ButtonType.OK, ButtonType.CANCEL);
		alert.setTitle(AppInfo.APPLICATION_NAME_WITH_VERSION);
		alert.setHeaderText(I18n.get("text.confirmation"));
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("/img/logo_min.png"));

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			cacheFilePath.toFile().delete();
			@SuppressWarnings("unchecked")
			ListView<Course> listView = (ListView<Course>) tabPane.getSelectionModel().getSelectedItem().getContent();
			listView.getItems().remove(selectedCourse);
			listView.getSelectionModel().clearSelection();
			lblDateUpdate.setText(null);

		}
	}

	private void loadData(String password) {

		DataBase dataBase;
		try {

			dataBase = (DataBase) Serialization.decrypt(password, cacheFilePath.toString());
			controller.setDataBase(dataBase);
			isBBDDLoaded = true;
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			incorrectPasswordWindow();
		} catch (InvalidClassException | ClassNotFoundException e) {
			LOGGER.warn("Se ha modificado una de las clases serializables", e);
			UtilMethods.errorWindow("Se ha modificado una de las clases serializables", e);
		}

	}

	private void incorrectPasswordWindow() {
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle(I18n.get("title.passwordIncorrect"));

		dialog.setHeaderText(I18n.get("header.passwordIncorrectMessage") + "\n" + I18n.get("header.passwordDateTime")
				+ lblDateUpdate.getText());

		Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
		dialogStage.getIcons().add(new Image("img/error.png"));
		dialog.getDialogPane().setGraphic(new ImageView("img/error.png"));
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

		PasswordField pwd = new PasswordField();
		HBox content = new HBox();
		content.setAlignment(Pos.CENTER);
		content.setSpacing(10);
		content.getChildren().addAll(new Label(I18n.get("label.oldPassword")), pwd);
		dialog.getDialogPane().setContent(content);

		// desabilitamos el boton hasta que no escriba texto
		Node accept = dialog.getDialogPane().lookupButton(ButtonType.OK);
		accept.setDisable(true);

		pwd.textProperty()
				.addListener((observable, oldValue, newValue) -> accept.setDisable(newValue.trim().isEmpty()));
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == ButtonType.OK) {
				return pwd.getText();
			}
			return null;
		});

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			controller.setPassword(result.get());
			loadData(result.get());
		}

	}

	private void loadNextWindow() {
		if (!isBBDDLoaded) {
			return;
		}

		UtilMethods.changeScene(getClass().getResource("/view/Main.fxml"), controller.getStage(), false);
		controller.getStage().setResizable(true);
		controller.getStage().setMaximized(true);
		controller.getStage().show();

	}

	/**
	 * Vuelve a la ventana de login de usuario.
	 * 
	 * @param actionEvent El ActionEvent.
	 */
	public void logOut(ActionEvent actionEvent) {
		LOGGER.info("Cerrando sesión de usuario");

		UtilMethods.changeScene(getClass().getResource("/view/Login.fxml"), controller.getStage());

	}
}
