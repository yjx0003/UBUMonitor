package controllers.ubulogs.logtypes;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.LogLine;
/**
 * 
 * Clase por defecto para los pares componente y evento que no tenga asignado una clase para manejar.
 * @author Yi Peng Ji
 *
 */
public class Default extends ReferencesLog {

	private static final Logger LOGGER = LoggerFactory.getLogger(Default.class);
		
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
		LOGGER.warn("No se ha añadido un manejador para el log {}" , log.getComponentEvent());
		// do nothing

	}

}
