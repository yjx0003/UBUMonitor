package model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Clase Role para distinguir el rol de los usuarios matriculados en un curso.
 * 
 * @author Claudia Martínez Herrero
 * @version 2.0.1
 * @since 2.0.1
 */
public class Role implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String shortName;
	private Set<EnrolledUser> enrolledUsers;

	public Role() {
		this.enrolledUsers=new HashSet<>();
	}
	
	/**
	 * Constructor de un rol.
	 * 
	 * @param id
	 *            id del rol
	 * @param name
	 *            nombre del rol
	 * @param shortName
	 *            nombre corto
	 */
	public Role(int id, String name, String shortName) {
		this();
		this.id = id;
		this.name = name;
		this.shortName = shortName;
		this.enrolledUsers = new HashSet<>();

	}

	public Role(int id) {
		this();
		this.id = id;
	}

	/**
	 * Devuelve el id del rol
	 * 
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Modifica el id del rol
	 * 
	 * @param id
	 *            El id.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Devuelve el nombre del rol
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Modifica el nombre del rol.
	 * 
	 * @param name
	 *            El nombre.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Devuelve el nombre corto del rol
	 * 
	 * @return shortName
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * Modifica el nombre corto del rol
	 * 
	 * @param shortName
	 *            El nombre corto.
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * Devuelve los usuarios matriculados.
	 * @return los usuarios matriculados
	 */
	public Set<EnrolledUser> getEnrolledUsers() {
		return enrolledUsers;
	}

	/**
	 * Modifica los usuarios que tiene este rol.
	 * @param enrolledUsers los nuevos usarios de este rol
	 */
	public void setEnrolledUsers(Set<EnrolledUser> enrolledUsers) {
		this.enrolledUsers = enrolledUsers;
	}

	/**
	 * Añade un usuario a este rol.
	 * @param user nuevo usuario a añadir
	 */
	public void addEnrolledUser(EnrolledUser user) {
		enrolledUsers.add(user);
	}


	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Role))
			return false;
		Role other = (Role) obj;
		return id == other.id;
		
	}

}
