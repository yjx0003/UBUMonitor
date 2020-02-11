package es.ubu.lsi.model;

import java.io.Serializable;

/**
 * Represanta la categoría que pertenece el curso.
 * 
 * @author Yi Peng Ji
 *
 */
public class CourseCategory implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;

	/**
	 * category name
	 */
	private String name;

	/**
	 * category description
	 */
	private String description;

	/**
	 * description format (1 = HTML, 0 = MOODLE, 2 = PLAIN or 4 = MARKDOWN)
	 */
	private DescriptionFormat descriptionFormat;

	/**
	 * parent category id
	 */
	private int parent;

	/**
	 * category sorting order
	 */
	private int sortorder;

	/**
	 * number of courses in this category
	 */
	private int coursecount;

	/**
	 * category depth
	 */
	private int depth;
	/**
	 * category path
	 */
	private String path;

	/**
	 * Constructor inicializando con el id
	 * 
	 * @param id de la categoria del curso
	 */
	public CourseCategory(int id) {
		this.id = id;
	}

	/**
	 * Devuelve el id de la categoria del curso.
	 * 
	 * @return id de categoria
	 */
	public int getId() {
		return id;
	}

	/**
	 * Modifica el id de la categoria.
	 * 
	 * @param id
	 *            id de categoria
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Devuelve el nombre de la categoria.
	 * 
	 * @return nombre de la categoría
	 */
	public String getName() {
		return name;
	}

	/**
	 * Modifica el nombre de la categoría
	 * 
	 * @param name
	 *            nombre de la categoría
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Devuelve la descripción de la categoría.
	 * 
	 * @return descripción de la categoría.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Modifica la descripción.
	 * 
	 * @param description
	 *            nueva descripción
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Devuelve el formato usado en la descripción.
	 * 
	 * @return formato de descripción
	 */
	public DescriptionFormat getDescriptionFormat() {
		return descriptionFormat;
	}

	/**
	 * Modifica el formato de descripción.
	 * 
	 * @param descriptionFormat
	 *            nuevo formato de descripción
	 */
	public void setDescriptionFormat(DescriptionFormat descriptionFormat) {
		this.descriptionFormat = descriptionFormat;
	}

	/**
	 * Devuelve el id de la super categoria.
	 * 
	 * @return id de la supercategoria
	 */
	public int getParent() {
		return parent;
	}

	/**
	 * Modifica el id de la super categoria.
	 * 
	 * @param parent
	 *            id de la supercategoria
	 */
	public void setParent(int parent) {
		this.parent = parent;
	}

	/**
	 * En que orden esta la categoría.
	 * 
	 * @return orden de la categoría
	 */
	public int getSortorder() {
		return sortorder;
	}

	/**
	 * Cambiar el orden de la categoría
	 * 
	 * @param sortorder tipo de ordenacion
	 */
	public void setSortorder(int sortorder) {
		this.sortorder = sortorder;
	}

	/**
	 * Devuelve el número de cursos de la categoría.
	 * 
	 * @return el número de cursos de la categoría
	 */
	public int getCoursecount() {
		return coursecount;
	}

	/**
	 * Modifica el número de cursos de la categoría.
	 * 
	 * @param coursecount
	 *            el nuevo número de cursos
	 */
	public void setCoursecount(int coursecount) {
		this.coursecount = coursecount;
	}

	/**
	 * Nivel de la jerarquía de la categoría.
	 * 
	 * @return nivel de la categoría
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * Modifica el nivel de jerarquía de categoria.
	 * 
	 * @param depth
	 *            nivel de jerarquía
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	/**
	 * Devuelve la ruta de la categoría.
	 * 
	 * @return ruta de la categoría
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Modifica la ruta.
	 * 
	 * @param path
	 *            nueva ruta
	 */
	public void setPath(String path) {
		this.path = path;
	}

	

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CourseCategory))
			return false;
		CourseCategory other = (CourseCategory) obj;
		return id == other.id;
			
	}

	@Override
	public String toString() {
		return name;
	}


}
