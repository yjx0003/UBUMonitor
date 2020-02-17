package es.ubu.lsi.ubumonitor.webservice;

import java.io.IOException;
import java.util.Set;

import org.json.JSONObject;
import org.json.JSONTokener;

import es.ubu.lsi.ubumonitor.controllers.Connection;
import es.ubu.lsi.ubumonitor.webservice.core.CoreUserGetUsersByField.Field;
import okhttp3.Response;

/**
 * Clase principal encargada de buscar el token del servicio rest API de moodle.
 * Tambien es el encargado de obtener las respuestas de las funciones de Moodle.
 * 
 * @author Yi Peng Ji
 *
 */
public abstract class WebService {

	private static String token;
	private static String urlWithToken;
	private static final String REST_FORMAT = "json";
	private StringBuilder parameters;

	/**
	 * Constructor que comprueba si ya hay token asignado al usuario e inicializa
	 * los parametros de la URL a vacio.
	 */
	public WebService() {

		checkToken();

		this.parameters = new StringBuilder();

	}

	/**
	 * Inicializa con el cuerpo de la url con el token.
	 * 
	 * @param host host
	 * @param token token del usuario
	 */
	public static void initialize(String host, String token) {

		WebService.token = token;

		urlWithToken = host + "/webservice/rest/server.php?wstoken=" + token + "&moodlewsrestformat=" + REST_FORMAT
				+ "&wsfunction=";

	}

	/**
	 * Intenta conectarse al servicio de moodle para recuperar el token del usuario
	 * para la API.
	 * 
	 * @param host host de moodle
	 * @param userName username de moodle
	 * @param password contraseña de moodle
	 * @throws IOException si no se ha podido conectar al host
	 */
	public static void initialize(String host, String userName, String password) throws IOException {
		String url = host + "/login/token.php?username=" + userName + "&password=" + password + "&service="
				+ WSFunctions.MOODLE_MOBILE_APP;
		Response response = Connection.getResponse(url);
		JSONObject jsonObject = new JSONObject(new JSONTokener(response.body().byteStream()));
		response.close();
		String token = jsonObject.getString("token");

		initialize(host, token);
	}

	/**
	 * Comprueba que hay token asignado.
	 */
	private static void checkToken() {
		if (token == null) {
			throw new IllegalStateException(
					"Token no configurado, llama al metodo initizialize de la clase WebService para configurarlo.");
		}
	}

	/**
	 * Devuelve el token
	 * 
	 * @return token
	 */
	public static String getToken() {
		checkToken();
		return token;
	}

	/**
	 * Devuelve la respuesta de una petición de una función de Moodle con sus
	 * parámetros.
	 * 
	 * @return la respuesta de petición
	 * @throws IOException si ha habido un problema al conectarse con el servidor
	 */
	public Response getResponse() throws IOException {

		parameters = new StringBuilder();

		appendToUrlParameters();

		String url = urlWithToken + getWSFunction() + parameters;

		return getResponse(url);

	}

	/**
	 * Devuelve la función de moodle.
	 * 
	 * @return la función de moodle
	 */
	public abstract WSFunctions getWSFunction();

	/**
	 * Devuelve la URL completa con token incluido (tener cuidado de no guardarlo en
	 * el logger, información sensible). Dedicado a pruebas.
	 * 
	 * @return la url completa
	 */
	public String geCompletetUrl() {
		return urlWithToken + getWSFunction() + parameters;
	}

	/**
	 * Añade los parametros adicionales a la funcion de moodle.
	 */
	protected abstract void appendToUrlParameters();

	/**
	 * Añade el id de usuario como parametro adicional de la URL si no es 0.
	 * 
	 * @param userid id de usuario
	 */
	protected void appendToUrlUserid(int userid) {
		if (userid != 0)
			parameters.append("&userid=" + userid);
	}

	/**
	 * Añade el id curso.
	 * 
	 * @param courseid id de curso
	 */
	protected void appendToUrlCourseid(int courseid) {
		if (courseid != 0)
			parameters.append("&courseid=" + courseid);
	}

	/**
	 * Añade los ids de cursos.
	 * 
	 * @param coursesids conjunto de ids de curso
	 */
	protected void appendToUrlCoursesids(Set<Integer> coursesids) {
		if (coursesids != null) {
			for (Integer courseid : coursesids) {
				parameters.append("&courseids[]=" + courseid);
			}
		}
	}

	/**
	 * Añade el id del grupo
	 * 
	 * @param groupid id del grupo
	 */
	protected void appendToUrlGruopid(int groupid) {
		if (groupid != 0)
			parameters.append("&groupid=" + groupid);
	}

	/**
	 * Añade un conjunto de values.
	 * 
	 * @param values conjunto de values
	 */
	protected void appendToUrlValues(Set<String> values) {
		if (values != null) {
			for (String value : values) {
				parameters.append("&values[]=" + value);
			}
		}
	}

	/**
	 * Añade campos a la URL
	 * 
	 * @param field campos
	 */
	protected void appendToUrlField(Field field) {
		if (field != null)
			parameters.append("&field=" + field);
	}

	/**
	 * Añade opciones
	 * 
	 * @param index indice
	 * @param name nombre
	 * @param value valor
	 */
	protected void appendToUrlOptions(int index, String name, String value) {
		parameters.append("&options[" + index + "][name]=" + name + "&options[" + index + "][value]=" + value);
	}

	/**
	 * Añade opciones
	 * 
	 * @param index indice
	 * @param name nombre
	 * @param value valor
	 */
	protected void appendToUrlOptions(int index, String name, int value) {
		appendToUrlOptions(index, name, Integer.toString(value));
	}

	/**
	 * Añade opciones
	 * 
	 * @param index indice
	 * @param name nombre
	 * @param value valor
	 */
	protected void appendToUrlOptions(int index, String name, boolean value) {
		appendToUrlOptions(index, name, Boolean.toString(value));
	}

	/**
	 * Añade opciones
	 * 
	 * @param index indice
	 * @param name nombre
	 * @param value valor
	 */
	protected void appendToUrlCriteria(String key, String value) {
		parameters.append("&criteria[0][key]=" + key + "&criteria[0][value]=" + value);
	}

	protected void appendToUrlClassification(String classification) {
		if (classification != null) {
			parameters.append("&classification=" + classification);
		}
	}

	/**
	 * Devueve el contenido de la funcion de moodle.
	 * 
	 * @param url url completa de la peticion
	 * @return el contenido de la respues de la peticion
	 * @throws IOException si ha habido un error al realizar la peticion
	 */
	private Response getResponse(String url) throws IOException {
		return Connection.getResponse(url);

	}

}
