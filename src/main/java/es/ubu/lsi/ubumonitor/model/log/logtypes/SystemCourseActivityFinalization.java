package es.ubu.lsi.ubumonitor.model.log.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;

public class SystemCourseActivityFinalization extends ReferencesLog {

	/**
	 * Instacia única de la clase UserPagePagePageCmid.
	 */
	private static SystemCourseActivityFinalization instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private SystemCourseActivityFinalization() {
	}

	/**
	 * Devuelve la instancia única de UserPagePagePageCmid.
	 * @return instancia singleton
	 */
	public static SystemCourseActivityFinalization getInstance() {
		if (instance == null) {
			instance = new SystemCourseActivityFinalization();
		}
		return instance;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		if(ids.size() == 3) {
			UserCmidAffected.getInstance().setLogReferencesAttributes(log, ids);
		}else if(ids.size() == 4) {
			UserStateCmidAffected.getInstance().setLogReferencesAttributes(log, ids);
		}

	}
}