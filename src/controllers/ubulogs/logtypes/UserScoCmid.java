package controllers.ubulogs.logtypes;

import java.util.List;

import model.LogLine;

/**
 * The user with id '' launched the sco with id '' for the scorm with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserScoCmid extends ReferencesLog {

	/**
	 * Instacia única de la clase UserScoCmid.
	 */
	private static UserScoCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserScoCmid() {
	}

	/**
	 * Devuelve la instancia única de UserScoCmid.
	 * @return instancia singleton
	 */
	public static UserScoCmid getInstance() {
		if (instance == null) {
			instance = new UserScoCmid();
		}
		return instance;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		// TODO Auto-generated method stub
		
	}

}
