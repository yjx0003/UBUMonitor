package webservice.core;

import java.util.HashSet;
import java.util.Set;

import webservice.ParametersCourseid;
import webservice.WSFunctions;

/**
 * Devuelve los usuarios matriculados en un curso.
 * @author Yi Peng Ji
 *
 */
public class CoreEnrolGetEnrolledUsers extends ParametersCourseid {

	/**
	 * return only users with this capability. This option requires
	 * 'moodle/role:review' on the course context.
	 * https://docs.moodle.org/37/en/Category:Capabilities
	 */
	private String withcapability;

	/**
	 * return only users in this group id. If the course has groups enabled and this
	 * param isn't defined, returns all the viewable users. This option requires
	 * 'moodle/site:accessallgroups' on the course context if the user doesn't
	 * belong to the group.
	 */
	private int groupid;

	/**
	 * return only users with active enrolments and matching time restrictions. This
	 * option requires 'moodle/course:enrolreview' on the course context.
	 */
	private int onlyactive;

	/**
	 * return only the values of these user fields.
	 */
	private Set<String> userfields;

	/**
	 * sql limit from.
	 */
	private int limitfrom;

	/**
	 * maximum number of returned users.
	 */
	private int limitnumber;

	/**
	 * sort by id, firstname or lastname. For ordering like the site does, use
	 * siteorder.
	 */
	private String sortby;

	/**
	 * ASC or DESC
	 */
	private String sortdirection;

	/**
	 * contador de indice de las opciones
	 */
	private int index;

	
	/**
	 * Constructor con solo el id del curso son parametros adicionales de la función de Moodle.
	 * @param courseid id del curso
	 */
	public CoreEnrolGetEnrolledUsers(int courseid) {
		super(courseid);
	}

