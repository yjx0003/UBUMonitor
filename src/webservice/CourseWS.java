package webservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.Assignment;
import model.Course;
import model.EnrolledUser;
import model.GradeReportLine;
import model.Scale;

/**
 * Clase Course para webservices. Recoge funciones útiles para servicios web
 * relacionados con un curso.
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class CourseWS {

	static final Logger logger = LoggerFactory.getLogger(CourseWS.class);

	private CourseWS() {
		throw new IllegalStateException("Clase de utilidad");
	}

	/**
	 * Establece los usuarios que están matriculados en un curso junto con su rol y
	 * grupo.
	 * 
	 * @param host
	 *            El nombre del host.
	 * @param token
	 *            El token de usuario.
	 * @param course
	 *            El curso.
	 * 
	 * @throws IOException
	 * @throws JSONException
	 */
	public static void setEnrolledUsers(String host, String token, Course course) throws IOException, JSONException {
		logger.info("Obteniendo los usuarios del curso.");
		CloseableHttpClient httpclient = HttpClients.createDefault();
		ArrayList<EnrolledUser> eUsers = new ArrayList<>();
		try {
			HttpGet httpget = new HttpGet(
					host + "/webservice/rest/server.php?wstoken=" + token + "&moodlewsrestformat=json&wsfunction="
							+ MoodleOptions.OBTENER_USUARIOS_MATRICULADOS + "&courseid=" + course.getId());
			CloseableHttpResponse response = httpclient.execute(httpget);

			String respuesta = EntityUtils.toString(response.getEntity());
			JSONArray jsonArray = new JSONArray(respuesta);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);
				if (jsonObject != null) {
					eUsers.add(new EnrolledUser(jsonObject));
				}
			}
		} catch (IOException e) {
			logger.error("Error de conexion con Moodle al obtener los usuarios matriculados.", e);
			throw new IOException("Error de conexion con Moodle al obtener los usuarios matriculados."
					+ "\n Es posible que su equipo haya perdido la conexion a internet.");
		} catch (JSONException e) {
			logger.error("Se ha producido un error al obtener los usuarios matriculados.", e);
			throw new JSONException("Se ha producido un error al obtener los usuarios matriculados.");
		} finally {
			httpclient.close();
		}
		course.setEnrolledUsers(eUsers);
		course.setRoles(eUsers);
		course.setGroups(eUsers);
	}

	/**
	 * Esta función se usará para obtener todos los GradeReportLine del primer
	 * usuario matriculado y así sacar la estructura del calificador del curso para
	 * después mostrarla como TreeView en la vista.
	 * 
	 * @param host
	 *            El nombre del host.
	 * @param token
	 *            Token del profesor logueado.
	 * @param userId
	 *            Id del usuario a cargar.
	 * @param course
	 *            El curso a cargar.
	 * 
	 * @throws IOException
	 * @throws JSONException
	 */
	public static void setGradeReportLines(String host, String token, int userId, Course course)
			throws IOException, JSONException, Exception {
		logger.info("Generando el arbol del calificador.");
		try {
			// Obtenemos el JSON con los datos
			JSONObject graddesData = getGradesData(host, token, userId, course.getId());

			// En esta pila sólo van a entrar Categorías. Se mantendrán en
			// la pila mientran tengan descendencia.
			// Una vez añadida al árbol toda la descendencia de un nodo,
			// este nodo se saca de la pila y se añade al árbol.
			Stack<GradeReportLine> deque = new Stack<>();

			JSONArray tables = (JSONArray) graddesData.get("tables");
			JSONObject alumn = (JSONObject) tables.get(0);
			JSONArray tableData = alumn.getJSONArray("tabledata");
			logger.debug("Contenido del calificador en bruto: {}", tableData.toString());

			// El elemento table data tiene las líneas del configurador
			// (que convertiremos a GradeReportLines)
			for (int i = 0; i < tableData.length(); i++) {
				JSONObject tableDataElement = tableData.getJSONObject(i);
				logger.debug("Categoría o item leido del calificador: {}", tableDataElement.toString());
				// sea categoría o item, se saca de la misma manera el
				// nivel del itemname
				JSONObject itemname = tableDataElement.getJSONObject("itemname");

				// Si es un feedback (item o suma de
				// calificaciones):
				if (tableDataElement.isNull("leader")) {
					GradeReportLine actualLine = getGradeReportLineData(tableDataElement);
					String typeActivity = actualLine.getNameType();

					if (!typeActivity.equals("Category")) { // Si es un item
						// Si es un assignment obtenemos la escala si la tiene
						if (typeActivity.equals("Assignment")) {
							Assignment assignment = new Assignment(actualLine.getName(), typeActivity,
									actualLine.getWeight(), actualLine.getRangeMin(), actualLine.getRangeMax());
							assignment.setScaleId(
									getAssignmentScale(host, token, course.getId(), actualLine.getName(), course));
							actualLine.setActivity(assignment);
						}
						if (!deque.isEmpty()) {
							deque.lastElement().addChild(actualLine);
						}
					} else {
						if (!deque.isEmpty()) { // FIX RMS 1.5.2
							// Obtenemos el elemento cabecera de la pila
							GradeReportLine categoryLine = deque.pop();
							// Establecemos los valores restantes
							categoryLine.setId(actualLine.getId());
							categoryLine.setWeight(actualLine.getWeight());
							categoryLine.setRangeMin(actualLine.getRangeMin());
							categoryLine.setRangeMax(actualLine.getRangeMax());
							categoryLine.setNameType(actualLine.getNameType());
							categoryLine.setGrade(actualLine.getGrade());
						}
					}
				} else {// --- Si es una categoría
					String nameLine = getNameCategorie(itemname.getString("content"));
					int actualLevel = getActualLevel(itemname.getString("class"));
					int idLine = getIdLine(itemname.getString("id"));

					// Añadimos la cabecera de la categoria a la pila
					GradeReportLine actualLine = new GradeReportLine(idLine, nameLine, actualLevel, false);
					// Lo añadimos como hijo de la categoria anterior
					if (!deque.isEmpty()) {
						deque.lastElement().addChild(actualLine);
					}

					// Añadimos esta cabecera a la pila
					deque.add(actualLine);

					// Si es el elemento del primer nivel lo añadimos, es la raiz del arbol
					if (actualLevel == 1) {
						course.setGradeReportLine(actualLine);
					}
				}
			} // End for
			course.setActivities(course.getGradeReportLines());

		} catch (

		IOException e) {
			logger.error("Error de conexion con Moodle al generar el arbol del calificador.", e);
			throw new IOException("Error de conexion con Moodle al generar el arbol del calificador."
					+ "\n Es posible que su equipo haya perdido la conexion a internet.");
		} catch (JSONException e) {
			logger.error("Se ha producido un error al generar el arbol del calificador.", e);
			throw new JSONException("Se ha producido un error al generar el arbol del calificador.");
		} catch (Exception ex) {
			logger.error("Se ha producido un error al obtener la estructura del libro de calificaciones.", ex);
			throw new Exception("Se ha producido un error al obtener la estructura del libro de calificaciones.");
		}

	}

	/**
	 * Genera todos los GradeReportLines de un curso para un usuario.Este proceso se
	 * realiza al inicio alamcenando en memoria los datos.
	 * 
	 * @param host
	 *            El nombre del host.
	 * @param token
	 *            Token del profesor logueado.
	 * @param userId
	 *            Id del usuario a cargar.
	 * @param courseId
	 *            Curso del que se quieren cargar los datos.
	 * @return ArrayList con todos los GradeReportLines del usuario.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	public static ArrayList<GradeReportLine> getUserGradeReportLines(String host, String token, int userId,
			int courseId) throws IOException, JSONException, Exception {

		ArrayList<GradeReportLine> gradeReportLines = null;
		try {
			// Obtenemos el JSON con los datos
			JSONObject gradesData = getGradesData(host, token, userId, courseId);

			// lista de GradeReportLines
			gradeReportLines = new ArrayList<>();

			JSONArray tables = (JSONArray) gradesData.get("tables");
			JSONObject alumn = (JSONObject) tables.get(0);
			JSONArray tableData = alumn.getJSONArray("tabledata");
			// El elemento table data tiene las líneas del configurador
			// (que convertiremos a GradeReportLines)
			for (int i = 0; i < tableData.length(); i++) {
				JSONObject tableDataElement = tableData.getJSONObject(i);
				// Si es un item o suma de calificaciones:
				if (tableDataElement.isNull("leader")) {
					// Añadimos el elemento a la lista como item
					gradeReportLines.add(getGradeReportLineData(tableDataElement));
				}
			} // End for

		} catch (IOException e) {
			logger.error("Error de conexion con Moodle al obtener las notas del alumno.", e);
			throw new IOException("Error de conexion con Moodle al obtener las notas del alumno."
					+ "\n Es posible que su equipo haya perdido la conexion a internet.");
		} catch (JSONException e) {
			logger.error("Se ha producido un error al obtener las notas del alumno.", e);
			throw new JSONException("Se ha producido un error al obtener las notas del alumno.");
		} catch (Exception ex) {
			logger.error("Se ha producido un error al obtener la estructura del libro de calificcciones.", ex);
			throw new Exception("Se ha producido un error al obtener la estructura del libro de calificaciones.");
		}

		return gradeReportLines;
	}

	private static GradeReportLine getGradeReportLineData(JSONObject tableDataElement) throws Exception {
		try {
			logger.debug("Añadiendo línea de calificaciones: {}", tableDataElement.toString());
			GradeReportLine actualGRL = null;
			// sea categoría o item, se saca de la misma manera el
			// nivel del itemname
			JSONObject itemname = tableDataElement.getJSONObject("itemname");
			int actualLevel = getActualLevel(itemname.getString("class"));
			int idLine = getIdLine(itemname.getString("id"));
			String nameContainer = itemname.getString("content");
			String nameLine;
			String typeActivity;

			// Sacamos la nota (grade)
			String grade = "0"; // Default value
			JSONObject gradeContainer = tableDataElement.optJSONObject("grade");
			if (gradeContainer != null) {
				grade = getNumber(gradeContainer.getString("content"));
			}

			// Sacamos el porcentaje
			Float percentage = 0.0F; // FIX BUG IN VERSION 1.5.2 RMS
			JSONObject percentageContainer = tableDataElement.optJSONObject("percentage");
			if (percentageContainer != null) {
				percentage = getFloat(percentageContainer.getString("content"));
			}

			// Sacamos el peso
			JSONObject weightContainer = tableDataElement.optJSONObject("weight");
			Float weight = Float.NaN;
			if (weightContainer != null) {
				weight = getFloat(weightContainer.getString("content"));
			}

			// Sacamos el rango
			String rangeMin = "NA"; // default value Non Available
			String rangeMax = "NA"; // default value Non Available
			JSONObject rangeContainer = tableDataElement.optJSONObject("range");
			if (rangeContainer != null) {
				rangeMin = getRange(rangeContainer.getString("content"), true);
				rangeMax = getRange(rangeContainer.getString("content"), false);
			}

			// Si es una actividad (assignment o quiz)
			// Se reconocen por la etiqueta "<a"
			if (nameContainer.substring(0, 2).equals("<a")) {
				nameLine = getNameActivity(nameContainer);
				typeActivity = assignmentOrQuizOrForum(nameContainer);
			} else {
				// Si es un item manual o suma de calificaciones
				// Se reconocen por la etiqueta "<span"
				nameLine = getNameManualItemOrEndCategory(nameContainer);
				typeActivity = manualItemOrEndCategory(nameContainer);
			}

			boolean typeLine = !typeActivity.equals("Category");

			// Añadimos la linea actual
			actualGRL = new GradeReportLine(idLine, nameLine, actualLevel, typeLine, weight, rangeMin, rangeMax, grade,
					percentage, typeActivity);
			return actualGRL;
		} catch (Exception ex) {
			logger.error("Error loading grade book: {}", ex.getMessage());
			throw ex;
		}
	}

	/**
	 * Genera todos los GradeReportLines de un curso para un usuario.Este proceso se
	 * realiza al inicio alamcenando en memoria los datos. Moodle 3.3.
	 * 
	 * @param host
	 *            El nombre del host.
	 * @param token
	 *            Token del profesor logueado.
	 * @param userId
	 *            Id del usuario a cargar.
	 * @param courseId
	 *            Curso del que se quieren cargar los datos.
	 * @return ArrayList con todos los GradeReportLines del usuario.
	 * 
	 * @throws IOException
	 * @throws JSONException
	 */
	public static ArrayList<GradeReportLine> getUserGradeReportLinesMoodle33(String host, String token, int userId,
			int courseId) throws IOException, JSONException {

		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		ArrayList<GradeReportLine> gradeReportLines = null;
		try {
			String call = host + "/webservice/rest/server.php?wstoken=" + token + "&moodlewsrestformat=json&wsfunction="
					+ MoodleOptions.OBTENER_NOTAS_ALUMNO + "&courseid=" + courseId + "&userid=" + userId;
			HttpGet httpget = new HttpGet(call);
			response = httpclient.execute(httpget);

			String respuesta = EntityUtils.toString(response.getEntity());
			JSONObject jsonArray = new JSONObject(respuesta);

			// lista de GradeReportLines
			gradeReportLines = new ArrayList<>();

			if (jsonArray != null) {
				JSONArray usergrades = (JSONArray) jsonArray.get("usergrades");
				JSONObject alumn = (JSONObject) usergrades.get(0);

				JSONArray gradeItems = alumn.getJSONArray("gradeitems");

				// El elemento gradeitems tiene cada linea del calificador
				// que convertiremos en GradeReportLines
				for (int i = 0; i < gradeItems.length(); i++) {
					JSONObject gradeItemsElement = gradeItems.getJSONObject(i);

					// Obtenemos los datos necesarios
					int id;
					String name = gradeItemsElement.getString("itemname");
					String type = gradeItemsElement.getString("itemtype");
					// Para los tipos categoria y curso hay dos ids
					// Para estos casos obtenemos el id de iteminstance
					if (type.equals("category") || type.equals("course")) {
						id = gradeItemsElement.getInt("iteminstance");
					} else {
						id = gradeItemsElement.getInt("id");
					}
					int rangeMin = gradeItemsElement.getInt("grademin");
					int rangeMax = gradeItemsElement.getInt("grademax");
					String grade = gradeItemsElement.getString("gradeformatted");

					// Añadimos el nuevo GradeReportLine
					gradeReportLines.add(
							new GradeReportLine(id, name, grade, String.valueOf(rangeMin), String.valueOf(rangeMax)));
					logger.info("Añadiendo el GRL obtenido");
				}
			}
		} catch (IOException e) {
			logger.error("Error de conexion con Moodle al obtener las notas de alumno.", e);
			throw new IOException("Error de conexion con Moodle al generar el arbol del calificador."
					+ "\n Es posible que su equipo haya perdido la conexion a internet.");
		} catch (JSONException e) {
			logger.error("Se ha producido un error al obtener las notas del alumno.", e);
			throw new JSONException("Se ha producido un error al obtener las notas del alumno.");
		} finally {
			if (response != null) {
				response.close();
			}
			httpclient.close();
		}
		return gradeReportLines;
	}

	/**
	 * Devuelve el id del item
	 * 
	 * @param data
	 *            La cadena a analizar.
	 * @return id de un item
	 */
	public static int getIdLine(String data) {
		String[] matrix = data.split("_");
		return Integer.parseInt(matrix[1]);
	}

	/**
	 * Devuelve el nivel del GradeReportLine que está siendo leída.
	 * 
	 * @param data
	 *            La cadena a analizar.
	 * @return nivel de la línea
	 */
	public static int getActualLevel(String data) {
		int result = 0;
		result = Integer.parseInt(data.substring(5, data.indexOf(' ')));
		return result;
	}

	/**
	 * Devuelve el nombre de una categoría
	 * 
	 * @param data
	 *            La cadena a analizar.
	 * @return El nombre de la categoría.
	 */
	public static String getNameCategorie(String data) {
		String result = "";
		// Busco el final de la cadena única a partir de la cual empieza el
		// nombre de la categoría
		int begin = data.lastIndexOf('>') + 1;
		// El nombre termina al final del texto
		int end = data.length();
		// Me quedo con la cadena entre esos índices
		result = data.substring(begin, end);

		return result;
	}

	/**
	 * Devuelve el nombre de una actividad.
	 * 
	 * @param data
	 *            La cadena a analizar.
	 * @return El nombre de la actividad.
	 */
	public static String getNameActivity(String data) {
		int begin = data.indexOf(" />") + 3;
		int end = data.indexOf("</a>");
		return data.substring(begin, end);
	}

	/**
	 * Devuelve el nombre de un item manual o un cierre de categoría
	 * 
	 * @param data
	 *            La cadena a analizar.
	 * @return El nombre del elemento.
	 */
	public static String getNameManualItemOrEndCategory(String data) {
		int end = data.indexOf("</span>");
		data = data.substring(0, end);
		int begin = data.lastIndexOf('>') + 1;
		return data.substring(begin);
	}

	/**
	 * Devuelve el tipo de un GradeReportLine (actividad o categoría)
	 * 
	 * @param data
	 *            La cadena a anlizar.
	 * @return tipo de línea (cebecera de categoría, suma de calificaciones o item)
	 */
	public static boolean getTypeLine(String data) {
		String[] matrix = data.split(" ");
		return matrix[2].equals("item");
	}

	/**
	 * Comprueba si la línea es una suma de calificaciones de categoría (un cierre
	 * de categoría).
	 * 
	 * @param data
	 *            La cadena a analizar.
	 * @return true si la línea es una suma de calificaciones, false si no
	 */
	public static boolean getBaggtLine(String data) {
		String[] matrix = data.split(" ");
		return matrix[3].equals("baggt");
	}

	/**
	 * Devuelve el rango mínimo o máximo.
	 * 
	 * @param data
	 *            La cadena a analizar.
	 * @param option
	 *            True si es rango mínimo. False si es máximo.
	 * @return rango máximo o mínimo
	 */
	public static String getRange(String data, boolean option) {
		String[] ranges = data.split("&ndash;");
		try {
			if (option) // true = rango mínimo
				return ranges[0];
			else // false = rango máximo
				return ranges[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			return "0";
		}

	}

	/**
	 * Comprueba si una cadena de texto contiene un número decimal.
	 * 
	 * @param cad
	 *            texto
	 * @return true si hay un decimal, false si no
	 */
	public static boolean esDecimal(String cad) {
		try {
			Float.parseFloat(cad);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	/**
	 * Diferencia si la actividad es un quiz o un assignment.
	 * 
	 * @param data
	 *            La cadena a analizar.
	 * @return El nombre de la actividad.
	 */
	private static String assignmentOrQuizOrForum(String data) {
		String url = data.substring(data.lastIndexOf("href="), data.indexOf("?id="));
		if (url.contains("mod/assign")) {
			return "Assignment";
		} else if (url.contains("mod/quiz"))
			return "Quiz";
		else if (url.contains("mod/forum"))
			return "Forum";
		else if (url.contains("mod/workshop"))
			return "Workshop";
		else if (url.contains("mod/lesson"))
			return "Lesson";
		else
			return "";
	}

	/**
	 * Diferencia si la línea es un item manual o un cierre de categoría.
	 * 
	 * @param data
	 *            La cadena a analizar.
	 * @return ManualItem si es un item manual, Category si es un cierre de
	 *         categoría o una cadena vacia si no es niguna de las anteriores.
	 */
	private static String manualItemOrEndCategory(String data) {
		// FNS
		if (data.contains("i/manual_item") || data.contains("Ítem manual")) {
			return "ManualItem";
		} else if (data.contains("i/agg_sum")
				// added by RMS
				|| data.contains("i/calc") || data.contains("i/agg_mean")
				// FNS
				|| data.contains("Calificación calculada")) {
			return "Category";
		} else {
			return "";
		}
	}

	/**
	 * Devuelve un número en formato Float si se encuentra en la cadena pasada.
	 * 
	 * @param data
	 *            La cadena a transformar.
	 * @return Un Float si es un número, Float.NaN si no lo es.
	 */
	public static Float getFloat(String data) {
		Pattern pattern = Pattern.compile("[0-9]{1,3},{1}[0-9]{1,2}");
		Matcher match = pattern.matcher(data);
		if (match.find()) {
			return Float.parseFloat(data.substring(match.start(), match.end()).replace(",", "."));
		}
		return Float.NaN;
	}

	/**
	 * Devuelve el numero, con el formato especificado, encontrado en la cadena
	 * pasada. Si no es un numero devuelve NaN si no hay nota("-") o la cadena
	 * pasada si es una escala.
	 * 
	 * @param data
	 *            La cadena a transformar.
	 * @return NaN si no hay nota("-"), la cadena pasada si es una escala o el
	 *         número si es un número.
	 * 
	 */
	private static String getNumber(String data) {
		Pattern pattern = Pattern.compile("[0-9]{1,3},{1}[0-9]{1,2}");
		Matcher match = pattern.matcher(data);
		if (match.find()) {
			return data.substring(match.start(), match.end());
		}
		// Si es un - es que no hay nota, sino es que es un texto de una escala
		return data.equals("-") ? "NaN" : data;
	}

	/**
	 * Devuelve el id de la escala asociada a la tarea(Assignment) o 0 si no hay
	 * ninguna escala asociada.
	 * 
	 * @param host
	 *            El nombre del host.
	 * @param token
	 *            El token del profesor logueado.
	 * @param courseId
	 *            Curso del que se quiere obtener la escala.
	 * @param assignmentName
	 *            Tarea de la que se quiere obtener la escala.
	 * @param course
	 *            El curso donde almacenar los datos.
	 * @return El id de la escala o 0 si no hay escala asociada.
	 * 
	 * @throws IOException
	 * @throws JSONException
	 */
	private static int getAssignmentScale(String host, String token, int courseId, String assignmentName, Course course)
			throws IOException, JSONException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			HttpGet httpget = new HttpGet(
					host + "/webservice/rest/server.php?wstoken=" + token + "&moodlewsrestformat=json&wsfunction="
							+ MoodleOptions.OBTENER_ASSIGNMENTS + "&courseids[]=" + courseId);
			response = httpclient.execute(httpget);

			String respuesta = EntityUtils.toString(response.getEntity());
			JSONObject jsonArray = new JSONObject(respuesta);
			JSONObject coruses = (JSONObject) ((JSONArray) jsonArray.get("courses")).get(0);
			JSONArray assignments = (JSONArray) coruses.get("assignments");
			for (int i = 0; i < assignments.length(); i++) {
				JSONObject assignment = assignments.getJSONObject(i);
				if (assignment.get("name").equals(assignmentName)) {
					int grade = (int) assignment.get("grade");
					// Si la nota es negativa indica el id de la escala
					if (grade < 0) {
						grade = Math.abs(grade);
						Scale scale = course.getScale(grade);
						return (scale != null) ? scale.getId() : loadScale(host, token, grade, course);
					}
				}
			}
		} catch (IOException e) {
			logger.error("Error al obtener la escala de la tarea, no se ha podido conectar con el webService: {}", e);
			throw new IOException("Error al obtener la escala de la tarea, no se ha podido conectar con el webService.",
					e);
		} catch (JSONException e) {
			logger.error("Error al obtener la esacal de la tarea: {}", e);
			throw new JSONException(e);
		} finally {
			if (response != null) {
				response.close();
			}
			httpclient.close();
		}
		return 0;
	}

	/**
	 * Obtiene los datos de una escala y los alamacena.
	 * 
	 * @param host
	 *            El nombre del host.
	 * @param token
	 *            El token del profesor logueado.
	 * @param scaleId
	 *            El id de la escala.
	 * @param course
	 *            El curso al que añadir los datos.
	 * @return el id de la escala.
	 * 
	 * @throws IOException
	 * @throws JSONException
	 */
	private static int loadScale(String host, String token, int scaleId, Course course)
			throws IOException, JSONException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		Scale scale = null;
		List<String> elements = new ArrayList<>();
		try {
			HttpGet httpget = new HttpGet(host + "/webservice/rest/server.php?wstoken=" + token
					+ "&moodlewsrestformat=json&wsfunction=" + MoodleOptions.OBTENER_ESCALA + "&scaleid=" + scaleId);
			response = httpclient.execute(httpget);
			String respuesta = EntityUtils.toString(response.getEntity());
			JSONArray jsonArray = new JSONArray(respuesta);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);
				if (jsonObject != null) {
					elements.add(jsonObject.getString("name"));
				}
			}
			scale = new Scale(scaleId, elements);
			course.addScale(scale);
		} catch (IOException e) {
			logger.error("Error al cargar las escalas, no se ha podido conectar con el webService: {}", e);
			throw new IOException("Error al cargar las escalas, no se ha podido conectar con el webService", e);
		} catch (JSONException e) {
			logger.error("Error al cargar las escalas: {}", e);
			throw new JSONException(e);
		} finally {
			if (response != null) {
				response.close();
			}
			httpclient.close();
		}
		return scale.getId();
	}

	/**
	 * Obtiene los datos de las calificaciaones para el alumno y el curso que
	 * recibe.
	 * 
	 * @param host
	 *            El nombre del host.
	 * @param token
	 *            Token del profesor logueado
	 * @param courseId
	 *            Curso del que se quieren cargar los datos
	 * @param userId
	 *            Id del usuario a cargar
	 * @return El JSON con los datos obtenidos.
	 * 
	 * @throws IOException
	 * @throws JSONException
	 */
	private static JSONObject getGradesData(String host, String token, int userId, int courseId)
			throws IOException, JSONException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		JSONObject gradesData = new JSONObject();
		try {
			String call = host + "/webservice/rest/server.php?wstoken=" + token + "&moodlewsrestformat=json&wsfunction="
					+ MoodleOptions.OBTENER_TABLA_NOTAS + "&courseid=" + courseId + "&userid=" + userId;
			HttpGet httpget = new HttpGet(call);
			response = httpclient.execute(httpget);

			gradesData = new JSONObject(EntityUtils.toString(response.getEntity()));
		} catch (IOException e) {
			logger.error("Error al obtener los datos de las escalas, no se ha podido conectar con el webService: {}",
					e);
			throw new IOException("Error al cargar las escalas, no se ha podido conectar con el webService", e);
		} catch (JSONException e) {
			logger.error("Error al obtener los datos de las escalas: {}", e);
			throw new JSONException(e);
		} finally {
			if (response != null) {
				response.close();
			}
			httpclient.close();
		}
		return gradesData;
	}
}
