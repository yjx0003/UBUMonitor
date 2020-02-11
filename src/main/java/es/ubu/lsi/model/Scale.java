package model;

import java.io.Serializable;
import java.util.List;

/**
 * Clase Scale para almacenar las escalas.
 * No se usa en esta versión.
 * @author Félix Nogal Santamaría
 * @version 1.0
 *
 */
public class Scale implements Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * Id de la escala
	 */
	private int id;
	/**
	 * Lista de elementos de la escala ordenados de menor a mayor valor.
	 */
	private List<String> elements;
	
	/**
	 * Constructor de Scale.
	 * 
	 * @param id
	 * 		El id de la escala.
	 * @param elements
	 * 		Los elementos de la escala.
	 */
	public Scale(int id, List<String> elements) {
		this.id = id;
		this.elements = elements;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the elements
	 */
	public List<String> getElements() {
		return elements;
	}

	/**
	 * @param elements the elements to set
	 */
	public void setElements(List<String> elements) {
		this.elements = elements;
	}
}
