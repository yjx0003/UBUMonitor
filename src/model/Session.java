package model;

import org.apache.http.client.methods.HttpGet;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webservice.MoodleOptions;

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
	
	static final Logger logger = LoggerFactory.getLogger(Session.class);

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
	 * @return
	 * 		El token del usuario.
	 */
	public String getToken() {
		return this.tokenUser;
	}

	/**
	 * Establece el token del usuario a partir de usuario y contraseña. Se
	 * realiza mediante una petición http al webservice de Moodle.
	 * 
	 * @param host
	 * 		El nombre del host.
	 * 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public void setToken(String host) throws IOException, JSONException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			HttpGet httpget = new HttpGet(host + "/login/token.php?username=" + this.userName + "&password="
					+ this.password + "&service=" + MoodleOptions.SERVICIO_WEB_MOODLE);
			response = httpclient.execute(httpget);
			
			String respuesta = EntityUtils.toString(response.getEntity());
			JSONObject jsonObject = new JSONObject(respuesta);
			this.tokenUser = jsonObject.getString("token");
		} finally {
			httpclient.close();
			if(response != null) {response.close();}
		}
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
	 * 		El nombre de usuario.
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
	 * 		El curso.
	 */
	public void setActualCourse(Course course) {
		this.actualCourse = course;
	}
}
