package es.ubu.lsi.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.model.LogLine;
/**
 * The user with id '' has created the glossary entry with id '' for the glossary activity with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserGlossaryCmid extends ReferencesLog {

	
	/**
	 * Instacia única de la clase UserGlossaryCmid.
	 */
	private static UserGlossaryCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserGlossaryCmid() {
	}

	/**
	 * Devuelve la instancia única de UserGlossaryCmid.
	 * @return instancia singleton
	 */
	public static UserGlossaryCmid getInstance() {
		if (instance == null) {
			instance = new UserGlossaryCmid();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//glossary entry id
		setCourseModuleById(log, ids.get(2));
	}

}
