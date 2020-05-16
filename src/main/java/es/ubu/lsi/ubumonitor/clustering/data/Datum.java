package es.ubu.lsi.ubumonitor.clustering.data;

import java.util.Objects;

public class Datum {

	private String type;
	private String item;
	private String iconFile;
	private Number value;

	public Datum(String type, String item, String iconFile, Number value) {
		this.type = type;
		this.item = item;
		this.iconFile = iconFile == null ? "manual" : iconFile;
		this.value = value;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the item
	 */
	public String getItem() {
		return item;
	}

	/**
	 * @return the iconFile
	 */
	public String getIconFile() {
		return iconFile;
	}

	/**
	 * @return the value
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