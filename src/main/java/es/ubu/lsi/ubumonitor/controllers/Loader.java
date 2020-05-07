package es.ubu.lsi.ubumonitor.controllers;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.AppInfo;
import es.ubu.lsi.ubumonitor.Style;
import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

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
			Thread.setDefaultUncaughtExceptionHandler((t, e) -> LOGGER.error("Uncaughted error:", t, e));
			controller.initialize();

			LOGGER.info("[Bienvenido a {}]", AppInfo.APPLICATION_NAME_WITH_VERSION);
			
			hackTooltipStartTiming();
			
			primaryStage.getIcons()
					.add(new Image("/img/logo_min.png"));
			primaryStage.setTitle(AppInfo.APPLICATION_NAME_WITH_VERSION);
			primaryStage.setResizable(false);
			UtilMethods.changeScene(getClass().getResource("/view/Login.fxml"), primaryStage);
			Style.addStyle(ConfigHelper.getProperty("style"), primaryStage.getScene()
					.getStylesheets());
			controller.setStage(primaryStage);

		} catch (Exception e) {
			LOGGER.error("Error al iniciar controller: {}", e);
		}
	}

	@Override
	public void stop() {

		ConfigHelper.save();
	}
	
	/**
	 * https://stackoverflow.com/a/27739605
	 */
	private static void hackTooltipStartTiming() {
	    try {
	        Field fieldBehavior = Tooltip.class.getDeclaredField("BEHAVIOR");
	        fieldBehavior.setAccessible(true);
	        Object objBehavior = fieldBehavior.get(new Tooltip());

	        Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
	        fieldTimer.setAccessible(true);
	        Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

	        objTimer.getKeyFrames().clear();
	        objTimer.getKeyFrames().add(new KeyFrame(new Duration(250)));
	    } catch (Exception e) {
	       LOGGER.error("Cannot set tooltip delay", e);
	    }
	}
	

	public static void initialize() {
		launch();
	}
}