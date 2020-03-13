package es.ubu.lsi.ubumonitor.clustering.util;

import java.util.Optional;

import org.controlsfx.control.PropertySheet;

import es.ubu.lsi.ubumonitor.controllers.I18n;
import javafx.beans.value.ObservableValue;

public class SimplePropertySheetItem implements PropertySheet.Item {

	private String name;
	private Object value;
	private String description;

	public SimplePropertySheetItem(String name) {
		this(name, "");
	}

	public SimplePropertySheetItem(String name, String value) {
		this(name, value, "");
	}

	public SimplePropertySheetItem(String name, Object value, String description) {
		this.name = name;
		this.value = value;
		this.description = description;
	}

	@Override
	public Class<?> getType() {
		return value.getClass();
	}

	@Override
	public String getCategory() {
		return null;
	}

	@Override
	public String getName() {
		return I18n.get(name);
	}

	@Override
	public String getDescription() {
		return I18n.get(description);
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public Optional<ObservableValue<? extends Object>> getObservableValue() {
		return Optional.empty();
	}

	@Override
	public String toString() {
		return name + "=" + value;
	}

}
