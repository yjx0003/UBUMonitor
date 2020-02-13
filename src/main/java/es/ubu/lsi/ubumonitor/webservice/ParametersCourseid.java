package es.ubu.lsi.webservice;

/**
 * Clase abstracta que añade el id del curso a los parametros de la URL.
 * @author Yi Peng Ji
 *
 */
public abstract class ParametersCourseid extends WebService{

	private int courseid;
	
	/**
	 * Constructor con el id del curso.
	 * @param courseid id del curso
	 */
	public ParametersCourseid(int courseid) {
	
		this.courseid=courseid;
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToUrlParameters() {
		appendToUrlCourseid(courseid);
		
	}

}
