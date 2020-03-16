package es.ubu.lsi.ubumonitor.controllers.configuration;

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

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.SelectionUserController;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.model.LastActivity;
import es.ubu.lsi.ubumonitor.model.LastActivityFactory;
import es.ubu.lsi.ubumonitor.model.Role;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.util.Callback;

public class ConfigurationController implements Initializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationController.class);

	private MainController mainController;

	@FXML
	private PropertySheet propertySheet;

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
							SelectionUserController.getActivityConverter());
				}

			}

			return DEFAUL_PROPERTY_EDITOR_FACTORY.call(item);
		});

	}
	
	public void setOnClose() {
		propertySheet.getScene().getWindow().setOnHidden(e -> onClose());
	}

	public void onClose() {
		Controller controller = Controller.getInstance();
		saveConfiguration(controller.getMainConfiguration(), controller.getConfiguration(controller.getActualCourse()));
		applyConfiguration();

	}

	public static void saveConfiguration(MainConfiguration mainConfiguration, Path path) {
		try {
			path.toFile().getParentFile().mkdirs();
			Files.write(path, mainConfiguration.toJson().getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			LOGGER.error("Error al guardar el fichero de configuración", e);
			UtilMethods.errorWindow(I18n.get("error.saveconfiguration"), e);
		}
	}

	public void applyConfiguration() {
		mainController.applyConfiguration();
	}

	public void restoreConfiguration() {
		ButtonType option = UtilMethods.confirmationWindow(I18n.get("text.restoredefault"));
		if (option == ButtonType.OK) {
			Controller.getInstance().getMainConfiguration().setDefaultValues();
			propertySheet.getItems().setAll(Controller.getInstance().getMainConfiguration().getProperties());
		}

	}

	public void restoreSavedConfiguration() {
		Controller controller = Controller.getInstance();
		loadConfiguration(controller.getMainConfiguration(), controller.getConfiguration(controller.getActualCourse()));

	}

	public static void loadConfiguration(MainConfiguration mainConfiguration, Path path) {
		if (path.toFile().exists()) {
			try {
				mainConfiguration.fromJson(new String(Files.readAllBytes(path), StandardCharsets.UTF_8));

			} catch (IOException e) {
				UtilMethods.errorWindow(I18n.get("error.chargeconfiguration"), e);
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

}
