package es.ubu.lsi.ubumonitor.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.text.Collator;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.AppInfo;
import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.persistence.Serialization;
import es.ubu.lsi.ubumonitor.util.FileUtil;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

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

	private static final Pattern PATTERN_COURSE_FILE = Pattern.compile("^(.+)-(\\d+)$"); // ^(.+)-(\\d+)$
	//private static final Pattern PATTERN_PREVIOUS_COURSE_FILE = Pattern
		//	.compile("^\\((\\d{4}-\\d{2}-\\d{2})\\) (.+)-(\\d+)$"); // ^\((\d{4}-\d{2}-\d{2})\) (.+)-(\d+)$
	/**
	 * path con directorios de los ficheros cache
	 */
	private Controller controller = Controller.getInstance();
	
	private boolean newPassword;

	@FXML
	private AnchorPane anchorPane;
	@FXML
	private Label lblUser;
	@FXML
	private ListView<File> listCourses;

	@FXML
	private ListView<File> listViewPreviousCourses;

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
	
	@FXML
	private Tab tabPreviousCache;

	/**
	 * Función initialize. Muestra la lista de cursos del usuario introducido.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		try {
			conexionLabel.setText(I18n.get("text.online_" + !controller.isOfflineMode()));
			lblUser.setText(I18n.get("label.welcome") + " " + controller.getUsername());
			LOGGER.info("Cargando cursos...");

			anchorPane.disableProperty()
					.bind(btnEntrar.visibleProperty()
							.not());
			btnRemove.visibleProperty()
					.bind(btnEntrar.visibleProperty());

			labelLoggedIn.setText(controller.getLoggedIn()
					.format(Controller.DATE_TIME_FORMATTER));
			labelHost.setText(controller.getUrlHost()
					.toString());

			initListViews();

			tabPane.getSelectionModel()
					.selectedItemProperty()
					.addListener((ov, oldValue, newValue) -> {
						ListView<File> listView = (ListView<File>) oldValue.getContent();
						listView.getSelectionModel()
								.clearSelection();

					});

			// select tab and file from properties
			Platform.runLater(() -> {
				tabPane.getSelectionModel()
						.select(ConfigHelper.getProperty("tabIndexOfflineMode", 0));
				ListView<File> listView = (ListView<File>) tabPane.getSelectionModel()
						.getSelectedItem()
						.getContent();
				String pathLastCache = ConfigHelper.getProperty("listViewIndexOfflineMode", null);
				if (pathLastCache != null) {
					File file = new File(pathLastCache);
					if (listView.getItems()
							.contains(file)) {
						listView.getSelectionModel()
								.select(file);
						listView.scrollTo(file);
					}

				}
			});

		} catch (Exception e) {
			LOGGER.error("Error al cargar los cursos", e);
		}

	}

	private void initListViews() {

		File[] files = controller.getHostUserModelversionDir()
				.toFile()
				.listFiles((dir, name) -> PATTERN_COURSE_FILE.matcher(name)
						.matches());

		File[] previousCourses = controller.getHostUserModelversionArchivedDir()
				.toFile()
				.listFiles();
		Comparator<File> comparator = Comparator.comparing(File::getName, Collator.getInstance());
		initListView(files, listCourses, comparator);
		initListView(previousCourses, listViewPreviousCourses, comparator);

	}

	private void initListView(File[] files, ListView<File> listView, Comparator<File> comparator) {
		if (files == null || files.length == 0) {
			return;
		}

		ObservableList<File> observableList = FXCollections.observableArrayList(files);

		observableList.sort(comparator);

		listView.setItems(observableList);
		listView.getSelectionModel()
				.selectedItemProperty()
				.addListener((ov, value, newValue) -> checkFile(newValue));
		listView.setCellFactory(callback -> new ListCell<File>() {
			@Override
			public void updateItem(File file, boolean empty) {
				super.updateItem(file, empty);
				if (empty || file == null) {
					setText(null);
				} else {
					setText(file.getName());
				}
			}

		});

	}

	private File getSelectedCourse() {
		@SuppressWarnings("unchecked")
		ListView<File> listView = (ListView<File>) tabPane.getSelectionModel()
				.getSelectedItem()
				.getContent();
		return listView.getSelectionModel()
				.getSelectedItem();
	}

	private void checkFile(File file) {
		if (file == null)
			return;

		if (file.isFile()) {
			long lastModified = file.lastModified();

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

	public void enterCourse() {

		// Guardamos en una variable el curso seleccionado por el usuario

		File selectedCourse = getSelectedCourse();
		if (selectedCourse == null) {
			lblNoSelect.setVisible(true);
			return;
		}
		lblNoSelect.setVisible(false);
		LOGGER.info(" Curso seleccionado: {}", selectedCourse.getName());

		loadData(selectedCourse, controller.getPassword());
		if (newPassword && isBBDDLoaded) {
			Serialization.encrypt(controller.getPassword(), selectedCourse.toString(), controller.getDataBase());
		}
		loadNextWindow();

	}

	public void removeCourse() throws IOException {
		File selectedCourse = getSelectedCourse();
		if (selectedCourse == null) {
			lblNoSelect.setText(I18n.get("error.nocourse"));
			return;
		}
		Alert alert = new Alert(AlertType.WARNING, I18n.get("text.confirmationtext"), ButtonType.OK, ButtonType.CANCEL);
		alert.setTitle(AppInfo.APPLICATION_NAME_WITH_VERSION);
		alert.setHeaderText(I18n.get("text.confirmation"));
		Stage stage = (Stage) alert.getDialogPane()
				.getScene()
				.getWindow();
		stage.getIcons()
				.add(new Image("/img/logo_min.png"));

		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			Files.delete(selectedCourse.toPath());
			@SuppressWarnings("unchecked")
			ListView<File> listView = (ListView<File>) tabPane.getSelectionModel()
					.getSelectedItem()
					.getContent();
			listView.getItems()
					.remove(selectedCourse);
			listView.getSelectionModel()
					.clearSelection();
			lblDateUpdate.setText(null);

		}
	}

	private void loadData(File file, String password) {

		DataBase dataBase;
		try {

			dataBase = (DataBase) Serialization.decrypt(password, file.toString());
			dataBase.checkSubDatabases();
			controller.setDataBase(dataBase);
			
			controller.setDefaultUpdate(
					ZonedDateTime.ofInstant(Instant.ofEpochSecond(file.lastModified()), ZoneId.systemDefault()));
			ZoneId zoneId = dataBase.getUserZoneId() == null ? ZoneId.systemDefault() : dataBase.getUserZoneId();
			TimeZone.setDefault(TimeZone.getTimeZone(zoneId));
			controller.setActualCoursePath(file.toPath());
			isBBDDLoaded = true;
		} catch (IllegalBlockSizeException | BadPaddingException | IllegalArgumentException e) {
			incorrectPasswordWindow(file);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			UtilMethods.errorWindow(e.getMessage(), e);

		} catch (Exception e) {
			LOGGER.warn("Se ha modificado una de las clases serializables", e);
			UtilMethods.errorWindow(I18n.get("error.invalidcache"), e);

		}

	}

	private void incorrectPasswordWindow(File file) {
		Dialog<Pair<String, Boolean>> dialog = new Dialog<>();
		dialog.setTitle(I18n.get("title.passwordIncorrect"));

		dialog.setHeaderText(I18n.get("header.passwordIncorrectMessage") + "\n" + I18n.get("header.passwordDateTime")
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

		GridPane grid = new GridPane();

		grid.setHgap(20);
		grid.setVgap(20);
		grid.setPadding(new Insets(20, 50, 10, 50));
		grid.add(new Label(I18n.get("label.oldPassword")), 0, 0);
		PasswordField pwd = new PasswordField();

		grid.add(pwd, 1, 0);

		CheckBox saveNewPassword = new CheckBox(I18n.get("checkbox.saveNewPassword"));
		saveNewPassword.setSelected(true);
		grid.add(saveNewPassword, 0, 1, 2, 1);
		dialog.getDialogPane()
				.setContent(grid);

		
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == ButtonType.OK) {
				return new Pair<String, Boolean>(pwd.getText(), saveNewPassword.isSelected());
			}
			return null;
		});

		// Traditional way to get the response value.
		Optional<Pair<String, Boolean>> result = dialog.showAndWait();
		if (result.isPresent()) {
			loadData(file, result.get().getKey());
			newPassword = result.get().getValue();
		}

	}

	private void loadNextWindow() {
		if (!isBBDDLoaded) {
			return;
		}
		ConfigHelper.setProperty("tabIndexOfflineMode", tabPane.getSelectionModel()
				.getSelectedIndex());

		File file = getSelectedCourse();
		ConfigHelper.setProperty("listViewIndexOfflineMode", file.toString());

		LocalDateTime lastModified = LocalDateTime.ofInstant(Instant.ofEpochMilli(getSelectedCourse().lastModified()),
				ZoneId.systemDefault());

		WelcomeController.changeToMainScene(controller.getStage(), getClass().getResource("/view/Main.fxml"),
				getClass().getResource("/img/alert.gif")
						.toExternalForm(),
				lastModified, !tabPreviousCache.isSelected());

	}

	/**
	 * Vuelve a la ventana de login de usuario.
	 * 
	 * @param actionEvent El ActionEvent.
	 */
	public void logOut() {
		LOGGER.info("Cerrando sesión de usuario");

		UtilMethods.changeScene(getClass().getResource("/view/Login.fxml"), controller.getStage());

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
	
	public void exportCourse() {
		File file = getSelectedCourse();
		if(file==null) {
			lblNoSelect.setVisible(true);
			return;
		}
		UtilMethods.fileAction("(" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ") " + file,
				ConfigHelper.getProperty("coursePath", "./"), controller.getStage(), FileUtil.FileChooserType.SAVE,
				f -> {
					FileUtil.exportFile(file.toPath(), f.getParentFile().toPath(), f.toPath());
					ConfigHelper.setProperty("coursePath", file.getParent());
				}, FileUtil.ALL);
		
	}
	
	public void importCourse() {
		
		UtilMethods.fileAction(null,
				ConfigHelper.getProperty("coursePath", "./"), controller.getStage(), FileUtil.FileChooserType.OPEN,
				f -> {
					
					FileUtil.exportFile( f.toPath() ,controller.getHostUserModelversionArchivedDir(), controller.getHostUserModelversionArchivedDir().resolve(f.getName()));
					ConfigHelper.setProperty("coursePath", f.getParent());
					tabPane.getSelectionModel().select(1);
					if(!listViewPreviousCourses.getItems().contains(f)) {
						listViewPreviousCourses.getItems().add(f);
						listViewPreviousCourses.getSelectionModel().select(f);
					}
					
				},false, FileUtil.ALL);
	}
}
