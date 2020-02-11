package webservice;

/**
 * Clase abstracta que añade el id del curso y adicionalmente id del grupo o
 * usuario.
 * 
 * @author Yi Peng Ji
 *
 */
public abstract class ParametersCourseidUseridGroupid extends WebService {

	private int courseid;
	private int groupid;
	private int userid;

	public enum OptionalParameter {
		USERID, GROUPID;
	}

	/**
	 * Constructor con el id del curso
	 * 
	 * @param courseid
	 *            id del curso
	 */
	public ParametersCourseidUseridGroupid(int courseid) {
		this.courseid = courseid;
	}

	/**
	 * Constructor con el id del curso y parametros adicional de id usuario o de
	 * grupo.
	 * 
	 * @param courseid
	 *            id del curso
	 * @param optionalParameter
	 *            parametro opcional
	 * @param userOrGroupid
	 *            id de usuario o de grupo
	 */
	public ParametersCourseidUseridGroupid(int courseid, OptionalParameter optionalParameter, int userOrGroupid) {

		this(courseid);

		switch (optionalParameter) {
		case USERID:
			this.userid = userOrGroupid;
			break;
		case GROUPID:
			this.groupid = userOrGroupid;
			break;
		default:
			throw new IllegalArgumentException("No puede ser nulo");
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToUrlParameters() {

		appendToUrlCourseid(courseid);
		if (userid != 0)
			appendToUrlUserid(userid);
		if (groupid != 0)
			appendToUrlGruopid(groupid);

	}

}
