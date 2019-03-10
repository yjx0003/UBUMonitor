package model;




/**
 * Clase sesión. Obtiene el token de usuario y guarda sus parámetros. Establece
 * la sesión.
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class Session {
	private String userName;
	private String password;
	private String tokenUser;
	private Course actualCourse;


	/**
	 * Constructor de la clase Session
	 * 
	 * @param mail
	 *            correo del usuario
	 * @param pass
	 *            contraseña de usuario
	 */
	public Session(String userName, String pass) {
		this.userName = userName;
		this.password = pass;
	}

	/**
	 * Obtiene el token de usuario
	 * 
	 * @return El token del usuario.
	 */
	public String getToken() {
		return this.tokenUser;
	}

	/**
	 * Establece el token del usuario a partir de usuario y contraseña. Se realiza
	 * mediante una petición http al webservice de Moodle.
	 * 
	 * @param tokenUser
	 */
	public void setToken(String tokenUser) {
		this.tokenUser = tokenUser;
	}

	/**
	 * Devuelve el nombre de usuario del usuario
	 * 
	 * @return userName
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * Modifica el nombre de usuario usuario
	 * 
	 * @param userName
	 *            El nombre de usuario.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Devuelve el curso actual
	 * 
	 * @return actualCourse
	 */
	public Course getActualCourse() {
		return this.actualCourse;
	}

	/**
	 * Devuelve el password de la cuenta
	 * 
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Modifica el curso actual.
	 * 
	 * @param course
	 *            El curso.
	 */
	public void setActualCourse(Course course) {
		this.actualCourse = course;
	}
}
