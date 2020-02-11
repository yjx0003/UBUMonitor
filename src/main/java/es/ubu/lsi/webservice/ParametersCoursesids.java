package webservice;

import java.util.HashSet;
import java.util.Set;


/**
 * Clase abstracta que añade parametros query de ids de cursos a la URL de Moodle. 
 * Ejemplo para añadir cursos con id 1 e id 2: {@literal &courseids[]=1&courseids[]=2}
 * 
 * @author Yi Peng Ji
 *
 */
public abstract class ParametersCoursesids extends WebService {

	
	/**
	 * Un set de los ids de cursos que se quieren añadir al query
	 */
	private Set<Integer> coursesids;

	/**
	 * Constructor que recibe un set de los ids de cursos
	 * @param coursesIds set de ids de cursos
	 */
	public ParametersCoursesids(Set<Integer> coursesIds) {
		this.coursesids = coursesIds;

	}

	/**
	 * Constructor vacío sin pasar ids de cursos
	 */
	public ParametersCoursesids() {
		coursesids = new HashSet<>();

	}
	/**
	 * Constructor de un solo id de curso.
	 * @param courseid id del curso
	 */
	public ParametersCoursesids(int courseid) {
		this();
		coursesids.add(courseid);

	}

	/**
	 * Añadimos los ids de cursos a la URL
	 */
	@Override
	public void appendToUrlParameters() {

		appendToUrlCoursesids(coursesids);

	}

	/**
	 * 
	 * Añade un id del curso
	 * @param courseId id del curso
	 */
	public void addCourseId(int courseId) {
		coursesids.add(courseId);
	}
	
	/**
	 * Eliminamos un id del curso
	 * @param courseId id del curso a eliminar
	 */
	public void removeCourseId(int courseId) {
		coursesids.remove(courseId);
	}

}
