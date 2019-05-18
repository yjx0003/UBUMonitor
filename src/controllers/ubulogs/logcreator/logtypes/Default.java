package controllers.ubulogs.logcreator.logtypes;

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

	static final Logger logger = LoggerFactory.getLogger(Default.class);
	/**
	 * static Singleton instance.
	 */
	private static Default instance;

	/**
	 * Private constructor for singleton.
	 */
	private Default() {
	}

	/**
	 * Return a singleton instance of DoNothing.
	 */
	public static Default getInstance() {
		if (instance == null) {
			instance = new Default();
		}
		return instance;
	}

	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		logger.warn("No se ha añadido un manejador para referenciar los atributos restantes de :" + log);
		// do nothing

	}

}
