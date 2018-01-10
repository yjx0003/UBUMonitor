package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.Scale;

/**
 * Clase curso (asignatura). Cada curso tiene un calificador (compuesto por
 * líneas de calificación o GradeReportLines); y varios grupos, roles de
 * participantes, y tipos de actividades.
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class Course implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String shortName;
	private String fullName;
	private int enrolledUsersCount;
	private String idNumber;
	private String summary;
	private List<EnrolledUser> enrolledUsers;
	private Set<String> roles; // roles que hay en el curso
	private Set<String> groups; // grupos que hay en el curso
	private GradeReportLine gradeReportLines;
	private Set<String> typeActivities;
	private List<Scale> scales;

	static final Logger logger = LoggerFactory.getLogger(Course.class);

	public Course() {
		this.enrolledUsers = new ArrayList<>();
		this.scales = new ArrayList<>();
	}

	/**
	 * Constructor de un curso a partir de contenido JSON. Establece los
	 * parámetros de un curso.
	 * 
	 * @param obj
	 *            objeto JSON con la información del curso
	 * @throws JSONException 
	 */
	public Course(JSONObject obj) throws JSONException {
		this.id = obj.getInt("id");
		if (obj.getString("shortname") != null)
			this.shortName = obj.getString("shortname");
		if (obj.getString("fullname") != null)
			this.fullName = obj.getString("fullname");
		if (obj.getInt("enrolledusercount") != 0)
			this.enrolledUsersCount = obj.getInt("enrolledusercount");
		if (obj.getString("idnumber") != null)
			this.idNumber = obj.getString("idnumber");
		if (obj.getString("summary") != null)
			this.summary = obj.getString("summary");
		this.enrolledUsers = new ArrayList<>();
		this.scales = new ArrayList<>();
	}

	/**
	 * Devuelve el id del curso.
	 * 
	 * @return id del curso
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Modifica el id del curso.
	 * 
	 * @param id
	 * 		El id.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Devuelve el nombre corto del curso.
	 * 
	 * @return shortName
	 */
	public String getShortName() {
		return this.shortName;
	}

	/**
	 * Modifica el nombre corto del curso.
	 * 
	 * @param shortName
	 * 		El nombre corto.
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * Devuelve el nombre del curso.
	 * 
	 * @return fullName
	 */
	public String getFullName() {
		return this.fullName;
	}

	/**
	 * Modifica el nombre del curso.
	 * 
	 * @param fullName
	 * 		El nombre completo-
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * Devuelve el nº de usuarios del curso.
	 * 
	 * @return enrolledUsersCount
	 */
	public int getEnrolledUsersCount() {
		return this.enrolledUsersCount;
	}

	/**
	 * Modifica el nº de usuarios del curso.
	 * 
	 * @param enrolledUserCount
	 * 		El número de usuarios.
	 */
	public void setEnrolledUsersCount(int enrolledUserCount) {
		this.enrolledUsersCount = enrolledUserCount;
	}

	/**
	 * Devuelve el idNumber del curso
	 * 
	 * @return idNumber
	 */
	public String getIdNumber() {
		return this.idNumber;
	}

	/**
	 * Modifica el idNumber del curso.
	 * 
	 * @param idNumber
	 * 		El id del curso.
	 */
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	/**
	 * Devuelve el resumen del curso.
	 * 
	 * @return summary
	 */
	public String getSummary() {
		return this.summary;
	}

	/**
	 * Modifica el resumen del curso.
	 * 
	 * @param summary
	 * 		El resumen.
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * Devuelve una lista de los usuarios matriculados en el curso.
	 * 
	 * @return lista de usuarios
	 */
	public List<EnrolledUser> getEnrolledUsers() {
		Collections.sort(this.enrolledUsers, (o1, o2) -> o1.getLastName().compareTo(o2.getLastName()));
		return this.enrolledUsers;
	}

	/**
	 * Modifica la lista de usuarios matriculados en el curso
	 * 
	 * @param eUsers
	 * 		La lista de usuarios matriculados.
	 */
	public void setEnrolledUsers(List<EnrolledUser> eUsers) {
		this.enrolledUsers.clear();
		for (EnrolledUser eUser : eUsers) {
			this.enrolledUsers.add(eUser);
		}
		setEnrolledUsersCount(this.enrolledUsers.size());
	}

	/**
	 * Devuelve los roles que hay en el curso.
	 * 
	 * @return lista de roles del curso
	 */
	public List<String> getRoles() {
		ArrayList<String> result = new ArrayList<>();
		Iterator<String> roleIt = this.roles.iterator();
		while (roleIt.hasNext()) {
			String data = roleIt.next();
			if (data != null && !data.trim().equals(""))
				result.add(data);
		}
		return result;
	}

	/**
	 * Almacena en un set los roles que hay en el curso.
	 * 
	 * @param users
	 * 		Usuarios matriculados en el curso.
	 */
	public void setRoles(List<EnrolledUser> users) {
		// Creamos el set de roles
		roles = new HashSet<>();
		// Recorremos la lista de usuarios matriculados en el curso
		for (int i = 0; i < users.size(); i++) {
			// sacamos el rol del usuario
			ArrayList<Role> roleArray = (ArrayList<Role>) users.get(i).getRoles();
			// cada rol nuevo se añade al set roles
			for (int j = 0; j < roleArray.size(); j++) {
				roles.add(roleArray.get(j).getName());
			}
		}
	}

	/**
	 * Devuelve los grupos que hay en el curso.
	 * 
	 * @return lista de grupos del curso
	 */
	public List<String> getGroups() {
		ArrayList<String> result = new ArrayList<>();
		Iterator<String> groupsIt = this.groups.iterator();
		while (groupsIt.hasNext()) {
			String data = groupsIt.next();
			if (data != null && !data.trim().equals(""))
				result.add(data);
		}
		return result;
	}

	/**
	 * Almacena en una lista los grupos que hay en un curso, a partir de los
	 * usuarios que están matriculados.
	 * 
	 * @param users
	 * 		 Usuarios del curso.
	 */
	public void setGroups(List<EnrolledUser> users) {
		// Creamos el set de grupos
		groups = new HashSet<>();
		// Recorremos la lista de usuarios matriculados en el curso
		for (int i = 0; i < users.size(); i++) {
			// Sacamos el grupo del usuario
			ArrayList<Group> groupsArray = (ArrayList<Group>) users.get(i).getGroups();
			// Cada grupo nuevo se añade al set de grupos
			for (int j = 0; j < groupsArray.size(); j++) {
				groups.add(groupsArray.get(j).getName());
			}
		}
	}

	/**
	 * Devuelve la lista de gradeReportLines que hay en el curso. (El
	 * calificador)
	 * 
	 * @return lista de gradeReportConfigurationLines.
	 */
	public List<GradeReportLine> getGradeReportLines() {
		List<GradeReportLine> listGRL = new ArrayList<>();
		listGRL.add(gradeReportLines);
		listGRL.addAll(getChildrens(gradeReportLines.getChildren()));
		
		return listGRL;
	}

	/**
	 * Función auxiliar para obtener los hijos de cada GRL de forma recursiva.
	 * 
	 * @param children
	 * 		La lista de hijos.
	 * @return
	 * 		La lista de hijos de cada GRL.
	 * 
	 */
	private List<GradeReportLine> getChildrens(List<GradeReportLine> children) {
		List<GradeReportLine> listGRL = new ArrayList<>();
		for(GradeReportLine grl: children) {
			listGRL.add(grl);
			if (grl.getChildren() != null) {
				listGRL.addAll(getChildrens(grl.getChildren()));
			}
		}
		return listGRL;
	}

	/**
	 * Establece la lista de gradeReportLines del curso (el calificador).
	 * 
	 * @param grcl
	 * 		EL gradeReportLine.
	 */
	public void setGradeReportLine(GradeReportLine grcl) {
		gradeReportLines = grcl;
	}

	/**
	 * Devuelve las actividades que hay en el curso.
	 * 
	 * @return lista de actividades
	 */
	public List<String> getActivities() {
		ArrayList<String> result = new ArrayList<>();
		Iterator<String> grclIt = this.typeActivities.iterator();
		while (grclIt.hasNext()) {
			String data = grclIt.next();
			if (data != null && !data.trim().equals(""))
				result.add(data);
		}
		return result;
	}

	/**
	 * Almacena en un set las actividades que hay en el curso.
	 * 
	 * @param grcl
	 * 		gradeReportConfigurationLines.
	 */
	public void setActivities(List<GradeReportLine> grcl) {
		// Creamos el set de roles
		typeActivities = new HashSet<>();
		// Recorremos la lista de usuarios matriculados en el curso
		for (int i = 0; i < grcl.size(); i++) {
			typeActivities.add(grcl.get(i).getNameType());
		}
	}


	/**
	 * Devuelve el curso de la lista cuyo nombre coincide con la cadena pasada.
	 * 
	 * @param courseList
	 * 		La lista de cursos.
	 * @param courseName
	 * 		El nombre del curso.
	 * @return
	 * 		EL curso.
	 */
	public static Course getCourseByString(List<Course> courseList, String courseName) {
		Course course = null;
		
		for (int i = 0; i < courseList.size(); i++) {
			if (courseList.get(i).getFullName().equals(courseName)) {
				course = courseList.get(i);
			}
		}

		return course;
	}
	
	/**
	 * Devuleve los usuarios en un grupo.
	 *  
	 * @param groupname
	 * 		El nombre del grupo.
	 * @return
	 * 		ArrayList con los usuarios de ese grupo.
	 */
	public List<EnrolledUser> getUsersInGroup(String groupname) {
		ArrayList<EnrolledUser> groupUsers = new ArrayList<>();
		
		for(EnrolledUser user: enrolledUsers) {
			for(Group group: user.getGroups()) {
				if(group.getName().equals(groupname)) {
					groupUsers.add(user);
					break;
				}
			}
		}
		
		return groupUsers;
	}
	
	/**
	 * Añade una escala a la lista de escalas.
	 * @param scale
	 * 		La escala a añadadir.
	 */
	public void addScale(Scale scale) {
		scales.add(scale);
	}
	
	/**
	 * Devuelve una escala por su id.
	 * 
	 * @param id
	 * 		El id de la escala.
	 * @return
	 * 		La escala o null si no existe.
	 */
	public Scale getScale(int id) {
		for(Scale scale: scales) {
			if (scale.getId() == id) {
				return scale;
			}
		}
		return null;
	}
	
	/**
	 * Devuleve el GradeReportLine cuyo nombre coincide con la cadena pasada.
	 * 
	 * @param elementName
	 * 		El nombre del GRL a buscar.
	 * @return
	 * 		El GradeReportLine o null si no se encuentra.
	 */
	public GradeReportLine getGradeReportLineByName(String elementName) {
		for(GradeReportLine grl: getGradeReportLines()) {
			if(grl.getName().equals(elementName)) {
				return grl;
			}
		}
		return null;
	}
}
