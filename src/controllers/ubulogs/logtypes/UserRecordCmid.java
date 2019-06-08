package controllers.ubulogs.logtypes;

import java.util.List;

import model.LogLine;

/**
 * The user with id '' created the data record with id '' for the data activity with course module id ''.
 * The user with id '' deleted the data record with id '' in the data activity with course module id ''.
 * The user with id '' updated the data record with id '' in the data activity with course module id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserRecordCmid extends ReferencesLog {

	/**
	 * Instacia única de la clase UserRecordCmid.
	 */
	private static UserRecordCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserRecordCmid() {
	}

	/**
	 * Devuelve la instancia única de UserRecordCmid.
	 * @return instancia singleton
	 */
	public static UserRecordCmid getInstance() {
		if (instance == null) {
			instance = new UserRecordCmid();
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
