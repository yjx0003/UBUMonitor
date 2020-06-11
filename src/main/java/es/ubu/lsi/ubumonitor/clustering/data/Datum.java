package es.ubu.lsi.ubumonitor.clustering.data;

import java.util.Objects;

/**
 * Representa un dato concreto.
 * 
 * @author Xing Long Ji
 *
 */
public class Datum {

	private String type;
	private String item;
	private String iconFile;
	private Number value;

	/**
	 * Constructor.
	 * 
	 * @param type     tipo de dato
	 * @param item     elemento de Moodle
	 * @param iconFile fichero del icono
	 * @param value    valor
	 */
	public Datum(String type, String item, String iconFile, Number value) {
		this.type = type;
		this.item = item;
		this.iconFile = iconFile == null ? "manual" : iconFile;
		this.value = value;
	}

	/**
	 * Devuelve el tipo.
	 * 
	 * @return el tipo
	 */
	public String getType() {
		return type;
	}

	/**
	 * Devuelve el elemento de Moodle.
	 * 
	 * @return el elemento
	 */
	public String getItem() {
		return item;
	}

	/**
	 * Devuelve el nombre del fichero del icono.
	 * 
	 * @return el nombre del fichero del icono
	 */
	public String getIconFile() {
		return iconFile;
	}

	/**
	 * Devuelve el valor del dato.
	 * 
	 * @return el valor
	 */
	public Number getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(item, type, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Datum))
			return false;
		Datum other = (Datum) obj;
		return Objects.equals(item, other.item) && Objects.equals(type, other.type)
				&& Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return "Datum [type=" + type + ", item=" + item + ", value=" + value + "]";
	}
}
