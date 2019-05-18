package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;
/**
 * 
 * Ignora los logs para añadir atributos adicionales
 * @author Yi Peng Ji
 *
 */
public class Ignore extends ReferencesLog{

	/**
	 * static Singleton instance.
	 */
	private static Ignore instance;

	/**
	 * Private constructor for singleton.
	 */
	private Ignore() {
	}

	/**
	 * Return a singleton instance of Ignore.
	 */
	public static Ignore getInstance() {
		if (instance == null) {
			instance = new Ignore();
		}
		return instance;
	}
	
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		// No hace nada		
	}

}
