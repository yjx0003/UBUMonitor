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
	
	private int roleId;
	private String roleName;
	private String roleShortName;
	private Set<EnrolledUser> enrolledUsers;
	
	public Role() {
		this.enrolledUsers=new HashSet<>();
	}
	

	public Role(int id) {
		this();
		setRoleId(id);
	}

	/**
	 * Devuelve el id del rol
	 * 
	 * @return id
	 */
	public int getRoleId() {
		return roleId;
	}

	/**
	 * Modifica el id del rol
	 * 
	 * @param id
	 *            El id.
	 */
	public void setRoleId(int id) {
		this.roleId = id;
	}

	/**
	 * Devuelve el nombre del rol
	 * 
	 * @return name
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * Modifica el nombre del rol.
	 * 
	 * @param name
	 *            El nombre.
	 */
	public void setRoleName(String name) {
		this.roleName = name;
	}

	/**
	 * Devuelve el nombre corto del rol
	 * 
	 * @return shortName
	 */
	public String getRoleShortName() {
		return roleShortName;
	}

	/**
	 * Modifica el nombre corto del rol
	 * 
	 * @param shortName
	 *            El nombre corto.
	 */
	public void setRoleShortName(String shortName) {
		this.roleShortName = shortName;
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
	
	public void removeEnrolledUser(EnrolledUser user) {
		enrolledUsers.remove(user);
	}
	
	public boolean contains(EnrolledUser e) {
		return enrolledUsers.contains(e);
	}

	public void clear() {
		enrolledUsers.clear();
	}

	@Override
	public String toString() {
		return roleName;
	}

	@Override
	public int hashCode() {
		return roleId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Role))
			return false;
		Role other = (Role) obj;
		return roleId == other.roleId;
		
	}




}
