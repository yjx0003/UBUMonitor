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

/**
 * Clase Loader. Inicializa la ventana de login
 * 
 * @author Félix Nogal Santamaría
 * @version 1.0
 *
 */
public class Loader extends Application {
	static final Logger logger = LoggerFactory.getLogger(Loader.class);
	private UBUGrades ubuGrades = UBUGrades.getInstance();

	@Override
	public void start(Stage primaryStage) {
		// Si no existe el recurso de idioma especificado cargamos el Español
		try {
			ubuGrades.setResourceBundle(ResourceBundle.getBundle("messages/Messages",
					new Locale(Locale.getDefault().toString().toLowerCase())));
			logger.info("Cargando idoma del sistema: {}", ubuGrades.getResourceBundle().getLocale().toString());
		}catch (NullPointerException | MissingResourceException e) {
			logger.error("No se ha podido encontrar el recurso de idioma, cargando en_en: {}", e);
			ubuGrades.setResourceBundle(ResourceBundle.getBundle("messages/Messages", new Locale("en_en")));
		}
		
		try {
			logger.info("[Bienvenido a UBUGrades]");
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"), ubuGrades.getResourceBundle());
			Parent root = loader.load();
			Scene scene = new Scene(root);
			Stage stage = primaryStage;
			stage.setScene(scene);
			stage.getIcons().add(new Image("/img/logo_min.png"));
			stage.setTitle("UBUGrades");
			stage.setResizable(false);
			stage.show();
			ubuGrades.setStage(stage);
		} catch (Exception e) {
			logger.error("Error al iniciar UBUGrades: {}", e);
		}
	}

	// Main comando
	public static void main(String[] args) {
		launch(args);
	}
}