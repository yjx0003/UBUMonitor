package es.ubu.lsi.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.model.LogLine;
/**
 * 
 * Ignora los logs para añadir atributos adicionales, usado para las descripciones que no aparecen ningun integer.
 * @author Yi Peng Ji
 *
 */
public class Ignore extends ReferencesLog{

	/**
	 * Instacia única de la clase Ignore.
	 */
	private static Ignore instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private Ignore() {
	}

	/**
	 * Devuelve la instancia única de Ignore.
	 * @return instancia singleton
	 */
	public static Ignore getInstance() {
		if (instance == null) {
			instance = new Ignore();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		// No hace nada		
	}

}
