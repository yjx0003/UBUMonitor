package controllers.configuration;

import java.util.Collection;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.editor.AbstractPropertyEditor;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

/**
 * Copy from https://stackoverflow.com/a/36253763
 * 
 * @author José Pereda
 *
 * @param <T>
 */
public class CheckComboBoxPropertyEditor<T> extends AbstractPropertyEditor<ObservableList<T>, CheckComboBox<T>> {

	private ListProperty<T> list;

	public CheckComboBoxPropertyEditor(Item property, Collection<T> items) {
		super(property, new CheckComboBox<T>());
		getEditor().getItems().addAll(items);
	
	}
	
	public CheckComboBoxPropertyEditor(Item property, Collection<T> items, StringConverter<T> stringConverter) {
		this(property,items);
		getEditor().setConverter(stringConverter);
	
	}

	@Override
	public void setValue(ObservableList<T> checked) {
		checked.forEach(getEditor().getCheckModel()::check);

	}

	@Override
	protected ObservableValue<ObservableList<T>> getObservableValue() {
		if (list == null) {
			list = new SimpleListProperty<>(getEditor().getCheckModel().getCheckedItems());
		}
		return list;
	}

}
