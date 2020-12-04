package es.ubu.lsi.ubumonitor.controllers.configuration;

import java.util.Collection;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.editor.AbstractPropertyEditor;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

/**
 * Copy from https://stackoverflow.com/a/36253763.
 * Custom property editor for {@link CheckComboBox} in the {@link PropertySheet} configuration.
 * @author José Pereda
 *
 * @param <T> type of object stored in the check combo box
 * 
 */
public class CheckComboBoxPropertyEditor<T> extends AbstractPropertyEditor<ObservableList<T>, CheckComboBox<T>> {

	private ListProperty<T> list;

	/**
	 * Constructs a {@link CheckComboBox} editor for the {@link PropertySheet}
	 * @param property property item in the Property Sheet
	 * @param items available object of the collection inside the {@link CheckComboBox}
	 */
	public CheckComboBoxPropertyEditor(Item property, Collection<T> items) {
		super(property, new CheckComboBox<T>());
		CheckComboBox<T> checkComboBox = getEditor();
		checkComboBox.setShowCheckedCount(true);
		checkComboBox.setTitle(property.getName());
		checkComboBox.getItems()
				.addAll(items);

	}

	/**
	 *  Constructs a {@link CheckComboBox} editor for the {@link PropertySheet} and set {@link StringConverter}} for CheckComboBox items.
	 * @param property property property item in the Property Sheet
	 * @param items items available object of the collection inside the {@link CheckComboBox}
	 * @param stringConverter change texts for items in the CheckComboBox items
	 */
	public CheckComboBoxPropertyEditor(Item property, Collection<T> items, StringConverter<T> stringConverter) {
		this(property, items);
		getEditor().setConverter(stringConverter);

	}


	@Override
	public void setValue(ObservableList<T> checked) {
		checked.forEach(getEditor().getCheckModel()::check);

	}

	@Override
	protected ObservableValue<ObservableList<T>> getObservableValue() {
		if (list == null) {
			list = new SimpleListProperty<>(getEditor().getCheckModel()
					.getCheckedItems());
		}
		return list;
	}

}
