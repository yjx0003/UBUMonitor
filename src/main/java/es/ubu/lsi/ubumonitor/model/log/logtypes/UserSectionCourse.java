package es.ubu.lsi.ubumonitor.model.log.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;
/**
 * The user with id '' updated section number '' for the course with id ''
 * The user with id '' viewed the section number '' of the course with id ''.

 * 
 * @author Yi Peng Ji
 *
 */
public class UserSectionCourse extends ReferencesLog{

	
	/**
	 * Instacia única de la clase UserSectionCourse.
	 */
	private static UserSectionCourse instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserSectionCourse() {
	}

	/**
	 * Devuelve la instancia única de UserSectionCourse.
	 * @return instancia singleton
	 */
	public static UserSectionCourse getInstance() {
		if (instance == null) {
			instance = new UserSectionCourse();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//section id
		// La id del curso se ignora de momento

	}

	
}
