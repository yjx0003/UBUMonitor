package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;
/**
 * The user with id '' has created the glossary entry with id '' for the glossary activity with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserGlossaryCmid extends ReferencesLog {

	
	/**
	 * static Singleton instance.
	 */
	private static UserGlossaryCmid instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserGlossaryCmid() {
	}

	/**
	 * Return a singleton instance of UserGlossaryCmid.
	 */
	public static UserGlossaryCmid getInstance() {
		if (instance == null) {
			instance = new UserGlossaryCmid();
		}
		return instance;
	}
	
	
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//glossary entry id
		setCourseModuleById(log, ids.get(2));
	}

}
