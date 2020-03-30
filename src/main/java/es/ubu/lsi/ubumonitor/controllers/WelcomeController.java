package es.ubu.lsi.ubumonitor.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.controllers.ubugrades.CreatorGradeItems;
import es.ubu.lsi.ubumonitor.controllers.ubugrades.CreatorUBUGradesController;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.DownloadLogController;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.LogCreator;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.CourseCategory;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.Logs;
import es.ubu.lsi.ubumonitor.persistence.Serialization;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.Response;

/**
 * Clase controlador de la pantalla de bienvenida en la que se muestran los
 * cursos del usuario logueado.
 * 
 * @author Claudia Martínez Herrero
 * @author Yi Peng Ji
 * @version 2.0
 * @since 1.0
 */
public class WelcomeController implements Initializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(WelcomeController.class);

	/**
	 * path con directorios de los ficheros cache
	 */
	private Path cacheFilePath;
	private Controller controller = Controller.getInstance();
	private boolean isFileCacheExists;
	@FXML
	private AnchorPane anchorPane;
	@FXML
	private Label lblUser;
	@FXML
	private ListView<Course> listCourses;

	@FXML
	private ListView<Course> listCoursesFavorite;

	@FXML
	private ListView<Course> listCoursesRecent;

	@FXML
	private ListView<Course> listCoursesInProgress;

	@FXML
	private ListView<Course> listCoursesPast;

	@FXML
	private ListView<Course> listCoursesFuture;

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
	private ProgressBar progressBar;
	@FXML
	private Label lblProgress;
	@FXML
	private Label lblDateUpdate;
	@FXML
	private CheckBox chkUpdateData;
	@FXML
	private CheckBox chkOnlyWeb;
	private boolean isBBDDLoaded;

	@FXML
	private Label conexionLabel;
	private boolean autoUpdate;

	public WelcomeController() {
		this(false);
	}

	public WelcomeController(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	/**
	 * Función initialize. Muestra la lista de cursos del usuario introducido.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		try {
			conexionLabel.setText(I18n.get("text.online_" + !controller.isOfflineMode()));
			lblUser.setText(I18n.get("label.welcome") + " " + controller.getUser()
					.getFullName());
			progressBar.visibleProperty()
					.bind(btnEntrar.visibleProperty()
							.not());
			anchorPane.disableProperty()
					.bind(btnEntrar.visibleProperty()
							.not());
			lblProgress.visibleProperty()
					.bind(btnEntrar.visibleProperty()
							.not());
			btnRemove.visibleProperty()
					.bind(btnEntrar.visibleProperty());
			btnRemove.disableProperty()
					.bind(chkUpdateData.disabledProperty());
			chkOnlyWeb.visibleProperty()
					.bind(chkUpdateData.selectedProperty());
			chkOnlyWeb.setSelected(Boolean.parseBoolean(ConfigHelper.getProperty("onlyWeb", "false")));
			labelLoggedIn.setText(controller.getLoggedIn()
					.format(Controller.DATE_TIME_FORMATTER));
			labelHost.setText(controller.getUrlHost()
					.toString());

			initListViews();

			tabPane.getSelectionModel()
					.selectedItemProperty()
					.addListener((ov, value, newValue) -> {
						ListView<Course> listView = (ListView<Course>) value.getContent();
						listView.getSelectionModel()
								.clearSelection();
						chkUpdateData.setDisable(true);
						lblDateUpdate.setText(null);
					});
			tabPane.getSelectionModel()
					.select(ConfigHelper.getProperty("courseList", 0));

			Platform.runLater(() -> {
				ListView<Course> listView = (ListView<Course>) tabPane.getSelectionModel()
						.getSelectedItem()
						.getContent();
				Course course = controller.getUser()
						.getCourseById(ConfigHelper.getProperty("actualCourse", -1));

				listView.getSelectionModel()
						.select(course);
				listView.scrollTo(course);
				if (autoUpdate) {
					chkUpdateData.setSelected(true);
					btnEntrar.fire();
				}
			});

		} catch (Exception e) {
			LOGGER.error("Error al cargar los cursos", e);
		}

	}

	private void initListViews() {
		Comparator<Course> courseComparator = Comparator.comparing(Course::getFullName)
				.thenComparing(c -> c.getCourseCategory()
						.getName());

		initListView(controller.getUser()
				.getCourses(), listCourses, courseComparator);
		initListView(controller.getUser()
				.getFavoriteCourses(), listCoursesFavorite, courseComparator);
		initListView(controller.getUser()
				.getRecentCourses(), listCoursesRecent, null);
		initListView(controller.getUser()
				.getInProgressCourses(), listCoursesInProgress, courseComparator);
		initListView(controller.getUser()
				.getPastCourses(), listCoursesPast, courseComparator);
		initListView(controller.getUser()
				.getFutureCourses(), listCoursesFuture, courseComparator);
	}

	private void initListView(List<Course> courseList, ListView<Course> listView, Comparator<Course> comparator) {
		ObservableList<Course> observableList = FXCollections.observableArrayList(courseList);
		if (comparator != null) {
			observableList.sort(comparator);
		}
		listView.setItems(observableList);
		listView.getSelectionModel()
				.selectedItemProperty()
				.addListener((ov, value, newValue) -> checkFile(newValue));

	}

	private Course getSelectedCourse() {
		@SuppressWarnings("unchecked")
		ListView<Course> listView = (ListView<Course>) tabPane.getSelectionModel()
				.getSelectedItem()
				.getContent();
		return listView.getSelectionModel()
				.getSelectedItem();
	}

	private void checkFile(Course newValue) {
		if (newValue == null)
			return;
		cacheFilePath = controller.getDirectoryCache(newValue);
		LOGGER.debug("Buscando si existe {}", cacheFilePath);

		File f = cacheFilePath.toFile();

		if (f.exists() && f.isFile()) {
			chkUpdateData.setSelected(false);
			chkUpdateData.setDisable(false);
			long lastModified = f.lastModified();

			LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastModified),
					ZoneId.systemDefault());
			lblDateUpdate.setText(localDateTime.format(Controller.DATE_TIME_FORMATTER));
			isFileCacheExists = true;
		} else {
			chkUpdateData.setSelected(true);
			chkUpdateData.setDisable(true);
			lblDateUpdate.setText(I18n.get("label.never"));
			isFileCacheExists = false;
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
			lblNoSelect.setVisible(true);
			return;
		}
		lblNoSelect.setVisible(false);
		LOGGER.info(" Curso seleccionado: {}", selectedCourse.getFullName());

		ConfigHelper.setProperty("courseList", Integer.toString(tabPane.getSelectionModel()
				.getSelectedIndex()));

		ConfigHelper.setProperty("actualCourse", getSelectedCourse().getId());

		ConfigHelper.setProperty("onlyWeb", Boolean.toString(chkOnlyWeb.isSelected()));

		if (chkUpdateData.isSelected()) {
			if (isFileCacheExists) {
				loadData(controller.getPassword());
			} else {
				DataBase copyDataBase = new DataBase();
				copyDataBase.setUserPhoto(controller.getUser()
						.getUserPhoto());
				copyDataBase.setFullName(controller.getUser()
						.getFullName());
				Course copyCourse = copyCourse(copyDataBase, selectedCourse);
				controller.setDataBase(copyDataBase);
				controller.setActualCourse(copyCourse);
				isBBDDLoaded = true;
			}
			downloadData();
		} else { // if loading cache
			loadData(controller.getPassword());
			loadNextWindow();
		}

	}

	public void removeCourse() throws IOException {
		Alert alert = new Alert(AlertType.WARNING, I18n.get("text.confirmationtext"), ButtonType.OK, ButtonType.CANCEL);
		alert.setTitle(AppInfo.APPLICATION_NAME_WITH_VERSION);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.setHeaderText(I18n.get("text.confirmation"));
		Stage stage = (Stage) alert.getDialogPane()
				.getScene()
				.getWindow();
		stage.getIcons()
				.add(new Image("/img/logo_min.png"));

		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			Files.delete(cacheFilePath);
			@SuppressWarnings("unchecked")
			ListView<Course> listView = (ListView<Course>) tabPane.getSelectionModel()
					.getSelectedItem()
					.getContent();
			int index = listView.getSelectionModel()
					.getSelectedIndex();
			listView.getSelectionModel()
					.clearSelection();
			listView.getSelectionModel()
					.select(index);
		}
	}

	private Course copyCourse(DataBase copyDataBase, Course selectedCourse) {

		Course copyCourse = copyDataBase.getCourses()
				.getById(selectedCourse.getId());
		copyCourse.setStartDate(selectedCourse.getStartDate());
		copyCourse.setEndDate(selectedCourse.getEndDate());
		copyCourse.setSummary(selectedCourse.getSummary());
		copyCourse.setSummaryformat(selectedCourse.getSummaryformat());

		CourseCategory courseCategory = selectedCourse.getCourseCategory();
		CourseCategory copyCourseCategory = copyDataBase.getCourseCategories()
				.getById(courseCategory.getId());

		copyCourseCategory.setName(courseCategory.getName());

		copyCourse.setCourseCategory(copyCourseCategory);

		return copyCourse;
	}

	private void saveData() {

		if (!isBBDDLoaded) {
			return;
		}

		File f = controller.getDirectoryCache()
				.toFile();
		if (!f.isDirectory()) {
			LOGGER.info("No existe el directorio, se va a crear: {}", controller.getDirectoryCache());
			f.mkdirs();
		}
		LOGGER.info("Guardando los datos encriptados en: {}", f.getAbsolutePath());
		Serialization.encrypt(controller.getPassword(), cacheFilePath.toString(), controller.getDataBase());

	}

	private void loadData(String password) {

		DataBase dataBase;
		try {

			dataBase = (DataBase) Serialization.decrypt(password, cacheFilePath.toString());
			copyCourse(dataBase, getSelectedCourse());
			controller.setDataBase(dataBase);
			isBBDDLoaded = true;
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			previusPasswordWindow();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			UtilMethods.errorWindow(e.getMessage(), e);
		} catch (Exception e) {
			LOGGER.warn("Se ha modificado una de las clases serializables", e);
			UtilMethods.errorWindow(I18n.get("error.invalidcache"), e);
		}

	}

	private void previusPasswordWindow() {
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle(I18n.get("title.passwordChanged"));

		dialog.setHeaderText(I18n.get("header.passwordChangedMessage") + "\n" + I18n.get("header.passwordDateTime")
				+ lblDateUpdate.getText());

		Stage dialogStage = (Stage) dialog.getDialogPane()
				.getScene()
				.getWindow();
		dialogStage.getIcons()
				.add(new Image("img/error.png"));
		dialog.getDialogPane()
				.setGraphic(new ImageView("img/error.png"));
		dialog.getDialogPane()
				.getButtonTypes()
				.addAll(ButtonType.OK);

		PasswordField pwd = new PasswordField();
		HBox content = new HBox();
		content.setAlignment(Pos.CENTER);
		content.setSpacing(10);
		content.getChildren()
				.addAll(new Label(I18n.get("label.oldPassword")), pwd);
		dialog.getDialogPane()
				.setContent(content);

		// desabilitamos el boton hasta que no escriba texto
		Node accept = dialog.getDialogPane()
				.lookupButton(ButtonType.OK);
		accept.setDisable(true);

		pwd.textProperty()
				.addListener((observable, oldValue, newValue) -> accept.setDisable(newValue.trim()
						.isEmpty()));
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == ButtonType.OK) {
				return pwd.getText();
			}
			return null;
		});

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			loadData(result.get());
		}

	}

	private void downloadData() {

		if (!isBBDDLoaded) {
			return;
		}

		btnEntrar.setVisible(false);
		Task<Void> task = getUserDataWorker();
		lblProgress.textProperty()
				.bind(task.messageProperty());
		task.setOnSucceeded(v -> loadNextWindow());
		task.setOnFailed(e -> {
			controller.getStage()
					.getScene()
					.setCursor(Cursor.DEFAULT);
			btnEntrar.setVisible(true);
			UtilMethods.errorWindow(I18n.get("error.downloadingdata"), task.getException());
			LOGGER.error("Error al actualizar los datos del curso: {}", task.getException());

		});

		Thread thread = new Thread(task, "datos");
		thread.setDaemon(true);
		thread.start();

	}

	private void loadNextWindow() {
		if (!isBBDDLoaded) {
			return;
		}

		UtilMethods.changeScene(getClass().getResource("/view/Main.fxml"), controller.getStage(), false);
		controller.getStage()
				.setMaximized(true);
		controller.getStage()
				.setResizable(true);
		controller.getStage()
				.show();

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

	/**
	 * Realiza el proceso de carga de las notas de los alumnos, carga del arbol del
	 * calificador y generación de las estadisticas.
	 * 
	 * @return La tarea a realizar.
	 */
	private Task<Void> getUserDataWorker() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				Course actualCourse = controller.getActualCourse();
				actualCourse.clear();
				LOGGER.info("Cargando datos del curso: {}", actualCourse.getFullName());
				// Establecemos los usuarios matriculados
				updateMessage(I18n.get("label.loadingstudents"));
				CreatorUBUGradesController.createEnrolledUsers(actualCourse.getId());
				CreatorUBUGradesController.createSectionsAndModules(actualCourse.getId());
				updateMessage(I18n.get("label.loadingqualifier"));
				// Establecemos calificador del curso
				CreatorGradeItems creatorGradeItems = new CreatorGradeItems(new Locale(controller.getUser()
						.getLang()));
				creatorGradeItems.createGradeItems(controller.getActualCourse()
						.getId());
				updateMessage(I18n.get("label.loadingActivitiesCompletion"));
				CreatorUBUGradesController.createActivitiesCompletionStatus(actualCourse.getId(),
						actualCourse.getEnrolledUsers());

				int tries = 1;
				int limitRelogin = 3;
				while (tries < limitRelogin) {
					try {
						updateMessage(MessageFormat.format(I18n.get("label.downloadinglog"), tries, limitRelogin));
						if (!isFileCacheExists) {

							DownloadLogController downloadLogController = LogCreator.download();

							Response response = downloadLogController.downloadLog(chkOnlyWeb.isSelected());
							LOGGER.info("Log descargado");
							updateMessage(I18n.get("label.parselog"));

							Logs logs = new Logs(downloadLogController.getServerTimeZone());
							LogCreator.parserResponse(logs, response, actualCourse.getEnrolledUsers());
							actualCourse.setLogs(logs);

						} else {

							LogCreator.createLogsMultipleDays(actualCourse.getLogs(), actualCourse.getEnrolledUsers(),
									chkOnlyWeb.isSelected());

						}
						tries = limitRelogin;
					} catch (RuntimeException e) {

						if (tries >= limitRelogin) {
							throw e;
						}
						tries++;
						updateMessage(I18n.get("label.relogin"));
						controller.reLogin();

					}
				}
				updateMessage(I18n.get("label.loadingstats"));
				// Establecemos las estadisticas
				controller.createStats();

				updateMessage(I18n.get("label.savelocal"));
				saveData();

				return null;
			}
		};
	}

	/**
	 * Abre en el navegador el repositorio del proyecto.
	 * 
	 * @param actionEvent El ActionEvent.
	 */
	public void aboutApp() {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AboutApp.fxml"), I18n.getResourceBundle());

		UtilMethods.createDialog(loader, controller.getStage());

	}

}
