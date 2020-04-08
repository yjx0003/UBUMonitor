package es.ubu.lsi.ubumonitor.model.log.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;
/**
 * The user with id '' deleted the grade with id '' for the user with id '' for the grade item with id ''.
 * @author Yi Peng Ji
 *
 */
public class UserGradeAffectedGradeitem extends ReferencesLog {

	/**
	 * Instacia única de la clase UserGradeAffectedGradeitem.
	 */
	private static UserGradeAffectedGradeitem instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserGradeAffectedGradeitem() {
	}

	/**
	 * Devuelve la instancia única de UserGradeAffectedGradeitem.
	 * @return instancia singleton
	 */
	public static UserGradeAffectedGradeitem getInstance() {
		if (instance == null) {
			instance = new UserGradeAffectedGradeitem();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//grade id
		setAffectedUserById(log, ids.get(2));
		//grade item id
		
	}

}
