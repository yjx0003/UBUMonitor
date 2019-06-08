package controllers.ubulogs.logtypes;

import java.util.List;

import model.LogLine;

/**
 * The user with id '' created the chapter with id '' for the book with course module id ''.
 * The user with id '' updated the chapter with id '' for the book with course module id ''.
 * The user with id '' viewed the chapter with id '' for the book with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserChapterCmid extends ReferencesLog {

	
	/**
	 * Instacia única de la clase UserChapterCmid.
	 */
	private static UserChapterCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserChapterCmid() {
	}

	/**
	 * Devuelve la instancia única de UserChapterCmid.
	 * @return instancia singleton
	 */
	public static UserChapterCmid getInstance() {
		if (instance == null) {
			instance = new UserChapterCmid();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//chapter id
		setCourseModuleById(log, ids.get(2));
	}

}
