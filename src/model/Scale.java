package model;

import java.util.List;

public class Scale {
	
	private int id;
	private List<String> elements;
	
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
