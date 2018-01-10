package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.value.ChangeListener;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;
import webservice.CourseWS;

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
	
	private UBUGrades ubuGrades = UBUGrades.getInstance();
	
	@FXML
	private Label lblUser;
	@FXML
	private ListView<String> listCourses;
	@FXML
	private Label lblNoSelect;
    @FXML
    private Button btnEntrar;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Label lblProgress;

	/**
	 * Función initialize. Muestra la lista de cursos del usuario introducido.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			lblUser.setText(ubuGrades.getUser().getFullName());
			logger.info("Cargando cursos...");
			ArrayList<String> nameCourses = new ArrayList<>();
			for (int i = 0; i < ubuGrades.getUser().getCourses().size(); i++) {
				nameCourses.add(ubuGrades.getUser().getCourses().get(i).getFullName());
			}
			Collections.sort(nameCourses);
			ObservableList<String> list = FXCollections.observableArrayList(nameCourses);
			progressBar.visibleProperty().set(false);
			listCourses.setItems(list);
		} catch (Exception e) {
			logger.error("Error al cargar los cursos", e);
		}
	}

	/**
	 * Botón entrar, accede a la siguiente ventana
	 * 
	 * @param event
	 * 		El evento.
	 */
	public void enterCourse(ActionEvent event) {

		// Guardamos en una variable el curso seleccionado por el usuario
		String selectedCourse = listCourses.getSelectionModel().getSelectedItem();
		if(selectedCourse == null) {
			lblNoSelect.setText(ubuGrades.getResourceBundle().getString("error.nocourse"));
			return; 
		}
		lblNoSelect.setText("");
		ubuGrades.getSession().setActualCourse(Course.getCourseByString(ubuGrades.getUser().getCourses(), selectedCourse));
		logger.info(" Curso seleccionado: " + ubuGrades.getSession().getActualCourse().getFullName());
		btnEntrar.setVisible(false);
		lblProgress.setVisible(true);
		progressBar.setProgress(0.0);
		Task<Void> task = getUserDataWorker();
		progressBar.progressProperty().bind(task.progressProperty());
		progressBar.visibleProperty().set(true);
		task.messageProperty().addListener((ChangeListener<String>)(observable, oldValue, newValue) -> {
			if(newValue.equals("end")) {
				// Cargamos la siguiente ventana
				try {
					// Accedemos a la siguiente ventana
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Main.fxml"), ubuGrades.getResourceBundle());
					ubuGrades.getStage().close();
					ubuGrades.setStage(new Stage());
					Parent root = loader.load();
					Scene scene = new Scene(root);
					ubuGrades.getStage().setScene(scene);
					ubuGrades.getStage().getIcons().add(new Image("/img/logo_min.png"));
					ubuGrades.getStage().setTitle("UBUGrades");
					ubuGrades.getStage().setResizable(true);
					ubuGrades.getStage().setMinHeight(600);
					ubuGrades.getStage().setMinWidth(800);
					ubuGrades.getStage().setMaximized(true);
					ubuGrades.getStage().show();
					lblNoSelect.setText("");
				} catch (IOException e) {
					logger.info("No se ha podido cargar la ventana Main.fxml: {}", e);
				}
			} else if (newValue.substring(0, 6).equals("update")){
				lblProgress.setText(newValue.substring(7));
			} else {
				errorWindow(newValue);
			}
		});
		Thread thread = new Thread(task, "datos");
		thread.start();
	}
	
	/**
	 * Realiza el proceso de carga de las notas de los alumnos, carga del arbol del calificador
	 * y generación de las estadisticas.
	 * 
	 * @return
	 * 		La tarea a realizar.
	 */
	private Task<Void> getUserDataWorker() {
		return new Task<Void>() {
			@Override
			protected Void call() {
				try {
					ubuGrades.getStage().getScene().setCursor(Cursor.WAIT);
					logger.info("Cargando datos del curso: " + ubuGrades.getSession().getActualCourse().getFullName());
					// Establecemos los usuarios matriculados
					CourseWS.setEnrolledUsers(ubuGrades.getHost(), 
							ubuGrades.getSession().getToken(), ubuGrades.getSession().getActualCourse());
					int enroledUsersCount = ubuGrades.getSession().getActualCourse().getEnrolledUsersCount()+8;
					int done = 0;
					updateProgress(done, enroledUsersCount);
					
					updateMessage("update_" + ubuGrades.getResourceBundle().getString("label.loadingqualifier"));
					// Establecemos calificador del curso
					CourseWS.setGradeReportLines(ubuGrades.getHost(), ubuGrades.getSession().getToken(),
							ubuGrades.getSession().getActualCourse().getEnrolledUsers().get(0).getId(),
							ubuGrades.getSession().getActualCourse());
					done += 4;
									
					updateProgress(done, enroledUsersCount);
					updateMessage("update_" + ubuGrades.getResourceBundle().getString("label.loadingstudents")
									+ (done-4) + " " + ubuGrades.getResourceBundle().getString("label.of") + " " + (enroledUsersCount-8));
					for(EnrolledUser user: ubuGrades.getSession().getActualCourse().getEnrolledUsers()) {
						// Obtenemos todas las lineas de calificación del usuario
						logger.info("Cargando los datos de: " + user.getFullName() + "...");
						user.setAllGradeReportLines(CourseWS.getUserGradeReportLines(ubuGrades.getHost(),
								ubuGrades.getSession().getToken(), user.getId(),ubuGrades.getSession().getActualCourse().getId()));
						updateProgress(done++, enroledUsersCount);
						logger.info("Datos cargados.");
						updateMessage("update_" + ubuGrades.getResourceBundle().getString("label.loadingstudents")
										+ (done-4) + " " + ubuGrades.getResourceBundle().getString("label.of") + " " + (enroledUsersCount-8));
					}
										
					updateMessage("update_" + ubuGrades.getResourceBundle().getString("label.loadingstats"));
					//Establecemos las estadisticas
					Stats.getStats(ubuGrades.getSession());
					updateProgress(done+4L, enroledUsersCount);
					
					Thread.sleep(50);
					//Indica que se ha terminado el trabajo
					updateMessage("end");
				} catch (Exception e) {
					logger.error("Error al cargar los datos de los alumnos: {}", e);
					updateMessage("Se produjo un error inesperado al cargar los datos.\n" + e.getLocalizedMessage());
				} finally {
					ubuGrades.getStage().getScene().setCursor(Cursor.DEFAULT);
				}
				return null;
			}
		};
	}
	
	/**
	 * Muestra una ventana de error.
	 * 
	 * @param mensaje
	 * 		El mensaje que se quiere mostrar.
	 */
	private void errorWindow(String mensaje) {
		Alert alert = new Alert(AlertType.ERROR);
		
		alert.setTitle("UbuGrades");
		alert.setHeaderText("Error");
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(ubuGrades.getStage());
		alert.getDialogPane().setContentText(mensaje);
		
		ButtonType buttonSalir = new ButtonType(ubuGrades.getResourceBundle().getString("label.close"));
		alert.getButtonTypes().setAll(buttonSalir);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == buttonSalir)
			ubuGrades.getStage().close();
	}  

}
