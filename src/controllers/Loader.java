package controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Clase Loader. Inicializa la ventana de login
 * 
 * @author Félix Nogal Santamaría
 * @author Yi Peng
 * @version 1.0
 *
 */
public class Loader extends Application {

	private static final Logger LOGGER = LoggerFactory.getLogger(Loader.class);
	private Controller controller = Controller.getInstance();

	@Override
	public void start(Stage primaryStage) {

		try {
			controller.initialize();
			LOGGER.info("[Bienvenido a {}]", AppInfo.APPLICATION_NAME);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"), I18n.getResourceBundle());
			Parent root = loader.load();
			Scene scene = new Scene(root);
			Stage stage = primaryStage;
			stage.setScene(scene);
			stage.getIcons().add(new Image("/img/logo_min.png"));
			stage.setTitle(AppInfo.APPLICATION_NAME);
			stage.setResizable(false);
			stage.show();
			controller.setStage(stage);
		} catch (Exception e) {
			LOGGER.error("Error al iniciar controller: {}", e);
		}
	}

	public static void initialize() {
		launch();
	}
}