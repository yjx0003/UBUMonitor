package controllers.configuration;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;
import org.controlsfx.property.editor.PropertyEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.Controller;
import controllers.I18n;
import controllers.MainController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Group;
import model.LastActivity;
import model.LastActivityFactory;
import model.Role;
import util.UtilMethods;

public class ConfigurationController implements Initializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationController.class);

	private MainController mainController;

	private Stage stage;

	@FXML
	PropertySheet propertySheet;

	private static final Callback<Item, PropertyEditor<?>> DEFAUL_PROPERTY_EDITOR_FACTORY = new DefaultPropertyEditorFactory();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		propertySheet.getItems().addAll(Controller.getInstance().getMainConfiguration().getProperties());
		propertySheet.setPropertyEditorFactory(item -> {

			if (item.getValue() instanceof ObservableList<?>) {
				Class<?> type = item.getType();
				if (type == Role.class) {
					return new CheckComboBoxPropertyEditor<>(item,
							Controller.getInstance().getActualCourse().getRoles());
				}
				if (type == Group.class) {
					return new CheckComboBoxPropertyEditor<>(item,
							Controller.getInstance().getActualCourse().getGroups());
				}
				if (type == LastActivity.class) {
					return new CheckComboBoxPropertyEditor<>(item, LastActivityFactory.getAllLastActivity(),
							mainController.getActivityConverter());
				}

			}

			return DEFAUL_PROPERTY_EDITOR_FACTORY.call(item);
		});

	}

	public void onClose() {
		Controller controller = Controller.getInstance();
		saveConfiguration(controller.getMainConfiguration(), controller.getConfiguration(controller.getActualCourse()),
				stage);
		applyConfiguration();

	}

	public static void saveConfiguration(MainConfiguration mainConfiguration, Path path, Stage stage) {
		try {
			path.toFile().getParentFile().mkdirs();
			Files.write(path, mainConfiguration.toJson().getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			LOGGER.error("Error al guardar el fichero de configuración", e);
			UtilMethods.errorWindow(stage, I18n.get("error.saveconfiguration"));
		}
	}

	public void applyConfiguration() {
		mainController.getJavaConnector().updateChart();
	}

	public void restoreConfiguration() {
		Controller.getInstance().getMainConfiguration().setDefaultValues();
		propertySheet.getItems().setAll(Controller.getInstance().getMainConfiguration().getProperties());

	}

	public void restoreSavedConfiguration() {
		Controller controller = Controller.getInstance();
		loadConfiguration(controller.getMainConfiguration(), controller.getConfiguration(controller.getActualCourse()),
				stage);

	}

	public static void loadConfiguration(MainConfiguration mainConfiguration, Path path, Stage stage) {
		if (path.toFile().exists()) {
			try {
				mainConfiguration.fromJson(new String(Files.readAllBytes(path), StandardCharsets.UTF_8));

			} catch (IOException e) {
				UtilMethods.errorWindow(stage, I18n.get("error.chargeconfiguration"));
			} catch (RuntimeException e) {
				UtilMethods.errorWindow(stage, I18n.get("error.filenotvalid"));
			}

		}
	}

	/**
	 * @return the mainController
	 */
	public MainController getMainController() {
		return mainController;
	}

	/**
	 * @param mainController the mainController to set
	 */
	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

	/**
	 * @return the stage
	 */
	public Stage getStage() {
		return stage;
	}

	/**
	 * @param stage the stage to set
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

}
