package model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Clase Group para distinguir los grupos que hay en un curso, así como los
 * grupos en los que se encuentra un usuario.
 * 
 * @author Claudia Martínez Herrero
 * @author Yi Peng Ji
 * @version 2.0.1
 * @since 1.0
 */
public class Group implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String description;
	private DescriptionFormat descriptionFormat;

	private Set<EnrolledUser> enrolledUsers;

	public Group() {
		this.enrolledUsers = new HashSet<>();
	}


	public Group(int id) {
		this();
		setId(id);
	}

	/**
	 * Devuelve el id del grupo.
	 * 
	 * @return id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Modifica el id del grupo.
	 * 
	 * @param id
	 *            El id.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Devuelve el nombre del grupo.
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Modifica el nombre del grupo.
	 * 
	 * @param name
	 *            El nombre.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Devuelve la descripción del grupo.
	 * 
	 * @return description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Modifica la descripción del grupo.
	 * 
	 * @param description
	 *            La descripción.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Devuelve el formato que tiene la descripcion.
	 * 
	 * @return formato de descripcion
	 */
	public DescriptionFormat getDescriptionFormat() {
		return descriptionFormat;
	}

	/**
	 * Modifica que formato de descripción usa.
	 * 
	 * @param descriptionFormat
	 *            formato de descripcion
	 */
	public void setDescriptionFormat(DescriptionFormat descriptionFormat) {
		this.descriptionFormat = descriptionFormat;
	}

	/**
	 * Devuelve los usuarios del grupo.
	 * 
	 * @return los usuarios del grupo
	 */
	public Set<EnrolledUser> getEnrolledUsers() {
		return enrolledUsers;
	}

	/**
	 * Modifica los usuarios del grupo.
	 * 
	 * @param enrolledUsers
	 *            modifica los usuarios del grupo
	 */
	public void setEnrolledUsers(Set<EnrolledUser> enrolledUsers) {
		this.enrolledUsers = enrolledUsers;
	}

	/**
	 * Añade un usuario al grupo.
	 * 
	 * @param user
	 *            nuevo usuario
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
		if (!(obj instanceof Group))
			return false;
		Group other = (Group) obj;
		return id == other.id;
	
	}

}
