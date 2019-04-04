package controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.ubugrades.CreatorUBUGradesController;
import controllers.ubulogs.UBULogController;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
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
	
	

	/**
	 * Función initialize. Muestra la lista de cursos del usuario introducido.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		try {
			directoryObject = "./cache/" + controller.getUser().getFullName() + "/";
			lblUser.setText(controller.getUser().getFullName());
			logger.info("Cargando cursos...");

			ObservableList<Course> list = FXCollections.observableArrayList(controller.getUser().getCourses());
			list.sort(Comparator.comparing(Course::getFullName));
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
					isFileCacheExists=false;
				} else {
					chkUpdateData.setSelected(true);
					chkUpdateData.setDisable(true);
					lblDateUpdate.setText(controller.getResourceBundle().getString("label.never"));
					isFileCacheExists=true;
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
		controller.setActualCourse(selectedCourse);
		logger.info(" Curso seleccionado: " + controller.getActualCourse().getFullName());

		if (chkUpdateData.isSelected()) {
			if(!isFileCacheExists) {
				loadData();
			}
			
			downloadData();

		} else {

			loadData();
			loadNextWindow();
		}

	}

	private void saveData() {
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

	private void loadData() {
		BBDD BBDD = (BBDD) Encryption.decrypt(controller.getPassword(),
				directoryObject + listCourses.selectionModelProperty().getValue().getSelectedItem());
		if (BBDD != null) {
			controller.setBBDD(BBDD);
		}

	}

	private void downloadData() {
		btnEntrar.setVisible(false);
		lblProgress.setVisible(true);
		progressBar.setVisible(true);
		Task<Void> task = getUserDataWorker();
		lblProgress.textProperty().bind(task.messageProperty());
		task.setOnSucceeded(v -> loadNextWindow());

		Thread thread = new Thread(task, "datos");
		thread.start();

	}

	private void loadNextWindow() {
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
			protected Void call() {
				try {
					controller.getActualCourse().clear();
					logger.info("Cargando datos del curso: " + controller.getActualCourse().getFullName());
					// Establecemos los usuarios matriculados
					updateMessage(controller.getResourceBundle().getString("label.loadingstudents"));
					CreatorUBUGradesController.createEnrolledUsers(controller.getActualCourse().getId());
					CreatorUBUGradesController.createModules(controller.getActualCourse().getId());
					updateMessage(controller.getResourceBundle().getString("label.loadingqualifier"));
					// Establecemos calificador del curso
					CreatorUBUGradesController.createGradeItems(controller.getActualCourse().getId());

					updateMessage(controller.getResourceBundle().getString("label.loadingstats"));
					// Establecemos las estadisticas
					controller.createStats();
					
					updateMessage("Actualizando el log");
					if(isFileCacheExists) {
						Logs logs=UBULogController.getInstance().createCourseLog();
						controller.getActualCourse().setLogs(logs);
					}else {
						Logs logs=controller.getActualCourse().getLogs();
						UBULogController.getInstance().updateCourseLog(logs);
						
					}
					updateMessage("Guardando en local");
					saveData();
					logger.debug(controller.getActualCourse().getLogs().toString());
					logger.debug(controller.getBBDD().toString());
					
				} catch (Exception e) {
					logger.error("Error al cargar los datos de los alumnos: {}", e);
					updateMessage("Se produjo un error inesperado al cargar los datos.\n" + e.getLocalizedMessage());
				} finally {
					controller.getStage().getScene().setCursor(Cursor.DEFAULT);
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

		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == buttonSalir)
			controller.getStage().close();
	}

}
