package controllers;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;
import org.controlsfx.property.editor.PropertyEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.configuration.CheckComboBoxPropertyEditor;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
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
		propertySheet.setPropertyEditorFactory(new Callback<PropertySheet.Item, PropertyEditor<?>>() {
			@Override
			public PropertyEditor<?> call(PropertySheet.Item item) {
				if (item.getValue() instanceof ObservableList<?>) {
					Class<?> type = item.getType();
					if (type == Role.class) {
						return new CheckComboBoxPropertyEditor<>(item,
								Controller.getInstance().getActualCourse().getRoles());
					} else if (type == Group.class) {
						return new CheckComboBoxPropertyEditor<>(item,
								Controller.getInstance().getActualCourse().getGroups());
					} else if (type == LastActivity.class) {
						return new CheckComboBoxPropertyEditor<>(item, LastActivityFactory.getAllLastActivity(),
								mainController.getActivityConverter());
					}

				}

				return DEFAUL_PROPERTY_EDITOR_FACTORY.call(item);
			}
		});

	}

	public void onClose() {
		saveConfiguration();
		applyConfiguration();

	}

	public void saveConfiguration() {
		try {
			Path path = Controller.getInstance().getConfiguration(Controller.getInstance().getActualCourse());
			path.toFile().getParentFile().mkdirs();
			Files.write(path,
					Controller.getInstance().getMainConfiguration().toJson().getBytes(StandardCharsets.UTF_8));
			mainController.getJavaConnector().updateChart();
		} catch (IOException e) {
			LOGGER.error("Error al guardar el fichero de configuración", e);
			UtilMethods.errorWindow(stage, I18n.get("error.saveconfiguration"));
		}
	}

	public void applyConfiguration() {
		mainController.getJavaConnector().updateButtons();
		mainController.getJavaConnector().updateChart();
	}

	public void restoreConfiguration(ActionEvent event) {
		Controller.getInstance().getMainConfiguration().setDefaultValues();
		stage.close();
		mainController.changeConfiguration(event);
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
