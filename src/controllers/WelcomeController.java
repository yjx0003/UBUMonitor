package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
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
	private ObservableList<String> list;
	@FXML
	private Label lblNoSelect;
	@FXML
	private ProgressBar progressBar;

	static final Logger logger = LoggerFactory.getLogger(WelcomeController.class);

	/**
	 * Función initialize. Muestra la lista de cursos del usuario introducido.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			lblUser.setText(UBUGrades.user.getFullName());
			logger.info("Cargando cursos...");
			ArrayList<String> nameCourses = new ArrayList<String>();
			for (int i = 0; i < UBUGrades.user.getCourses().size(); i++) {
				nameCourses.add(UBUGrades.user.getCourses().get(i).getFullName());
			}
			Collections.sort(nameCourses);

			list = FXCollections.observableArrayList(nameCourses);
			progressBar.visibleProperty().set(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		listCourses.setItems(list);
	}

	/**
	 * Botón entrar, accede a la siguiente ventana
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void enterCourse(ActionEvent event) throws Exception {
		UBUGrades.init.getScene().setCursor(Cursor.WAIT);

		// Guardamos en una variable el curso seleccionado por el usuario
		String selectedCourse = listCourses.getSelectionModel().getSelectedItem();
		UBUGrades.session.setActualCourse(Course.getCourseByString(selectedCourse));
		logger.info(" Curso seleccionado: " + UBUGrades.session.getActualCourse().getFullName());
		
		progressBar.setProgress(0.0);
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				logger.info("Cargando datos del curso: " + UBUGrades.session.getActualCourse().getFullName());
				updateProgress(1, 10);
				// Establecemos los usuarios matriculados
				CourseWS.setEnrolledUsers(UBUGrades.session.getToken(), UBUGrades.session.getActualCourse());
				updateProgress(6, 10);
				// Establecemos calificador del curso
				CourseWS.setGradeReportLines(UBUGrades.session.getToken(),
						UBUGrades.session.getActualCourse().getEnrolledUsers().get(0).getId(),
						UBUGrades.session.getActualCourse());
				updateProgress(10, 10);
				Thread.sleep(50);
				updateMessage("end");
				return null;
			}
		};
		progressBar.progressProperty().bind(task.progressProperty());
		progressBar.visibleProperty().set(true);
		task.messageProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
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
					} catch (Exception e) {
						lblNoSelect.setText("Debe seleccionar un curso");
						logger.info("Debe seleccionar un curso");
					}
				}
			}
		});
		final Thread thread = new Thread(task, "task-thread");
		thread.setDaemon(true);
		thread.start();
	}

}