	/**
	 * 
	 * Constructor privado que recibe un builder con parametros adicionales de la función de Moodle.
	 * @param builder builder
	 */
	private CoreEnrolGetEnrolledUsers(Builder builder) {
		super(builder.courseid);
		withcapability = builder.withcapability;
		groupid = builder.groupid;
		onlyactive = builder.onlyactive;
		userfields = builder.userfields;
		limitfrom = builder.limitfrom;
		limitnumber = builder.limitnumber;
		sortby = builder.sortby;
		sortdirection = builder.sortdirection;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToUrlParameters() {
		super.appendToUrlParameters();
		appendIfNotNull("withcapability", withcapability);
		appendIfNotZero("groupid", groupid);
		appendIfNotZero("onlyactive", onlyactive);
		appendIfNotNull("userfields", userfields);
		appendIfNotZero("limitfrom", limitfrom);
		appendIfNotZero("limitnumber", limitnumber);
		appendIfNotNull("sortby", sortby);
		appendIfNotNull("sortdirection", sortdirection);

	}

	/**
	 * Añadimos a la url si el String de valor se ha inicializado (not null).
	 * @param name nombre de la opción de Moodle
	 * @param value valor
	 */
	private void appendIfNotNull(String name, String value) {
		if (value != null)
			appendToUrlOptions(index++, name, value);
	}

	/**
	 * Añadimos a la url si el valor no es 0.
	 * @param name nombre de la opción de Moodle
	 * @param value valor
	 */
	private void appendIfNotZero(String name, int value) {
		if (value != 0)
			appendToUrlOptions(index++, name, value);
	}

	/**
	 * Añadimos a la url si el Set de valor se ha inicializado (not null).
	 * @param name
	 * @param value
	 */
	private void appendIfNotNull(String name, Set<String> value) {
		if (value != null)
			appendToUrlOptions(index++, name, String.join(",", value));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WSFunctions getWSFunction() {
		return WSFunctions.CORE_ENROL_GET_ENROLLED_USERS;
	}

	
	/**
	 * Devuelve el permiso
	 * @return el permiso 
	 */
	public String getWithcapability() {
		return withcapability;
	}

	/**
	 * Modifica el permiso
	 * @param withcapability permiso
	 */
	public void setWithcapability(String withcapability) {
		this.withcapability = withcapability;
	}

	/**
	 * Id del grupo
	 * @return id del grupo
	 */
	public int getGroupid() {
		return groupid;
	}

	/**
	 * Id del grupo
	 * @param groupid id del grupo
	 */
	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}

	/**
	 * Tiempo que han estado activos.
	 * @return el tiempo que han estado activos
	 */
	public int getOnlyactive() {
		return onlyactive;
	}

	/**
	 * Modifica el tiempo que han estado activos un usuario.
	 * @param onlyactive tiempo
	 */
	public void setOnlyactive(int onlyactive) {
		this.onlyactive = onlyactive;
	}

	/**
	 * Conjunto de campos que se recupera.
	 * @return conjunto de campos
	 */
	public Set<String> getUserfields() {
		return userfields;
	}

	/**
	 * Modifica los campos que se recupera en la llamada a Moodle.
	 * @param userfields los campos
	 */
	public void setUserfields(Set<String> userfields) {
		this.userfields = userfields;
	}

	/**
	 * Devuelve el limite de sql asignado
	 * @return limite de sql
	 */
	public int getLimitfrom() {
		return limitfrom;
	}

	/**
	 * Modifica el limite de sql.
	 * @param limitfrom el limite
	 */
	public void setLimitfrom(int limitfrom) {
		this.limitfrom = limitfrom;
	}

	/**
	 * Devuelve el número maximo de usuarios que da como respuesta la función de Moodle.
	 * @return limite maximo de usuarios.
	 */
	public int getLimitnumber() {
		return limitnumber;
	}

	/**
	 * Modifica el límite de usuarios.
	 * @param limitnumber límite de usuarios
	 */
	public void setLimitnumber(int limitnumber) {
		this.limitnumber = limitnumber;
	}

	/**
	 * Tipo de ordenación: por id, nombre, apellido, etc.
	 * @return tipo de ordenacion
	 */
	public String getSortby() {
		return sortby;
	}

	/**
	 * Modifica el tipo de ordenación.
	 * @param sortby tipo de ordenación: por id, nombre, apellido, etc
	 */
	public void setSortby(String sortby) {
		this.sortby = sortby;
	}

	/**
	 * Devuelve ASC o DESC. En que orden es la respuesta de moodle.
	 * @return ASC o  DESC
	 */
	public String getSortdirection() {
		return sortdirection;
	}

	/**
	 * Modifica el orden de respuesta.
	 * @param sortdirection ASC o DESC 
	 */
	public void setSortdirection(String sortdirection) {
		this.sortdirection = sortdirection;
	}

	/**
	 * Devuelve un Builder para añadir parámetros adicionales a la función de Moodle.
	 * @param courseid id del curso
	 * @return Builder
	 */
	public static Builder newBuilder(int courseid) {
		return new Builder(courseid);
	}

	/**
	 * 
	 * Constructor builder para añadir parámetros adicionales a la función de Moodle.
	 * @author Yi Peng Ji
	 *
	 */
	public static class Builder {

		private int courseid;
		private String withcapability;
		private int groupid;
		private int onlyactive;
		private Set<String> userfields;
		private int limitfrom;
		private int limitnumber;
		private String sortby;
		private String sortdirection;

		public Builder(int courseid) {
			this.courseid = courseid;
		}

		public Builder setGroupid(int groupid) {
			this.groupid = groupid;
			return this;
		}

		public Builder setWithcapability(String withcapability) {
			this.withcapability = withcapability;
			return this;
		}

		public Builder setOnlyactive(int onlyactive) {

			this.onlyactive = onlyactive;
			return this;
		}

		public Builder setUserfields(Set<String> userfields) {
			this.userfields = userfields;
			return this;
		}

		public Builder addUserfield(String userfield) {
			if (userfield == null)
				userfields = new HashSet<>();
			userfields.add(userfield);
			return this;
		}

		public Builder setLimitfrom(int limitfrom) {
			this.limitfrom = limitfrom;
			return this;
		}

		public Builder setLimitnumber(int limitnumber) {
			this.limitnumber = limitnumber;
			return this;
		}

		public Builder setSortbyId() {
			this.sortby = "id";
			return this;
		}

		public Builder setSortbyFirstname() {
			this.sortby = "firstname";
			return this;
		}

		public Builder setSortbyLastname() {
			this.sortby = "lastname";
			return this;
		}

		public Builder setSortbySiteorder() {
			this.sortby = "siteorder";
			return this;
		}

		public Builder setSortdirectionASC() {
			this.sortdirection = "ASC";
			return this;
		}

		public Builder setSortdirectionDESC() {
			this.sortdirection = "DESC";
			return this;
		}

		public CoreEnrolGetEnrolledUsers build() {

			return new CoreEnrolGetEnrolledUsers(this);

		}

	}
}
