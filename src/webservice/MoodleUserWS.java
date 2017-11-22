package webservice;

import java.util.ArrayList;
import java.util.Date;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.UBUGrades;
import model.Course;
import model.MoodleUser;

/**
 * Clase MoodleUser para webservices. Recoge funciones útiles para servicios web
 * relacionados con un MoodleUser.
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class MoodleUserWS {
	
	static final Logger logger = LoggerFactory.getLogger(MoodleUserWS.class);
	
	/**
	 * Establece los parámetros del usuario logueado.
	 * 
	 * @param token
	 *            token de usuario
	 * @param eMail
	 *            email de usuario
	 * @param mUser
	 *            moodleUser
	 * @throws Exception
	 */
	public static void setMoodleUser(String token, String eMail, MoodleUser mUser) throws Exception {
		logger.info("Obteniendo los datos del usuario.");
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpget = new HttpGet(UBUGrades.host + "/webservice/rest/server.php?wstoken=" + token
					+ "&moodlewsrestformat=json&wsfunction=" + MoodleOptions.OBTENER_INFO_USUARIO
					+ "&field=email&values[0]=" + eMail);
			CloseableHttpResponse response = httpclient.execute(httpget);
			String respuesta = EntityUtils.toString(response.getEntity());
			JSONArray jsonArray = new JSONArray(respuesta);
			if (jsonArray != null) {
				JSONObject jsonObject = (JSONObject) jsonArray.get(0);
				if (jsonObject != null) {
					mUser.setId(jsonObject.getInt("id"));
					if (jsonObject.has("username"))
						mUser.setUserName(jsonObject.getString("username"));
					if (jsonObject.has("fullname"))
						mUser.setFullName(jsonObject.getString("fullname"));
					if (jsonObject.has("email"))
						mUser.setEmail(jsonObject.getString("email"));
					if (jsonObject.has("firstaccess"))
						mUser.setFirstAccess(new Date(jsonObject.getLong("firstaccess") * 1000));
					if (jsonObject.has("lastaccess"))
						mUser.setLastAccess(new Date(jsonObject.getLong("lastaccess") * 1000));
				}
			}
		} finally {
			httpclient.close();
		}
	}

	/**
	 * Almacena los cursos del usuario
	 * 
	 * @param token
	 *            token de usuario
	 * @param mUser
	 *            usuario
	 * @throws Exception
	 */
	public static void setCourses(String token, MoodleUser mUser) throws Exception {
		logger.info("Obteniendo los cursos del usuario.");
		CloseableHttpClient httpclient = HttpClients.createDefault();
		ArrayList<Course> courses = new ArrayList<Course>();
		try {
			HttpGet httpget = new HttpGet(UBUGrades.host + "/webservice/rest/server.php?wstoken=" + token
					+ "&moodlewsrestformat=json&wsfunction=" + MoodleOptions.OBTENER_CURSOS + "&userid="
					+ mUser.getId());
			CloseableHttpResponse response = httpclient.execute(httpget);
			
			String respuesta = EntityUtils.toString(response.getEntity());
			JSONArray jsonArray = new JSONArray(respuesta);
			if (jsonArray != null) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = (JSONObject) jsonArray.get(i);
					if (jsonObject != null) {
						courses.add(new Course(jsonObject));
					}
				}
			}
		} finally {
			httpclient.close();
		}
		mUser.setCourses(courses);
	}
}
