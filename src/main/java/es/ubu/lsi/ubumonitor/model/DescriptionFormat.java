package es.ubu.lsi.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeración para los distintos tipos de enumeración
 * @author Yi Peng Ji
 *
 */
public enum DescriptionFormat {
	MOODLE(0),
	HTML(1),
	PLAIN(2),
	MARKDOWN(4);

	private int number;
	private static Map<Integer, DescriptionFormat> map = new HashMap<>();
	static {
		for (DescriptionFormat df : DescriptionFormat.values()) {
			map.put(df.number, df);
		}
	}


	private DescriptionFormat(int number) {
		this.number = number;
	}

	/**
	 * Devuelve el valor de la enumeración a partir del número que devuelve la funcion de moodle.
	 * @param number el número
	 * @return el formato de descripcion
	 */
	public static DescriptionFormat get(int number) {
		return map.get(number);
	}

}