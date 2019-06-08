package controllers.ubulogs.logtypes;

import java.util.List;

import model.LogLine;


/**
 * 
 * The user with id '' created the custom scale with id '' from the course with the id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserScaleCourse extends ReferencesLog {

	/**
	 * Instacia única de la clase UserScaleCourse.
	 */
	private static UserScaleCourse instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserScaleCourse() {
	}

	/**
	 * Devuelve la instancia única de UserScaleCourse.
	 * @return instancia singleton
	 */
	public static UserScaleCourse getInstance() {
		if (instance == null) {
			instance = new UserScaleCourse();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//scale id
		// La id del curso se ignora de momento

	}

}
