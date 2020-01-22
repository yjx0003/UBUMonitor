package controllers;

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

import controllers.configuration.CheckComboBoxPropertyEditor;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.util.Callback;
import model.Group;
import model.LastActivity;
import model.LastActivityFactory;
import model.Role;

public class ConfigurationController implements Initializable {


	private MainController mainController;
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
