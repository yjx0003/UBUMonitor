package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Clase que representa un usuario matriculado en una asignatura
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class EnrolledUser implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String firstName;
	private String lastName;
	private String fullName;
	private Date firstAccess;
	private Date lastAccess;
	private String description;
	private String descriptionFormat;
	private String city;
	private String country;
	private String profileImageUrlSmall;
	private String profileImageUrl;
	private ArrayList<Role> roles;
	private ArrayList<Group> groups;
	private ArrayList<Integer> courses;
	private ArrayList<GradeReportLine> gradeReportLines;

	/**
	 * Constructor de EnrolledUser
	 * 
	 * @param obj
	 *            objeto JSON con la información del usuario
	 * @throws JSONException 
	 */
	public EnrolledUser(JSONObject obj) throws JSONException {
		this.id = obj.getInt("id");

		this.firstName = obj.getString("firstname");
		this.lastName = obj.getString("lastname");
		this.fullName = obj.getString("fullname");
		this.firstAccess = new Date(obj.getLong("firstaccess") * 1000);
		this.lastAccess = new Date(obj.getLong("lastaccess") * 1000);
		this.profileImageUrl = obj.getString("profileimageurl");
		
		JSONArray roleArray = obj.getJSONArray("roles");
		roles = new ArrayList<>();
		for (int i = 0; i < roleArray.length(); i++) {
			// Establece un rol con el id, name y shortname obtenido de cada
			// JSONObject del JSONArray
			Role rol = new Role(roleArray.getJSONObject(i).getInt("roleid"),
					roleArray.getJSONObject(i).getString("name"),
					roleArray.getJSONObject(i).getString("shortname"));
			roles.add(rol);
		}
		
		JSONArray groupArray = obj.getJSONArray("groups");
		groups = new ArrayList<>();
		for (int i = 0; i < groupArray.length(); i++) {
			// Establece un grupo con el id, name y description obtenido de
			// cada JSONObject del JSONArray
			Group group = new Group(groupArray.getJSONObject(i).getInt("id"),
					groupArray.getJSONObject(i).getString("name"),
					groupArray.getJSONObject(i).getString("description"));
			groups.add(group);
		}
		this.courses = new ArrayList<>();
	}

	/**
	 * Devuelve el id del usuario
	 * 
	 * @return id de usuario matriculado
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Modifica el id del usuario
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Devuelve el nombre del usuario
	 * 
	 * @return firstName
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * Modifica el nombre del usuario
	 * 
	 * @param firstName
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Devuelve el apellido del usuario
	 * 
	 * @return lastName
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * Modifica el apellido del usuario
	 * 
	 * @param lastName
	 */
	public void setlastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Devuelve el nombre completo del usuario
	 * 
	 * @return fullName
	 */
	public String getFullName() {
		return this.fullName;
	}

	/**
	 * Modifica el nombre completo del usuario
	 * 
	 * @param fullName
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * Devuelve el primer acceso del usuario a la plataforma
	 * 
	 * @return firstAccess
	 */
	public Date getFirstAccess() {
		return this.firstAccess;
	}

	/**
	 * Modifica el primer acceso del usuario a la plataforma
	 * 
	 * @param fisrtAccess
	 */
	public void setFirstAccess(Date firstAccess) {
		this.firstAccess = firstAccess;
	}

	/**
	 * Devuelve la última fecha de acceso a la plataforma
	 * 
	 * @return lastAccess
	 */
	public Date getLastAccess() {
		return this.lastAccess;
	}

	/**
	 * Modifica la última fecha de acceso a la plataforma
	 * 
	 * @param lastAccess
	 */
	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}

	/**
	 * Devuelve la descripción del usuario
	 * 
	 * @return description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Modifica la descripción del usuario
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Devuelve el formato de la descripción
	 * 
	 * @return descriptionFormat
	 */
	public String getDescriptionFormat() {
		return this.descriptionFormat;
	}

	/**
	 * Modifica el formato de la descripción
	 * 
	 * @param descriptionFormat
	 */
	public void setDescriptionFormat(String descriptionFormat) {
		this.descriptionFormat = descriptionFormat;
	}

	/**
	 * Devuelve la ciudad del usuario
	 * 
	 * @return city
	 */
	public String getCity() {
		return this.city;
	}

	/**
	 * Modifica la ciudad del usuario
	 * 
	 * @param city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Devuelve el país del usuario
	 * 
	 * @return country
	 */
	public String getCountry() {
		return this.country;
	}

	/**
	 * Modifica el país del usuario
	 * 
	 * @param country
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * Devuelve la url de la foto de usuario en icono
	 * 
	 * @return profileImageUrlSmall
	 */
	public String getProfileImageUrlSmall() {
		return this.profileImageUrlSmall;
	}

	/**
	 * Modifica la url de la foto de usuario en icono
	 * 
	 * @param profileImageUrlSmall
	 */
	public void setProfileImageUrlSmall(String profileImageUrlSmall) {
		this.profileImageUrlSmall = profileImageUrlSmall;
	}

	/**
	 * Devuelve la url de la foto del usuario
	 * 
	 * @return profileImageUrl
	 */
	public String getProfileImageUrl() {
		return this.profileImageUrl;
	}

	/**
	 * Modifica la url de la foto del usuario
	 * 
	 * @param profileImageUrl
	 */
	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	/**
	 * Devuelve la lista de roles que tiene el usuario
	 * 
	 * @return roles
	 */
	public List<Role> getRoles() {
		return this.roles;
	}

	/**
	 * Modifica la lista de roles que tiene el usuario
	 * 
	 * @param roles
	 */
	public void setRoles(List<Role> roles) {
		this.roles.clear();
		for (Role role : roles) {
			this.roles.add(role);
		}
	}

	/**
	 * Devuelve la lista de grupos en los que está el usuario
	 * 
	 * @return groups
	 */
	public List<Group> getGroups() {
		return this.groups;
	}

	/**
	 * Modifica la lista de grupos en los que está el usuario
	 * 
	 * @param groups
	 */
	public void setGroups(List<Group> groups) {
		this.groups.clear();
		for (Group group : groups) {
			this.groups.add(group);
		}
	}

	/**
	 * Devuelve la lista de cursos en los que está matriculado el usuario
	 * 
	 * @return courses
	 */
	public List<Integer> getEnrolledCourses() {
		return this.courses;
	}

	/**
	 * Modifica la lista de cursos en los que está matriculado el usuario
	 * 
	 * @param courses
	 */
	public void setEnrolledCourses(List<Integer> courses) {
		this.courses.clear();
		for (Integer course : courses) {
			this.courses.add(course);
		}
	}
	
	/**
	 * Devuelve un GradeReportLine según el id.
	 * 
	 * @param id
	 * 		El id del GradeReportLine.
	 * @return
	 * 		El GradeReportLine.
	 */
	public GradeReportLine getGradeReportLine(int id) {
		for(GradeReportLine grl : this.gradeReportLines) {
			if(grl.getId() == id) {
				return grl;
			}
		}
		return null;
	}
	
	/**
	 * Devuelve todos los GradeReportLines del usuario.
	 * 
	 * @return
	 * 		La lista de GRL.
	 */
	public List<GradeReportLine> getAllGradeReportLines() {
		return gradeReportLines;
	}
	
	public void setAllGradeReportLines(List<GradeReportLine> gradeReportLines) {
		this.gradeReportLines = (ArrayList<GradeReportLine>) gradeReportLines;
	}

	/**
	 * Convierte el EnrolledUser a un String con su nombre
	 */
	public String toString() {
		return this.getLastName() + ", " + this.getFirstName();
	}
}
