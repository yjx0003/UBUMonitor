package controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.ubugrades.CreatorGradeItems;
import controllers.ubugrades.CreatorUBUGradesController;
import controllers.ubulogs.logcreator.LogCreator;
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
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.BBDD;
import model.Course;
import model.Logs;
import persistence.Encryption;

/**
 * Clase controlador de la pantalla de bienvenida en la que se muestran los
 * cursos del usuario logueado.
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class WelcomeController implements Initializable {

	static final Logger logger = LoggerFactory.getLogger(WelcomeController.class);
	private String directoryObject;
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

	private boolean isBBDDLoaded = false;

	/**
	 * Función initialize. Muestra la lista de cursos del usuario introducido.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		try {
			directoryObject = "cache/" + controller.getUser().getFullName() + "/";
			lblUser.setText(controller.getUser().getFullName());
			logger.info("Cargando cursos...");

			ObservableList<Course> list = FXCollections.observableArrayList(controller.getUser().getCourses());
			Collections.sort(list,Comparator.comparing(Course::getFullName));
			progressBar.setVisible(false);
			listCourses.setItems(list);
			chkUpdateData.setDisable(true);
			// Deshabilitar boton hasta que se seleccione un elemento de la lista
			// btnEntrar.disableProperty().bind(Bindings.isEmpty(listCourses.getSelectionModel().getSelectedItems()));

			listCourses.getSelectionModel().selectedItemProperty().addListener((ov, value, newValue) -> {

				logger.debug("Buscando si existe " + directoryObject + newValue);

				File f = new File(directoryObject + newValue);

				if (f.exists() && f.isFile()) {
					chkUpdateData.setSelected(false);
					chkUpdateData.setDisable(false);
					long lastModified = f.lastModified();
					DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
					LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastModified),
							ZoneId.systemDefault());
					lblDateUpdate.setText(localDateTime.format(dtf));
					isFileCacheExists = false;
				} else {
					chkUpdateData.setSelected(true);
					chkUpdateData.setDisable(true);
					lblDateUpdate.setText(controller.getResourceBundle().getString("label.never"));
					isFileCacheExists = true;
				}
			});

		} catch (Exception e) {
			logger.error("Error al cargar los cursos", e);
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
			lblNoSelect.setText(controller.getResourceBundle().getString("error.nocourse"));
			return;
		}

		logger.info(" Curso seleccionado: " + selectedCourse.getFullName());

		if (chkUpdateData.isSelected()) {
			if (!isFileCacheExists) {
				loadData(controller.getPassword());
			} else {
				BBDD copia = new BBDD(controller.getDefaultBBDD());
				controller.setBBDD(copia);
				controller.setActualCourse(selectedCourse);
				isBBDDLoaded = true;
			}
			downloadData();
		} else {
			loadData(controller.getPassword());
			loadNextWindow();
		}

	}

	private void saveData() {

		if (!isBBDDLoaded) {
			return;
		}

		File f = new File(directoryObject);
		if (!f.isDirectory()) {
			logger.info("No existe el directorio, se va a crear: {}", directoryObject);
			f.mkdirs();
		}
		logger.info("Guardando los datos encriptados en: {}", f.getAbsolutePath());
		Encryption.encrypt(controller.getPassword(),
				directoryObject + listCourses.selectionModelProperty().getValue().getSelectedItem(),
				controller.getBBDD());

	}

	private void loadData(String password) {

		BBDD BBDD = (BBDD) Encryption.decrypt(password,
				directoryObject + listCourses.selectionModelProperty().getValue().getSelectedItem());
		if (BBDD != null) {

			controller.setBBDD(BBDD);
			isBBDDLoaded = true;
		} else {

			previusPasswordWindow();

		}

	}

	private void previusPasswordWindow() {
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle(controller.getResourceBundle().getString("title.passwordChanged"));
		dialog.setHeaderText(
				controller.getResourceBundle().getString("header.passwordChangedMessage") + "\n"
						+ controller.getResourceBundle().getString("header.passwordDateTime")
						+ lblDateUpdate.getText());

		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

		PasswordField pwd = new PasswordField();
		HBox content = new HBox();
		content.setAlignment(Pos.CENTER);
		content.setSpacing(10);
		content.getChildren().addAll(new Label(controller.getResourceBundle().getString("label.oldPassword")), pwd);
		dialog.getDialogPane().setContent(content);

		// desabilitamos el boton hasta que no escriba texto
		Node accept = dialog.getDialogPane().lookupButton(ButtonType.OK);
		accept.setDisable(true);

		pwd.textProperty().addListener((observable, oldValue, newValue) -> {
			accept.setDisable(newValue.trim().isEmpty());
		});
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
			System.out.println("No se ha cargado BBDD");
			return;
		}

		btnEntrar.setVisible(false);
		lblProgress.setVisible(true);
		progressBar.setVisible(true);
		Task<Void> task = getUserDataWorker();
		lblProgress.textProperty().bind(task.messageProperty());
		task.setOnSucceeded(v -> loadNextWindow());
		task.setOnFailed(e -> errorWindow(task.getMessage()));

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
					controller.getResourceBundle());
			controller.getStage().close();
			controller.setStage(new Stage());
			Parent root = loader.load();
			Scene scene = new Scene(root);
			controller.getStage().setScene(scene);
			controller.getStage().getIcons().add(new Image("/img/logo_min.png"));
			controller.getStage().setTitle("UBUGrades");
			controller.getStage().setResizable(true);
			controller.getStage().setMinHeight(600);
			controller.getStage().setMinWidth(800);
			controller.getStage().setMaximized(true);
			controller.getStage().show();
			lblNoSelect.setText("");
		} catch (IOException e) {

			logger.info("No se ha podido cargar la ventana Main.fxml: {}", e);
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
					logger.info("Cargando datos del curso: " + controller.getActualCourse().getFullName());
					// Establecemos los usuarios matriculados
					updateMessage(controller.getResourceBundle().getString("label.loadingstudents"));
					CreatorUBUGradesController.createEnrolledUsers(controller.getActualCourse().getId());
					CreatorUBUGradesController.createModules(controller.getActualCourse().getId());
					updateMessage(controller.getResourceBundle().getString("label.loadingqualifier"));
					// Establecemos calificador del curso
					CreatorGradeItems creatorGradeItems = new CreatorGradeItems(
							new Locale(controller.getUser().getLang()));
					creatorGradeItems.createGradeItems(controller.getActualCourse().getId());

					updateMessage(controller.getResourceBundle().getString("label.updatinglog"));
					if (isFileCacheExists) {
						Logs logs = LogCreator.createCourseLog();
						controller.getActualCourse().setLogs(logs);

					} else {
						Logs logs = controller.getActualCourse().getLogs();
						LogCreator.updateCourseLog(logs);

					}
					updateMessage(controller.getResourceBundle().getString("label.loadingstats"));
					// Establecemos las estadisticas
					controller.createStats();

					updateMessage("Guardando en local");
					saveData();
					logger.debug(controller.getBBDD().toString());
				} catch (Exception e) {
					logger.error("Error al cargar los datos de los alumnos: {}", e);
					updateMessage("Se produjo un error inesperado al cargar los datos.\n" + e.getLocalizedMessage());
					throw e;
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

		alert.setTitle("UbuGrades");
		alert.setHeaderText("Error");
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(controller.getStage());
		alert.getDialogPane().setContentText(mensaje);

		ButtonType buttonSalir = new ButtonType(controller.getResourceBundle().getString("label.close"));
		alert.getButtonTypes().setAll(buttonSalir);

		alert.showAndWait();
	}

}
