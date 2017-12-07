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

	static final Logger logger = LoggerFactory.getLogger(WelcomeController.class);

	/**
	 * Función initialize. Muestra la lista de cursos del usuario introducido.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			lblUser.setText(UBUGrades.user.getFullName());
			logger.info("Cargando cursos...");
			ArrayList<String> nameCourses = new ArrayList<>();
			for (int i = 0; i < UBUGrades.user.getCourses().size(); i++) {
				nameCourses.add(UBUGrades.user.getCourses().get(i).getFullName());
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
	 * @throws Exception
	 */
	public void enterCourse(ActionEvent event) {
		UBUGrades.init.getScene().setCursor(Cursor.WAIT);

		// Guardamos en una variable el curso seleccionado por el usuario
		String selectedCourse = listCourses.getSelectionModel().getSelectedItem();
		if(selectedCourse == null) {
			lblNoSelect.setText("Debe seleccionar un curso");
			return; 
		}
		lblNoSelect.setText("");
		UBUGrades.session.setActualCourse(Course.getCourseByString(selectedCourse));
		logger.info(" Curso seleccionado: " + UBUGrades.session.getActualCourse().getFullName());
		
		btnEntrar.setVisible(false);
		lblProgress.setVisible(true);
		progressBar.setProgress(0.0);
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() {
				try {
					logger.info("Cargando datos del curso: " + UBUGrades.session.getActualCourse().getFullName());
					// Establecemos los usuarios matriculados
					CourseWS.setEnrolledUsers(UBUGrades.session.getToken(), UBUGrades.session.getActualCourse());
					int enroledUsersCount = UBUGrades.session.getActualCourse().getEnrolledUsersCount()+8;
					int done = 0;
					updateProgress(done, enroledUsersCount);
					
					updateMessage("update_Alumnos cargados: " + done + " de " + (enroledUsersCount-8));
					for(EnrolledUser user: UBUGrades.session.getActualCourse().getEnrolledUsers()) {
						// Obtenemos todas las lineas de calificación del usuario
						logger.info("Cargando los datos de: " + user.getFullName() + "...");
						user.setAllGradeReportLines(CourseWS.getUserGradeReportLines(UBUGrades.session.getToken(), user.getId(),
								UBUGrades.session.getActualCourse().getId()));
						updateProgress(done++, enroledUsersCount);
						logger.info("Datos cargados.");
						updateMessage("update_Alumnos cargados: " + done + " de " + (enroledUsersCount-8));
					}
					
					updateMessage("update_Generando el calificador del curso...");
					// Establecemos calificador del curso
					CourseWS.setGradeReportLines(UBUGrades.session.getToken(),
							UBUGrades.session.getActualCourse().getEnrolledUsers().get(0).getId(),
							UBUGrades.session.getActualCourse());
					done += 4;
					updateProgress(done, enroledUsersCount);
					
					updateMessage("update_Generando estadisticas...");
					
					//Establecemos las estadisticas
					Stats.getStats();
					
					updateProgress(done+4L, enroledUsersCount);
					
					Thread.sleep(50);
					//Indica que se ha terminado el trabajo
					updateMessage("end");
				} catch (Exception e) {
					updateMessage(e.getMessage());
				}
				return null;
			}
		};
		
		progressBar.progressProperty().bind(task.progressProperty());
		progressBar.visibleProperty().set(true);
		task.messageProperty().addListener((ChangeListener<String>)(observable, oldValue, newValue) -> {
			if(newValue.equals("end")) {
				// Cargamos la siguiente ventana
				try {
					// Accedemos a la siguiente ventana
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Main.fxml"));
					
					UBUGrades.stage.close();
					UBUGrades.stage = new Stage();
					Parent root = loader.load();
					Scene scene = new Scene(root);
					UBUGrades.stage.setScene(scene);
					UBUGrades.stage.getIcons().add(new Image("/img/logo_min.png"));
					UBUGrades.stage.setTitle("UBUGrades");
					UBUGrades.stage.setResizable(true);
					UBUGrades.stage.setMinHeight(600);
					UBUGrades.stage.setMinWidth(800);
					UBUGrades.stage.setMaximized(true);
					UBUGrades.stage.show();
					UBUGrades.init.getScene().setCursor(Cursor.DEFAULT);
					lblNoSelect.setText("");
				} catch (IOException e) {
					logger.info("No se ha podido cargar la ventana principal: {}", e);
					//errorWindow("No se ha podido cargar el curso.");
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
	 * Muestra una ventana de error.
	 * 
	 * @param mensaje
	 * 		El mensaje que se quiere mostrar.
	 */
	private static void errorWindow(String mensaje) {
		Alert alert = new Alert(AlertType.ERROR);
		
		alert.setTitle("UbuGrades");
		alert.setHeaderText("Error");
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(UBUGrades.stage);
		alert.getDialogPane().setContentText(mensaje);
		
		ButtonType buttonSalir = new ButtonType("Cerrar UBUGrades");
		alert.getButtonTypes().setAll(buttonSalir);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == buttonSalir)
			UBUGrades.stage.close();
	}  

}
