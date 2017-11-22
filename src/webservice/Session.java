package webservice;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import controllers.UBUGrades;
import model.Course;

/**
 * Clase sesión. Obtiene el token de usuario y guarda sus parámetros. Establece
 * la sesión.
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class Session {
	private String email;
	private String password;
	private String tokenUser;
	private Course actualCourse;
	
	static final Logger logger = LoggerFactory.getLogger(Session.class);

	/**
	 * Constructor de la clase Session
	 * 
	 * @param mail
	 *            correo del usuario
	 * @param pass
	 *            contraseña de usuario
	 */
	public Session(String mail, String pass) {
		this.email = mail;
		this.password = pass;
	}

	/**
	 * Obtiene el token de usuario
	 * 
	 * @return
	 */
	public String getToken() {
		return this.tokenUser;
	}

	/**
	 * Establece el token del usuario a partir de usuario y contraseña. Se
	 * realiza mediante una petición http al webservice de Moodle
	 * 
	 * @throws Exception
	 */
	public void setToken() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			HttpGet httpget = new HttpGet(UBUGrades.host + "/login/token.php?username=" + this.email + "&password="
					+ this.password + "&service=" + MoodleOptions.SERVICIO_WEB_MOODLE);
			response = httpclient.execute(httpget);
			
			String respuesta = EntityUtils.toString(response.getEntity());
			JSONObject jsonObject = new JSONObject(respuesta);
			if (jsonObject != null) {
				this.tokenUser = jsonObject.getString("token");
			}
		} finally {
			httpclient.close();
			if(response != null) {response.close();}
		}
	}

	/**
	 * Devuelve el email del usuario
	 * 
	 * @return email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Modifica el email del usuario
	 * 
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
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
	 * Modifica el curso actual
	 * 
	 * @param course
	 */
	public void setActualCourse(Course course) {
		this.actualCourse = course;
	}
}
