package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;
/**
 * 
 * The user with id '' has viewed the tour with id '' at step index '' (id '') on the page with URL ''.
 * The user with id '' has ended the tour with id '' at step index '' (id '') on the page with URL ''.
 * The user with id '' has started the tour with id '' on the page with URL ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserTour extends ReferencesLog {

	
	/**
	 * Instacia única de la clase UserTour.
	 */
	private static UserTour instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserTour() {
	}

	/**
	 * Devuelve la instancia única de UserTour.
	 * @return instancia singleton
	 */
	public static UserTour getInstance() {
		if (instance == null) {
			instance = new UserTour();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//tour id
	}

}
