package controllers;

import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.ubugrades.CreatorGradeItems;
import controllers.ubugrades.CreatorUBUGradesController;
import controllers.ubulogs.LogCreator;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Course;
import model.CourseCategory;
import model.DataBase;
import model.Logs;
import persistence.Encryption;
import util.UtilMethods;

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
	 * Path de los directorios del cache sin el fichero encriptado
	 */
	private Path directoryCache;
	/**
	 * path con directorios de los ficheros cache
	 */
	private Path cacheFilePath;
	private Controller controller = Controller.getInstance();
	private boolean isFileCacheExists;

	@FXML
	private Label lblUser;
	@FXML
	private ListView<Course> listCourses;
	@FXML
	private Label lblNoSelect;
	@FXML
	private Button btnEntrar;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Label lblProgress;
	@FXML
	private Label lblDateUpdate;
	@FXML
	private CheckBox chkUpdateData;

	private boolean isBBDDLoaded;

	/**
	 * Función initialize. Muestra la lista de cursos del usuario introducido.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		try {
			directoryCache = Paths.get(AppInfo.CACHE_DIR, controller.getUrlHost().getHost(),
					controller.getUser().getFullName() + "-" + controller.getUser().getId());

			lblUser.setText(controller.getUser().getFullName());
			LOGGER.info("Cargando cursos...");

			ObservableList<Course> list = FXCollections.observableArrayList(controller.getUser().getCourses());
			Collections.sort(list, Comparator.comparing(Course::getFullName)
					.thenComparing(c -> c.getCourseCategory().getName()));

			progressBar.setVisible(false);
			listCourses.setItems(list);
			chkUpdateData.setDisable(true);
			listCourses.getSelectionModel().selectedItemProperty().addListener((ov, value, newValue) -> {

				cacheFilePath = directoryCache.resolve(UtilMethods.removeReservedChar(newValue.getFullName()) + "-" + newValue.getId());
				LOGGER.debug("Buscando si existe {}", cacheFilePath);

				File f = cacheFilePath.toFile();

				if (f.exists() && f.isFile()) {
					chkUpdateData.setSelected(false);
					chkUpdateData.setDisable(false);
					long lastModified = f.lastModified();

					LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastModified),
							ZoneId.systemDefault());
					lblDateUpdate.setText(localDateTime.format(Controller.DATE_TIME_FORMATTER));
					isFileCacheExists = false;
				} else {
					chkUpdateData.setSelected(true);
					chkUpdateData.setDisable(true);
					lblDateUpdate.setText(I18n.get("label.never"));
					isFileCacheExists = true;
				}
			});

		} catch (Exception e) {
			LOGGER.error("Error al cargar los cursos", e);
		}

	}

	/**
	 * Botón entrar, accede a la siguiente ventana
	 * 
	 * @param event
	 *            El evento.
	 */
	public void enterCourse(ActionEvent event) {

		// Guardamos en una variable el curso seleccionado por el usuario
		Course selectedCourse = listCourses.getSelectionModel().getSelectedItem();
		if (selectedCourse == null) {
			lblNoSelect.setText(I18n.get("error.nocourse"));
			return;
		}

		LOGGER.info(" Curso seleccionado: {}", selectedCourse.getFullName());

		if (chkUpdateData.isSelected()) {
			if (!isFileCacheExists) {
				loadData(controller.getPassword());
			} else {
				DataBase copyDataBase = new DataBase();
				Course copyCourse = copyDataBase(copyDataBase, selectedCourse);
				controller.setDataBase(copyDataBase);
				controller.setActualCourse(copyCourse);
				isBBDDLoaded = true;
			}
			downloadData();
		} else {
			loadData(controller.getPassword());
			loadNextWindow();
		}

	}

	private Course copyDataBase(DataBase copyDataBase, Course selectedCourse) {

		Course copyCourse = copyDataBase.getCourses().getById(selectedCourse.getId());
		copyCourse.setStartDate(selectedCourse.getStartDate());
		copyCourse.setEndDate(selectedCourse.getEndDate());
		copyCourse.setSummary(selectedCourse.getSummary());
		copyCourse.setSummaryformat(selectedCourse.getSummaryformat());

		CourseCategory courseCategory = selectedCourse.getCourseCategory();
		CourseCategory copyCourseCategory = copyDataBase.getCourseCategories().getById(courseCategory.getId());

		copyCourseCategory.setName(courseCategory.getName());

		copyCourse.setCourseCategory(copyCourseCategory);

		return copyCourse;
	}

	private void saveData() {

		if (!isBBDDLoaded) {
			return;
		}

		File f = directoryCache.toFile();
		if (!f.isDirectory()) {
			LOGGER.info("No existe el directorio, se va a crear: {}", directoryCache);
			f.mkdirs();
		}
		LOGGER.info("Guardando los datos encriptados en: {}", f.getAbsolutePath());
		Encryption.encrypt(controller.getPassword(), cacheFilePath.toString(), controller.getDataBase());

	}

	private void loadData(String password) {

		DataBase dataBase;
		try {
			dataBase = (DataBase) Encryption.decrypt(password, cacheFilePath.toString());
			copyDataBase(dataBase, listCourses.getSelectionModel().getSelectedItem());
			controller.setDataBase(dataBase);
			isBBDDLoaded = true;
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			previusPasswordWindow();
		} catch (InvalidClassException | ClassNotFoundException e) {
			LOGGER.warn("Se ha modificado una de las clases serializables", e);
			// TODO eliminar fichero y descargar de nuevo
		}

	}

	private void previusPasswordWindow() {
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle(I18n.get("title.passwordChanged"));

		dialog.getDialogPane().setGraphic(new ImageView("img/error.png"));
		dialog.setHeaderText(
				I18n.get("header.passwordChangedMessage") + "\n"
						+ I18n.get("header.passwordDateTime")
						+ lblDateUpdate.getText());

		Image errorIcon = new Image("img/error.png");
		Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
		dialogStage.getIcons().add(errorIcon);
		dialog.setGraphic(new ImageView(errorIcon));
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
			loadData(result.get());
			if (!chkUpdateData.isSelected()) {
				saveData(); // si no esta seleccionado el checkbox actualizar, volvemos a guardar en cache
							// con la nueva contraseña, en caso contrario ya se guarda si o si en el metodo
							// download data.
			}
		}

	}

	private void downloadData() {

		if (!isBBDDLoaded) {
			return;
		}

		btnEntrar.setVisible(false);
		lblProgress.setVisible(true);
		progressBar.setVisible(true);
		Task<Void> task = getUserDataWorker();
		lblProgress.textProperty().bind(task.messageProperty());
		task.setOnSucceeded(v -> loadNextWindow());
		task.setOnFailed(e -> {
			errorWindow(task.getException().getMessage());
			LOGGER.error("Error al actualizar los datos del curso: {}", task.getException());
		});

		Thread thread = new Thread(task, "datos");
		thread.start();

	}

	private void loadNextWindow() {
		if (!isBBDDLoaded) {
			return;
		}

		try {
			// Accedemos a la siguiente ventana
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Main.fxml"),
					I18n.getResourceBundle());
			controller.getStage().close();
			controller.setStage(new Stage());
			Parent root = loader.load();
			Scene scene = new Scene(root);
			controller.getStage().setScene(scene);
			controller.getStage().getIcons().add(new Image("/img/logo_min.png"));
			controller.getStage().setTitle(AppInfo.APPLICATION_NAME);
			controller.getStage().setResizable(true);
			controller.getStage().setMinHeight(600);
			controller.getStage().setMinWidth(800);
			controller.getStage().setMaximized(true);
			controller.getStage().show();
			lblNoSelect.setText("");
		} catch (IOException e) {

			LOGGER.info("No se ha podido cargar la ventana Main.fxml: {}", e);
			errorWindow("No se ha podido cargar la ventana Main.fxml");
		}
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
				try {
					controller.getActualCourse().clear();
					LOGGER.info("Cargando datos del curso: {}", controller.getActualCourse().getFullName());
					// Establecemos los usuarios matriculados
					updateMessage(I18n.get("label.loadingstudents"));
					CreatorUBUGradesController.createEnrolledUsers(controller.getActualCourse().getId());
					CreatorUBUGradesController.createSectionsAndModules(controller.getActualCourse().getId());
					updateMessage(I18n.get("label.loadingqualifier"));
					// Establecemos calificador del curso
					CreatorGradeItems creatorGradeItems = new CreatorGradeItems(
							new Locale(controller.getUser().getLang()));
					creatorGradeItems.createGradeItems(controller.getActualCourse().getId());

					updateMessage(I18n.get("label.updatinglog"));
					if (isFileCacheExists) {
						Logs logs = LogCreator.createCourseLog();
						controller.getActualCourse().setLogs(logs);

					} else {
						Logs logs = controller.getActualCourse().getLogs();
						LogCreator.updateCourseLog(logs);

					}
					updateMessage(I18n.get("label.loadingstats"));
					// Establecemos las estadisticas
					controller.createStats();

					updateMessage(I18n.get("label.savelocal"));
					saveData();
				} catch (IOException e) {
					LOGGER.error("Error al cargar los datos de los alumnos: {}", e);
					updateMessage("Se produjo un error inesperado al cargar los datos.\n" + e.getMessage());
					throw new IllegalStateException();
				} finally {
					controller.getStage().getScene().setCursor(Cursor.DEFAULT);
					progressBar.setVisible(false);
					lblProgress.setVisible(false);
					btnEntrar.setVisible(true);
				}
				return null;
			}
		};
	}

	/**
	 * Muestra una ventana de error.
	 * 
	 * @param mensaje
	 *            El mensaje que se quiere mostrar.
	 */
	private void errorWindow(String mensaje) {
		Alert alert = new Alert(AlertType.ERROR);

		alert.setTitle("UBUMonitor");
		alert.setHeaderText("Error");
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(controller.getStage());
		alert.getDialogPane().setContentText(mensaje);

		ButtonType buttonSalir = new ButtonType(I18n.get("label.close"));
		alert.getButtonTypes().setAll(buttonSalir);

		alert.showAndWait();
	}

}
