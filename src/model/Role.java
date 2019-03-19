package model;

import java.io.Serializable;

/**
 * Clase Role para distinguir el rol de los usuarios matriculados en un curso.
 * 
 * @author Claudia Martínez Herrero
 * @version 2.0.1
 * @since 2.0.1
 */
public class Role implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String shortName;

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
		this.id = id;
		this.name = name;
		this.shortName = shortName;
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
	 * 		El id.
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
	 * 		El nombre.
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
	 * 		El nombre corto.
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	@Override
	public String toString(){
		return name;
	}
}
