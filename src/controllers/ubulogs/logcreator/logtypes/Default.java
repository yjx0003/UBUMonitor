package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.LogLine;

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
		logger.info("No se ha añadido un manejador para referenciar los atributos restantes de :" + log);
		// do nothing

	}

}
