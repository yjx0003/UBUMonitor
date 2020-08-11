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
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.AppInfo;
import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.controllers.load.Connection;
import es.ubu.lsi.ubumonitor.controllers.load.DownloadLogController;
import es.ubu.lsi.ubumonitor.controllers.load.LogCreator;
import es.ubu.lsi.ubumonitor.controllers.load.PopulateActivityCompletion;
import es.ubu.lsi.ubumonitor.controllers.load.PopulateCourse;
import es.ubu.lsi.ubumonitor.controllers.load.PopulateCourseContent;
import es.ubu.lsi.ubumonitor.controllers.load.PopulateCourseEvent;
import es.ubu.lsi.ubumonitor.controllers.load.PopulateEnrolledUsersCourse;
import es.ubu.lsi.ubumonitor.controllers.load.PopulateForum;
import es.ubu.lsi.ubumonitor.controllers.load.PopulateGradeItem;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.CourseCategory;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.DiscussionPost;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.ForumDiscussion;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.LogStats;
import es.ubu.lsi.ubumonitor.model.Logs;
import es.ubu.lsi.ubumonitor.model.ModuleType;
import es.ubu.lsi.ubumonitor.model.Section;
import es.ubu.lsi.ubumonitor.model.Stats;
import es.ubu.lsi.ubumonitor.model.log.TypeTimes;
import es.ubu.lsi.ubumonitor.persistence.Serialization;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.webservice.api.core.course.CoreCourseSearchCourses;
import es.ubu.lsi.ubumonitor.webservice.webservices.WebService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
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
	private ListView<Course> listViewSearch;

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
	private Button buttonCancelDownload;

	@FXML
	private ProgressBar progressBar;
	@FXML
	private Label lblProgress;
	@FXML
	private Label lblDateUpdate;
	@FXML
	private CheckBox chkUpdateData;

	private boolean isBBDDLoaded;

	@FXML
	private Label conexionLabel;
	private boolean autoUpdate;

	@FXML
	private CheckBox checkBoxGradeItem;
	@FXML
	private CheckBox checkBoxActivityCompletion;
	@FXML
	private CheckBox checkBoxLogs;

	@FXML
	private TextField textFieldSearch;

	@FXML
	private Button buttonSearch;

	@FXML
	private Button buttonLogout;

	@FXML
	private Label totalLabel;

	@FXML
	private Label labelShowing;

	@FXML
	private HBox horizontalBox;

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
			buttonLogout.disableProperty()
					.bind(btnEntrar.visibleProperty()
							.not());

			manageCheckBox(checkBoxGradeItem, chkUpdateData);
			manageCheckBox(checkBoxActivityCompletion, chkUpdateData);
			manageCheckBox(checkBoxLogs, chkUpdateData);

			labelLoggedIn.setText(controller.getLoggedIn()
					.format(Controller.DATE_TIME_FORMATTER));
			labelHost.setText(controller.getUrlHost()
					.toString());

			initListViews();

			tabPane.getSelectionModel()
					.selectedItemProperty()
					.addListener((ov, value, newValue) -> {

						ListView<Course> listView;
						if (value.getContent() instanceof ListView<?>) {
							listView = (ListView<Course>) value.getContent();

						} else {

							listView = listViewSearch;
						}
						listView.getSelectionModel()
								.clearSelection();
						chkUpdateData.setDisable(true);
						lblDateUpdate.setText(null);
						if (!(newValue.getContent() instanceof ListView<?>)) {
							Platform.runLater(() -> textFieldSearch.requestFocus());
							buttonSearch.setDefaultButton(true);
							btnEntrar.setDefaultButton(false);
						} else {
							btnEntrar.setDefaultButton(true);
							buttonSearch.setDefaultButton(false);
						}
					});
			tabPane.getSelectionModel()
					.select(ConfigHelper.getProperty("courseList", 0));

			Platform.runLater(() -> {
				ListView<Course> listView = getActualListView();

				Course course = controller.getUser()
						.getCourseById(ConfigHelper.getProperty("actualCourse", -1));

				listView.getSelectionModel()
						.select(course);
				listView.scrollTo(course);
				if (autoUpdate) {
					chkUpdateData.setSelected(true);
					btnEntrar.fire();
				}
				if (Files.isDirectory(controller.getHostUserDir())
						&& !Files.isDirectory(controller.getHostUserModelversionDir())) {
					UtilMethods.warningWindow(I18n.get("text.modelversionchanged"));
					controller.getHostUserModelversionDir()
							.toFile()
							.mkdirs();
				}

			});

		} catch (Exception e) {
			LOGGER.error("Error al cargar los cursos", e);
		}

	}

	@SuppressWarnings("unchecked")
	private ListView<Course> getActualListView() {
		ListView<Course> listView;
		if (tabPane.getSelectionModel()
				.getSelectedItem()
				.getContent() instanceof ListView<?>) {
			listView = (ListView<Course>) tabPane.getSelectionModel()
					.getSelectedItem()
					.getContent();

		} else {
			listView = listViewSearch;
		}
		return listView;
	}

	private void manageCheckBox(CheckBox checkBox1, CheckBox checkBox2) {
		checkBox1.visibleProperty()
				.bind(checkBox2.selectedProperty());

	}

	private void initListViews() {
		Comparator<Course> courseComparator = Course.getCourseComparator();

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
		listViewSearch.getSelectionModel()
				.selectedItemProperty()
				.addListener((ov, value, newValue) -> checkFile(newValue));

		listViewSearch.setCellFactory(callback -> new ListCell<Course>() {
			@Override
			protected void updateItem(Course course, boolean empty) {
				super.updateItem(course, empty);

				if (empty || course == null || !course.hasCourseAccess()) {
					setDisable(true);

				} else {
					setDisable(false);

					setFocusTraversable(course.hasCourseAccess());

				}

				setText(course == null ? null : course.toString());
				setMouseTransparent(isDisable());
				setFocusTraversable(!isDisable());
			}
		});

		listViewSearch.setOnKeyPressed(Event::consume);
		horizontalBox.visibleProperty()
				.bind(Bindings.isEmpty(listViewSearch.getItems())
						.not());
		horizontalBox.managedProperty()
				.bind(horizontalBox.visibleProperty());
		labelShowing.textProperty()
				.bind(Bindings.size(listViewSearch.getItems())
						.asString());

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
		return getActualListView().getSelectionModel()
				.getSelectedItem();
	}

	private void checkFile(Course course) {
		if (course == null)
			return;
		cacheFilePath = controller.getHostUserModelversionDir().resolve(controller.getCourseFile(course));
		LOGGER.debug("Buscando si existe {}", cacheFilePath);

		File f = cacheFilePath.toFile();

		if (f.exists() && f.isFile()) { // exist local file

			long lastModified = f.lastModified();

			LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastModified),
					ZoneId.systemDefault());
			lblDateUpdate.setText(localDateTime.format(Controller.DATE_TIME_FORMATTER));
			isFileCacheExists = true;
		} else {

			lblDateUpdate.setText(I18n.get("label.never"));
			isFileCacheExists = false;
		}
		chkUpdateData.setSelected(!isFileCacheExists);
		chkUpdateData.setDisable(!isFileCacheExists);
		checkBoxGradeItem.setSelected(course.hasGradeItemAccess());
		checkBoxGradeItem.setDisable(!course.hasGradeItemAccess());
		checkBoxLogs.setSelected(course.hasReportAccess());
		checkBoxLogs.setDisable(!course.hasReportAccess());
		checkBoxActivityCompletion.setSelected(course.hasActivityCompletion());
		checkBoxActivityCompletion.setDisable(!course.hasActivityCompletion());
	}

	/**
	 * Botón entrar, accede a la siguiente ventana
	 * 
	 * @param event El evento.
	 */

	public void enterCourse() {

		// Guardamos en una variable el curso seleccionado por el usuario

		Course selectedCourse = getSelectedCourse();
		if (selectedCourse == null) {
			lblNoSelect.setVisible(true);
			return;
		}
		lblNoSelect.setVisible(false);
		LOGGER.info(" Curso seleccionado: {}", selectedCourse.getFullName());

		ConfigHelper.setProperty("courseList", tabPane.getSelectionModel()
				.getSelectedIndex());

		ConfigHelper.setProperty("actualCourse", getSelectedCourse().getId());

		if (chkUpdateData.isSelected()) {
			if (isFileCacheExists) {
				loadData(controller.getPassword());
			} else {
				DataBase copyDataBase = new DataBase();
				copyDataBase.setUserPhoto(controller.getUser()
						.getUserPhoto());
				copyDataBase.setFullName(controller.getUser()
						.getFullName());
				copyDataBase.setUserZoneId(controller.getUser()
						.getTimezone());
				TimeZone.setDefault(TimeZone.getTimeZone(copyDataBase.getUserZoneId()));
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

			ListView<Course> listView = getActualListView();
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
		LOGGER.debug("entramos a savedata");
		if (!isBBDDLoaded) {
			return;
		}

		File f = controller.getHostUserModelversionDir()
				.toFile();
		if (!f.isDirectory()) {
			LOGGER.info("No existe el directorio, se va a crear: {}", controller.getHostUserModelversionDir());
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
			controller.setDefaultUpdate(ZonedDateTime.ofInstant(Instant.ofEpochSecond(cacheFilePath.toFile()
					.lastModified()), ZoneId.systemDefault()));
			TimeZone.setDefault(TimeZone.getTimeZone(dataBase.getUserZoneId()));
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			previusPasswordWindow();
		} catch (IOException e) {
			UtilMethods.errorWindow(e.getMessage(), e);
			throw new IllegalStateException(e);
		} catch (Exception e) {
			UtilMethods.errorWindow(I18n.get("error.invalidcache"), e);
			throw new IllegalStateException("Se ha modificado una de las clases serializables", e);
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

		Service<Void> service = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return getUserDataWorker(controller.getWebService(), controller.getDataBase());
			}

		};
		lblProgress.textProperty()
				.bind(service.messageProperty());
		service.setOnSucceeded(v -> {
			controller.setDefaultUpdate(ZonedDateTime.now());

			loadNextWindow();
		});
		service.setOnFailed(e -> {
			controller.getStage()
					.getScene()
					.setCursor(Cursor.DEFAULT);

			UtilMethods.errorWindow(I18n.get("error.downloadingdata") + " " + service.getException()
					.getMessage(), service.getException());
			LOGGER.error("Error al actualizar los datos del curso: {}", service.getException());

		});
		service.setOnCancelled(e -> {
			controller.getStage()
					.getScene()
					.setCursor(Cursor.DEFAULT);
			controller.setDataBase(null);
		});

		btnEntrar.visibleProperty()
				.bind(service.runningProperty()
						.not());
		buttonCancelDownload.visibleProperty()
				.bind(service.runningProperty());
		buttonCancelDownload.setOnAction(e -> service.cancel());
		service.start();

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
		Connection.clearCookies();
		UtilMethods.changeScene(getClass().getResource("/view/Login.fxml"), controller.getStage());

	}

	/**
	 * Realiza el proceso de carga de las notas de los alumnos, carga del arbol del
	 * calificador y generación de las estadisticas.
	 * 
	 * @return La tarea a realizar.
	 */
	private Task<Void> getUserDataWorker(WebService webService, DataBase dataBase) {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Course actualCourse = dataBase.getActualCourse();
				LOGGER.info("Cargando datos del curso: {}", actualCourse.getFullName());
				// Establecemos los usuarios matriculados

				actualCourse.clearCourseData();
				updateMessage(I18n.get("label.loadingcoursedata"));
				PopulateEnrolledUsersCourse populateEnrolledUsersCourse = new PopulateEnrolledUsersCourse(dataBase,
						webService);
				actualCourse.addEnrolledUsers(populateEnrolledUsersCourse.createEnrolledUsers(actualCourse.getId()));

				PopulateCourseContent populateCourseContent = new PopulateCourseContent(webService, dataBase);
				Pair<List<Section>, List<CourseModule>> pair = populateCourseContent
						.populateCourseContent(actualCourse.getId());
				actualCourse.addSections(pair.getKey());
				actualCourse.addCourseModules(pair.getValue());

				PopulateForum populateForum = new PopulateForum(dataBase, webService);
				List<ForumDiscussion> forumDiscussions = populateForum.populateForumDiscussions(pair.getValue()
						.stream()
						.filter(cm -> cm.getModuleType() == ModuleType.FORUM)
						.collect(Collectors.toList()));

				List<DiscussionPost> discussionPosts = populateForum.populateDiscussionPosts(forumDiscussions.stream()
						.map(ForumDiscussion::getId)
						.collect(Collectors.toList()));
				actualCourse.addDiscussionPosts(discussionPosts);
				
				PopulateCourseEvent populateCourseEvent = new PopulateCourseEvent(dataBase, webService);
				actualCourse.addCourseEvents(populateCourseEvent.populateCourseEvents(actualCourse.getId()));
				
				actualCourse.setUpdatedCourseData(ZonedDateTime.now());

				if (checkBoxGradeItem.isSelected() && !isCancelled()) {
					actualCourse.getGradeItems()
							.clear();
					updateMessage(I18n.get("label.loadingqualifier"));
					// Establecemos calificador del curso
					PopulateGradeItem populateGradeItem = new PopulateGradeItem(dataBase, webService);
					List<GradeItem> gradeItems = populateGradeItem.createGradeItems(actualCourse.getId(),
							controller.getUser()
									.getId());

					actualCourse.setGradeItems(new HashSet<>(gradeItems));
					// Establecemos las estadisticas
					Stats stats = new Stats(actualCourse);
					actualCourse.setStats(stats);
					actualCourse.setUpdatedGradeItem(ZonedDateTime.now());
				}
				if (checkBoxActivityCompletion.isSelected() && !isCancelled()) {
					actualCourse.getModules()
							.forEach(m -> m.getActivitiesCompletion()
									.clear());
					updateMessage(I18n.get("label.loadingActivitiesCompletion"));
					PopulateActivityCompletion populateActivityCompletion = new PopulateActivityCompletion(dataBase,
							webService);
					populateActivityCompletion.creeateActivitiesCompletionStatus(actualCourse.getId(),
							actualCourse.getEnrolledUsers());
					actualCourse.setUpdatedActivityCompletion(ZonedDateTime.now());
				}
				if (checkBoxLogs.isSelected()) {
					int tries = 1;
					int limitRelogin = 3;
					while (tries <= limitRelogin && !isCancelled()) {
						try {

							updateMessage(MessageFormat.format(I18n.get("label.downloadinglog"), tries, limitRelogin));
							if (actualCourse.getUpdatedLog() == null) {

								DownloadLogController downloadLogController = LogCreator.download();

								Response response = downloadLogController.downloadLog(false);
								LOGGER.info("Log descargado");
								updateMessage(I18n.get("label.parselog"));
								Logs logs = new Logs(downloadLogController.getServerTimeZone());
								LogCreator.parserResponse(logs, response);
								actualCourse.setLogs(logs);

							} else {

								LogCreator.createLogsMultipleDays(actualCourse.getLogs());

							}

							LogStats logStats = new LogStats(actualCourse.getLogs()
									.getList());
							actualCourse.setLogStats(logStats);
							Set<EnrolledUser> notEnrolled = logStats.getByType(TypeTimes.ALL)
									.getComponents()
									.getUsers()
									.stream()
									.filter(user -> !actualCourse.getEnrolledUsers()
											.contains(user))
									.collect(Collectors.toSet());
							List<Integer> ids = notEnrolled.stream()
									.map(EnrolledUser::getId)
									.collect(Collectors.toList());
							LOGGER.info("Los ids de usuarios no matriculados: {}", ids);
							populateEnrolledUsersCourse.searchUser(ids);
							actualCourse.setNotEnrolledUser(notEnrolled);
							actualCourse.setUpdatedLog(ZonedDateTime.now());

							tries = limitRelogin + 1;
						} catch (Exception e) {
							if (tries >= limitRelogin) {
								throw e;
							}
							tries++;
							updateMessage(I18n.get("label.relogin"));
							controller.getLogin()
									.reLogin(controller.getUrlHost()
											.toString(), controller.getUsername(), controller.getPassword());

						}
					}
				}
				if (!isCancelled()) {
					updateMessage(I18n.get("label.savelocal"));
					saveData();
				}

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

	public void onActionSearch() {
		String text = textFieldSearch.getText();
		if (text == null || text.trim()
				.isEmpty()) {
			UtilMethods.errorWindow(I18n.get("error.emptytextfield"));
			return;
		}

		try {
			PopulateCourse populateCourse = new PopulateCourse(controller.getDataBase(), controller.getWebService());
			CoreCourseSearchCourses coreCourseSearchCourses = new CoreCourseSearchCourses();
			coreCourseSearchCourses.setBySearch(text);
			JSONObject jsonObject = UtilMethods.getJSONObjectResponse(controller.getWebService(),
					coreCourseSearchCourses);
			List<Course> courses = populateCourse.searchCourse(jsonObject);

			if (courses.isEmpty()) {
				UtilMethods.warningWindow(I18n.get("warning.nofound"));

			} else {

				populateCourse.createCourseAdministrationOptions(courses.stream()
						.map(Course::getId)
						.collect(Collectors.toList()));
				Collections.sort(courses, Comparator.comparing(Course::hasCourseAccess, Comparator.reverseOrder())
						.thenComparing(Course.getCourseComparator()));
				listViewSearch.getItems()
						.setAll(courses);
				totalLabel.setText(Integer.toString(jsonObject.getInt("total")));
			}

		} catch (Exception e) {
			UtilMethods.errorWindow("Error when searching", e);
		}

	}

}
