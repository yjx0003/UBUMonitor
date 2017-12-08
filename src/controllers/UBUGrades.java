package controllers;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.*;
import webservice.Session;

/**
 * Clase main. Inicializa la ventana de login
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class UBUGrades extends Application {
	public static String host = "";
	public static Stage stage;
	public static Stage init;
	public static Session session;
	public static MoodleUser user;
	public static ResourceBundle resourceBundle;
	
	static final Logger logger = LoggerFactory.getLogger(UBUGrades.class);

	@Override
	public void start(Stage primaryStage) {
		// Si no existe el recurso de idioma especificado cargamos el Español
		try {
			resourceBundle = ResourceBundle.getBundle("Messages");
		}catch (NullPointerException | MissingResourceException e) {
			logger.error("No se ha podido encontrar el recurso de idioma, cargando es_ES: {}", e);
			resourceBundle = ResourceBundle.getBundle("Messages", new Locale("es_ES"));
		}
		
		try {
			logger.info("[Bienvenido a UBUGrades]");
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"), resourceBundle);
			Parent root = loader.load();
			Scene scene = new Scene(root);
			init = primaryStage;
			init.setScene(scene);
			init.getIcons().add(new Image("/img/logo_min.png"));
			init.setTitle("UBUGrades");
			init.setResizable(false);
			init.show();
		} catch (Exception e) {
			logger.error("Error al iniciar UBUGrades");
			e.printStackTrace();
		}
	}

	// Main comando
	public static void main(String[] args) {
		launch(args);
	}
}