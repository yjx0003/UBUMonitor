package es.ubu.lsi.ubumonitor.model.log.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;
/**
 * The user with id '' created the event '' with id ''.
 * The user with id '' deleted the event '' with id ''.
 * The user with id '' updated the event '' with id ''.
 * @author Yi Peng Ji
 *
 */
public class UserCalendar extends ReferencesLog {

	
	/**
	 * Instacia única de la clase UserCalendar.
	 */
	private static UserCalendar instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserCalendar() {
	}

	/**
	 * Devuelve la instancia única de UserCalendar.
	 * @return instancia singleton
	 */
	public static UserCalendar getInstance() {
		if (instance == null) {
			instance = new UserCalendar();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//calendar event id core_calendar_get_calendar_events
	}

}
