package es.ubu.lsi.ubumonitor.clustering.controller.collector;

import java.util.List;

import es.ubu.lsi.ubumonitor.clustering.data.UserData;

/**
 * Clase base de los colectores de datos.
 * 
 * @author Xing Long Ji
 *
 */
public abstract class DataCollector {

	private String type;

	/**
	 * Constructor.
	 * 
	 * @param type tipos de datos que recoge
	 */
	protected DataCollector(String type) {
		this.type = type;
	}

	/**
	 * Añade los datos que recoge a los usuarios. Ademas añade los datos
	 * normalizado.
	 * 
	 * @param users lista de usuarios
	 */
	public abstract void collect(List<UserData> users);

	/**
	 * Devulve el tipo de datos que recoge.
	 * 
	 * @return tipo de datos
	 */
	public String getType() {
		return type;
	}
}
