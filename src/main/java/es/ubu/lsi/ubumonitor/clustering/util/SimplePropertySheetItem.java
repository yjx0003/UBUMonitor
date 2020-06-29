package es.ubu.lsi.ubumonitor.clustering.util;

import java.util.Optional;

import org.controlsfx.control.PropertySheet;

import javafx.beans.value.ObservableValue;

/**
 * Item basico para el PropertySheet.
 * 
 * @author Xing Long Ji
 *
 */
public class SimplePropertySheetItem implements PropertySheet.Item {

	private String name;
	private Object value;
	private String description;

	/**
	 * Constructor con solo el nombre.
	 * 
	 * @param name nombre o clave
	 */
	public SimplePropertySheetItem(String name) {
		this(name, "");
	}

	/**
	 * Constructor con el nombre y el valor.
	 * 
	 * @param name  nombre o clave
	 * @param value valor
	 */
	public SimplePropertySheetItem(String name, Object value) {
		this(name, value, "");
	}

	/**
	 * Constructor con el nombre, el valor y una descripción.
	 * 
	 * @param name        nombre o clave
	 * @param value       valor
	 * @param description descripción o tooltip
	 */
	public SimplePropertySheetItem(String name, Object value, String description) {
		this.name = name;
		this.value = value;
		this.description = description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> getType() {
		return value.getClass();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCategory() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValue() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<ObservableValue<? extends Object>> getObservableValue() {
		return Optional.empty();
	}

	@Override
	public String toString() {
		return name + "=" + value;
	}

}
