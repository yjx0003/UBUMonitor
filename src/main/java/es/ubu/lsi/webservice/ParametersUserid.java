package es.ubu.lsi.webservice;

/**
 * Clase abstracta que recoge el id del usuario.
 * @author Yi Peng Ji
 *
 */
public abstract class ParametersUserid extends WebService {

	private int userid;
	
	/**
	 * Id de usuario.
	 * @param userid id de usuario
	 */
	public ParametersUserid(int userid) {
		this.userid = userid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToUrlParameters() {
		appendToUrlUserid(userid);
	}

}
