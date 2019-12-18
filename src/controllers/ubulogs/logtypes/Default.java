package controllers.ubulogs.logtypes;

import java.util.List;

import model.LogLine;
/**
 * 
 * Clase por defecto para los pares componente y evento que no tenga asignado una clase para manejar.
 * @author Yi Peng Ji
 *
 */
public class Default extends ReferencesLog {

		
	/**
	 * Instacia única de la clase Default.
	 */
	private static Default instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private Default() {
	}

	/**
	 * Devuelve la instancia única de Default.
	 * @return instancia singleton
	 */
	public static Default getInstance() {
		if (instance == null) {
			instance = new Default();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		// do nothing

	}

}
