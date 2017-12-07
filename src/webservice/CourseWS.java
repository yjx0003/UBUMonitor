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
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.UBUGrades;
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
	 * Establece los usuarios que están matriculados en un curso junto con su
	 * rol y grupo.
	 * 
	 * @param token
	 *            token de usuario
	 * @param idCurso
	 *            id del curso
	 * @throws Exception
	 */
	public static void setEnrolledUsers(String token, Course course) throws Exception {
		logger.info("Obteniendo los usuarios del curso.");
		CloseableHttpClient httpclient = HttpClients.createDefault();
		ArrayList<EnrolledUser> eUsers = new ArrayList<>();
		try {
			HttpGet httpget = new HttpGet(UBUGrades.host + "/webservice/rest/server.php?wstoken=" + token
					+ "&moodlewsrestformat=json&wsfunction=" + MoodleOptions.OBTENER_USUARIOS_MATRICULADOS
					+ "&courseid=" + course.getId());
			CloseableHttpResponse response = httpclient.execute(httpget);
			
			String respuesta = EntityUtils.toString(response.getEntity());
			JSONArray jsonArray = new JSONArray(respuesta);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);
				if (jsonObject != null) {
					eUsers.add(new EnrolledUser(token, jsonObject));
				}
			}
		} catch (IOException e) {
			logger.error("Error de conexion con Moodle al obtener los usuarios matriculados.", e);
			throw new IOException("Error de conexion con Moodle al obtener los usuarios matriculados."
					+ "\n Es posible que su equipo haya perdido la conexion a internet.");
		} catch (Exception e ) {
			logger.error("Se ha producido un error al obtener los usuarios matriculados.", e);
			throw new Exception("Se ha producido un error al obtener los usuarios matriculados.");
		} finally {
			httpclient.close();
		}
		course.setEnrolledUsers(eUsers);
		course.setRoles(eUsers);
		course.setGroups(eUsers);
	}

	/**
	 * Esta función se usará para obtener todos los GradeReportLine del primer usuario
	 * matriculado y así sacar la estructura del calificador del curso para
	 * después mostrarla como TreeView en la vista.
	 * 
	 * @param token
	 *            token del profesor logueado
	 * @param courseId
	 *            curso del que se quieren cargar los datos
	 * @param userId
	 *            id del usuario a cargar
	 * @throws Exception
	 */
	public static void setGradeReportLines(String token, int userId, Course course) throws Exception {
		logger.info("Generando el arbol del calificador.");
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			String call = UBUGrades.host + "/webservice/rest/server.php?wstoken=" + token
					+ "&moodlewsrestformat=json&wsfunction=" + MoodleOptions.OBTENER_TABLA_NOTAS + "&courseid="
					+ course.getId() + "&userid=" + userId;
			HttpGet httpget = new HttpGet(call);
			response = httpclient.execute(httpget);
		
			String respuesta = EntityUtils.toString(response.getEntity());
			JSONObject jsonArray = new JSONObject(respuesta);
			// lista de GradeReportLines
			course.gradeReportLines = new ArrayList<GradeReportLine>();
			// En esta pila sólo van a entrar Categorías. Se mantendrán en
			// la pila mientran tengan descendencia.
			// Una vez añadida al árbol toda la descendencia de un nodo,
			// este nodo se saca de la pila y se añade al árbol.
			Stack<GradeReportLine> deque = new Stack<>();

			if (jsonArray != null) {
				JSONArray tables = (JSONArray) jsonArray.get("tables");
				JSONObject alumn = (JSONObject) tables.get(0);

				JSONArray tableData = alumn.getJSONArray("tabledata");

				// El elemento table data tiene las líneas del configurador
				// (que convertiremos a GradeReportLines)
				for (int i = 0; i < tableData.length(); i++) {
					JSONObject tableDataElement = tableData.getJSONObject(i);
					// sea categoría o item, se saca de la misma manera el
					// nivel del itemname
					JSONObject itemname = tableDataElement.getJSONObject("itemname");
					int actualLevel = getActualLevel(itemname.getString("class"));
					int idLine = getIdLine(itemname.getString("id"));
					// Si es un feedback (item o suma de
					// calificaciones):
					if (tableDataElement.isNull("leader")) {
						String nameContainer = itemname.getString("content");
						String nameLine = "";
						String typeActivity = "";
						boolean typeLine = false;
						// Si es una actividad (assignment o quiz)
						// Se reconocen por la etiqueta "<a"
						if (nameContainer.substring(0, 2).equals("<a")) {
							nameLine = getNameActivity(nameContainer);
							typeActivity = assignmentOrQuizOrForum(nameContainer);
							typeLine = true;
						} else {
							// Si es un item manual o suma de calificaciones
							// Se reconocen por la etiqueta "<span"
							nameLine = getNameManualItemOrEndCategory(nameContainer);
							typeActivity = manualItemOrEndCategory(nameContainer);
							if (typeActivity.equals("ManualItem")) {
								typeLine = true;
							} else if (typeActivity.equals("Category")) {
								typeLine = false;
							}
						}
						// Sacamos la nota (grade)
						JSONObject gradeContainer = tableDataElement.getJSONObject("grade");
						String grade = getNumber(gradeContainer.getString("content"));

						// Sacamos el porcentaje
						JSONObject percentageContainer = tableDataElement.getJSONObject("percentage");
						Float percentage = getFloat(percentageContainer.getString("content"));
						// Sacamos el peso
						JSONObject weightContainer = tableDataElement.optJSONObject("weight");
						Float weight = Float.NaN;
						if (weightContainer != null) {
							weight = getFloat(weightContainer.getString("content"));
						}
						// Sacamos el rango
						JSONObject rangeContainer = tableDataElement.getJSONObject("range");
						String rangeMin = getRange(rangeContainer.getString("content"), true);
						String rangeMax = getRange(rangeContainer.getString("content"), false);
						if (typeLine) { // Si es un item
							// Añadimos la linea actual
							GradeReportLine actualLine = new GradeReportLine(idLine, nameLine, actualLevel,
									typeLine, weight, rangeMin, rangeMax, grade, percentage, typeActivity);
							// Si es un assignment obtenemos la escala si la tiene
							if(typeActivity.equals("Assignment")) {
								Assignment assignment = new Assignment(nameLine, typeActivity, weight, rangeMin, rangeMax);
								assignment.setScaleId(getAssignmentScale(token, course.getId(), nameLine));
								actualLine.setActivity(assignment);
							}
							if (!deque.isEmpty()) {
								deque.lastElement().addChild(actualLine);
							}
							// Añadimos el elemento a la lista como item
							course.gradeReportLines.add(actualLine);
						} else {
							// Obtenemos el elemento cabecera de la pila
							GradeReportLine actualLine = deque.pop();
							// Establecemos los valores restantes
							actualLine.setWeight(weight);
							actualLine.setRangeMin(rangeMin);
							actualLine.setRangeMax(rangeMax);
							actualLine.setNameType(typeActivity);
							actualLine.setGrade(grade);
							// Modificamos la cabecera de esta suma, para
							// dejarla como una categoria completa
							course.updateGRLList(actualLine);
						}
					} else {// --- Si es una categoría
						String nameLine = getNameCategorie(itemname.getString("content"));

						// Añadimos la cabecera de la categoria a la pila
						GradeReportLine actualLine = new GradeReportLine(idLine, nameLine, actualLevel, false);
						// Lo añadimos como hijo de la categoria anterior
						if (!deque.isEmpty()) {
							deque.lastElement().addChild(actualLine);
						}

						// Añadimos esta cabecera a la pila
						deque.add(actualLine);
						// Añadimos el elemento a la lista como cabecera por
						// ahora
						course.gradeReportLines.add(actualLine);
					}
				} // End for
				course.setActivities(course.gradeReportLines);
			} // End if
		} catch (IOException e) {
			logger.error("Error de conexion con Moodle al generar el arbol del calificador.", e);
			throw new IOException("Error de conexion con Moodle al generar el arbol del calificador."
					+ "\n Es posible que su equipo haya perdido la conexion a internet.");
		} catch (Exception e ) {
			logger.error("Se ha producido un error al generar el arbol del calificador.", e);
			throw new Exception("Se ha producido un error al generar el arbol del calificador.");
		} finally {
				response.close();
				httpclient.close();
		}
	}
	
	/**
	 * Genera todos los GradeReportLines de un curso para un usuario.Este proceso se realiza al inicio
	 * alamcenando en memoria los datos.
	 * 
	 * @param token
	 *            token del profesor logueado
	 * @param courseId
	 *            curso del que se quieren cargar los datos
	 * @param userId
	 *            id del usuario a cargar
	 * @return
	 * 		ArrayList con todos los GradeReportLines del usuario.
	 * @throws Exception
	 */
	public static ArrayList<GradeReportLine> getUserGradeReportLines(String token, int userId, int courseId) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		ArrayList<GradeReportLine> gradeReportLines = null;
		try {
			String call = UBUGrades.host + "/webservice/rest/server.php?wstoken=" + token
					+ "&moodlewsrestformat=json&wsfunction=" + MoodleOptions.OBTENER_TABLA_NOTAS + "&courseid="
					+ courseId + "&userid=" + userId;
			HttpGet httpget = new HttpGet(call);
			response = httpclient.execute(httpget);
		
			String respuesta = EntityUtils.toString(response.getEntity());
			JSONObject jsonArray = new JSONObject(respuesta);
			// lista de GradeReportLines
			gradeReportLines = new ArrayList<>();
			// En esta pila sólo van a entrar Categorías. Se mantendrán en
			// la pila mientran tengan descendencia.
			// Una vez añadida al árbol toda la descendencia de un nodo,
			// este nodo se saca de la pila y se añade al árbol.
			Stack<GradeReportLine> deque = new Stack<>();

			if (jsonArray != null) {
				JSONArray tables = (JSONArray) jsonArray.get("tables");
				JSONObject alumn = (JSONObject) tables.get(0);

				JSONArray tableData = alumn.getJSONArray("tabledata");

				// El elemento table data tiene las líneas del configurador
				// (que convertiremos a GradeReportLines)
				for (int i = 0; i < tableData.length(); i++) {
					JSONObject tableDataElement = tableData.getJSONObject(i);
					// sea categoría o item, se saca de la misma manera el
					// nivel del itemname
					JSONObject itemname = tableDataElement.getJSONObject("itemname");
					int actualLevel = getActualLevel(itemname.getString("class"));
					int idLine = getIdLine(itemname.getString("id"));
					// Si es un feedback (item o suma de calificaciones):
					if (tableDataElement.isNull("leader")) {
						String nameContainer = itemname.getString("content");
						String nameLine = "";
						String typeActivity = "";
						boolean typeLine = false;
						// Si es una actividad (assignment o quiz)
						// Se reconocen por la etiqueta "<a"
						if (nameContainer.substring(0, 2).equals("<a")) {
							nameLine = getNameActivity(nameContainer);
							typeActivity = assignmentOrQuizOrForum(nameContainer);
							typeLine = true;
						} else {
							// Si es un item manual o suma de calificaciones
							// Se reconocen por la etiqueta "<span"
							nameLine = getNameManualItemOrEndCategory(nameContainer);
							typeActivity = manualItemOrEndCategory(nameContainer);
							if (typeActivity.equals("ManualItem")) {
								typeLine = true;
							} else if (typeActivity.equals("Category")) {
								typeLine = false;
							}
						}
						// Sacamos la nota (grade)
						JSONObject gradeContainer = tableDataElement.getJSONObject("grade");
						String grade = getNumber(gradeContainer.getString("content"));

						// Sacamos el porcentaje
						JSONObject percentageContainer = tableDataElement.getJSONObject("percentage");
						Float percentage = getFloat(percentageContainer.getString("content"));
						
						// Sacamos el peso
						JSONObject weightContainer = tableDataElement.optJSONObject("weight");
						Float weight = Float.NaN;
						if (weightContainer != null) {
							weight = getFloat(weightContainer.getString("content"));
						}
						// Sacamos el rango
						JSONObject rangeContainer = tableDataElement.getJSONObject("range");
						String rangeMin = getRange(rangeContainer.getString("content"), true);
						String rangeMax = getRange(rangeContainer.getString("content"), false);
						if (typeLine) { // Si es un item
							// Añadimos la linea actual
							GradeReportLine actualLine = new GradeReportLine(idLine, nameLine, actualLevel,
									typeLine, weight, rangeMin, rangeMax, grade, percentage, typeActivity);		
							// Si es un assignment obtenemos la escala si la tiene
							if(typeActivity.equals("Assignment")) {
								Assignment assignment = new Assignment(nameLine, typeActivity, weight, rangeMin, rangeMax);
								assignment.setScaleId(getAssignmentScale(token, courseId, nameLine));
								actualLine.setActivity(assignment);
							}
							if (!deque.isEmpty()) {
								deque.lastElement().addChild(actualLine);
							}
							// Añadimos el elemento a la lista como item
							gradeReportLines.add(actualLine);
						} else {
							// Obtenemos el elemento cabecera de la pila
							GradeReportLine actualLine = deque.pop();
							// Establecemos los valores restantes
							actualLine.setWeight(weight);
							actualLine.setRangeMin(rangeMin);
							actualLine.setRangeMax(rangeMax);
							actualLine.setNameType(typeActivity);
							actualLine.setGrade(grade);
							// Modificamos la cabecera de esta suma, para
							// dejarla como una categoria completa
							
							for (int x = 0; x < gradeReportLines.size(); x++) {
								if(gradeReportLines.get(x).getId() == actualLine.getId())
									gradeReportLines.set(x, actualLine);
							}
						}
					}else {// --- Si es una categoría
						String nameLine = getNameCategorie(itemname.getString("content"));

						// Añadimos la cabecera de la categoria a la pila
						GradeReportLine actualLine = new GradeReportLine(idLine, nameLine, actualLevel, false);
						// Lo añadimos como hijo de la categoria anterior
						if (!deque.isEmpty()) {
							deque.lastElement().addChild(actualLine);
						}

						// Añadimos esta cabecera a la pila
						deque.add(actualLine);
						// Añadimos el elemento a la lista como cabecera por
						// ahora
						gradeReportLines.add(actualLine);
					}
				} // End for
			} // End if			
		} catch (IOException e) {
			logger.error("Error de conexion con Moodle al obtener las notas del alumno.", e);
			throw new IOException("Error de conexion con Moodle al obtener las notas del alumno."
					+ "\n Es posible que su equipo haya perdido la conexion a internet.");
		} catch (Exception e ) {
			logger.error("Se ha producido un error al obtener las notas del alumno.", e);
			throw new Exception("Se ha producido un error al obtener las notas del alumno.");
		} finally {
				response.close();
				httpclient.close();
		}
		return gradeReportLines;
	}

	/**
	 * Genera todos los GradeReportLines de un curso para un usuario.Este proceso se realiza al inicio
	 * alamcenando en memoria los datos. Moodle 3.3
	 * 
	 * @param token
	 *            token del profesor logueado.
	 * @param userId
	 *            id del usuario a cargar.
	 * @param courseId
	 *            curso del que se quieren cargar los datos.
	 * @return
	 * 		ArrayList con todos los GradeReportLines del usuario.
	 * @throws Exception
	 */
	public static ArrayList<GradeReportLine> getUserGradeReportLinesMoodle33(String token, int userId, int courseId) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		ArrayList<GradeReportLine> gradeReportLines = null;
		try {
			String call = UBUGrades.host + "/webservice/rest/server.php?wstoken=" + token
					+ "&moodlewsrestformat=json&wsfunction=" + MoodleOptions.OBTENER_NOTAS_ALUMNO + "&courseid="
					+ courseId + "&userid=" + userId;
			HttpGet httpget = new HttpGet(call);
			response = httpclient.execute(httpget);
			
			String respuesta = EntityUtils.toString(response.getEntity());
			JSONObject jsonArray = new JSONObject(respuesta);
			logger.info("Llamada al webService->OK userID=" + userId + " courseID " + courseId);
			
			//lista de GradeReportLines
			gradeReportLines = new ArrayList<>();
			
			if(jsonArray != null ) {
				logger.info("Parseando datos...");
				JSONArray usergrades = (JSONArray) jsonArray.get("usergrades");
				JSONObject alumn = (JSONObject) usergrades.get(0);
				
				JSONArray gradeItems = alumn.getJSONArray("gradeitems");
				
				// El elemento gradeitems tiene cada linea del calificador
				// que convertiremos en GradeReportLines
				for(int i = 0; i < gradeItems.length(); i++) {
					JSONObject gradeItemsElement = gradeItems.getJSONObject(i);
					
					// Obtenemos los datos necesarios
					int id;
					String name = gradeItemsElement.getString("itemname");
					logger.info("Obteniendo nombre-> " + name);
					String type = gradeItemsElement.getString("itemtype");
					logger.info("Obteniendo tipo-> " + type);
					// Para los tipos categoria y curso hay dos ids
					// Para estos casos obtenemos el id de iteminstance
					if (type.equals("category") || type.equals("course")) {
						id = gradeItemsElement.getInt("iteminstance");
					} else {
						id = gradeItemsElement.getInt("id");
					}
					int rangeMin = gradeItemsElement.getInt("grademin");
					int rangeMax = gradeItemsElement.getInt("grademax");
					logger.info("Obteniendo rangos-> " + rangeMin + "-" + rangeMax);
					String grade = gradeItemsElement.getString("gradeformatted");
					logger.info("Obteniendo nota-> " + grade);
					
					
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
		} catch (Exception e ) {
			logger.error("Se ha producido un error al obtener las notas del alumno.", e);
			throw new Exception("Se ha producido un error al obtener las notas del alumno.");
		} finally {
				response.close();
				httpclient.close();
		}
		return gradeReportLines;
	}

	/**
	 * Devuelve el id del item
	 * 
	 * @param data
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
	 * @return
	 */
	public static String getNameCategorie(String data) {
		String result = "";
		// Busco el final de la cadena única a partir de la cual empieza el
		// nombre de la categoría
		int begin = data.lastIndexOf('>') + 1;
		// El nombre termina al final de todo el texto
		int end = data.length();
		// Me quedo con la cadena entre esos índices
		result = data.substring(begin, end);

		return result;
	}

	/**
	 * Devuelve el nombre de una actividad
	 * 
	 * @param data
	 * @return
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
	 * @return
	 */
	public static String getNameManualItemOrEndCategory(String data) {
		int end = data.indexOf("</span>");
		data = data.substring(0, end);
		int begin = data.lastIndexOf("</i>") + 4;
		return data.substring(begin);
	}

	/**
	 * Devuelve el tipo de un GradeReportLine (actividad o categoría)
	 * 
	 * @param data
	 * @return tipo de línea (cebecera de categoría, suma de calificaciones o
	 *         item)
	 */
	public static boolean getTypeLine(String data) {
		String[] matrix = data.split(" ");
		return matrix[2].equals("item");
	}

	/**
	 * Comprueba si la línea es una suma de calificaciones de categoría (un
	 * cierre de categoría)
	 * 
	 * @param data
	 * @return true si la línea es una suma de calificaciones, false si no
	 */
	public static boolean getBaggtLine(String data) {
		String[] matrix = data.split(" ");
		return matrix[3].equals("baggt");
	}

	/**
	 * Devuelve el rango mínimo o máximo
	 * 
	 * @param data
	 * @param option
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
	 * @param nameContainer
	 * @return 
	 * 		el nombre del assignment
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
	 * @return
	 */
	private static String manualItemOrEndCategory(String data) {
		//FNS 
		if(data.contains("Ítem manual")) {
			return "ManualItem";
		}else if(data.contains("i/agg_sum")
				// added by RMS
				|| data.contains("i/calc") || data.contains("i/agg_mean")
				//FNS
				|| data.contains("Calificación calculada")) {
			return "Category";
		}else {
			return "";
		}
	}

	/**
	 * Devuelve un número en formato Float si se encuentra en la cadena pasada
	 * 
	 * @param data
	 * @return
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
	 * @return
	 */
	private static String getNumber(String data) {
		Pattern pattern = Pattern.compile("[0-9]{1,3},{1}[0-9]{1,2}");
		Matcher match = pattern.matcher(data);
		if (match.find()) {
			return data.substring(match.start(), match.end());
		}
		// Si es un - es que no hay nota, sino es que es un texto de una escala
		return data.equals("-") ?  "NaN": data;
	}
	
	private static int getAssignmentScale(String token, int courseId, String assignmentName) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpget = new HttpGet(UBUGrades.host + "/webservice/rest/server.php?wstoken=" + token
					+ "&moodlewsrestformat=json&wsfunction=" + MoodleOptions.OBTENER_ASSIGNMENTS
					+ "&courseids[]=" + courseId);
			CloseableHttpResponse response = httpclient.execute(httpget);
			
			String respuesta = EntityUtils.toString(response.getEntity());
			JSONObject jsonArray = new JSONObject(respuesta);
			JSONObject coruses = (JSONObject) ((JSONArray) jsonArray.get("courses")).get(0);
			JSONArray assignments = (JSONArray) coruses.get("assignments");
			for (int i = 0; i < assignments.length(); i++) {
				JSONObject assignment = assignments.getJSONObject(i);
				if(assignment.get("name").equals(assignmentName)) {
					int grade = (int) assignment.get("grade");
					// Si la nota es negativa indica el id de la escala
					if(grade<0) {
						grade = Math.abs(grade);
						Scale scale = UBUGrades.session.getActualCourse().getScale(grade);
						return (scale != null) ? scale.getId() : getScale(token, grade);				
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error al obtener la escala de la tarea: {}", e);
		}
		return 0;
	}
	
	private static int getScale(String token, int scaleId) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		Scale scale = null;
		List<String> elements = new ArrayList<>();
		try {
			HttpGet httpget = new HttpGet(UBUGrades.host + "/webservice/rest/server.php?wstoken=" + token
					+ "&moodlewsrestformat=json&wsfunction=" + MoodleOptions.OBTENER_ESCALA
					+ "&scaleid=" + scaleId);
			CloseableHttpResponse response = httpclient.execute(httpget);
			String respuesta = EntityUtils.toString(response.getEntity());
			JSONArray jsonArray = new JSONArray(respuesta);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);
				if (jsonObject != null) {
					elements.add(jsonObject.getString("name"));
				}
			}
			scale = new Scale(scaleId, elements);
			UBUGrades.session.getActualCourse().addScale(scale);
		} catch (Exception e) {
			logger.error("Error al obtener la escala: {}", e);
		}
		return (scale != null) ? scale.getId() : 0;
	}
}
