package es.ubu.lsi.model;

import java.io.Serializable;

/**
 * Clase de bases de datos. Almacena datos de todos los años Encargado de las
 * instaciar varios de las clases del paquete {@link model}. En BBDD mantiene
 * solo una instancía por cada id que haya de cada elemento. Las instancias de
 * cada BBDD no son los mismos a pesar de que tengan los mismos ids.
 * 
 * En la versión actual de la aplicación, el id de los grade item es el orden
 * que devuelve en la funcion de moodle gradereport_user_get_grades_table.
 * 
 * @author Yi Peng Ji
 *
 */
public class DataBase implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private MoodleUser moodleUser;
	private SubDataBase<Role> roles;
	private SubDataBase<Group> groups;
	private SubDataBase<EnrolledUser> users;
	private SubDataBase<CourseModule> modules;
	private SubDataBase<Course> courses;
	private SubDataBase<GradeItem> gradeItems;
	private SubDataBase<CourseCategory> courseCategories;
	private SubDataBase<Section> sections;

	/**
	 * Curso actual del usuario
	 */
	private Course actualCourse;

	public DataBase() {
		roles = new SubDataBase<>(Role::new);
		groups = new SubDataBase<>(Group::new);
		users = new SubDataBase<>(EnrolledUser::new);
		modules = new SubDataBase<>(CourseModule::new);
		courses = new SubDataBase<>(Course::new);
		gradeItems = new SubDataBase<>(GradeItem::new);
		courseCategories = new SubDataBase<>(CourseCategory::new);
		sections = new SubDataBase<>(Section::new);
	}

	/**
	 * Devuelve el curso actual.
	 * 
	 * @return curso actual.
	 */
	public Course getActualCourse() {
		return actualCourse;
	}

	/**
	 * Modifica el curso actual.
	 * 
	 * @param course
	 *            curso actual
	 */
	public void setActualCourse(Course course) {
		actualCourse = course;
	}

	public SubDataBase<Role> getRoles() {
		return roles;
	}

	public SubDataBase<Group> getGroups() {
		return groups;
	}

	public SubDataBase<EnrolledUser> getUsers() {
		return users;
	}

	public SubDataBase<CourseModule> getModules() {
		return modules;
	}

	public SubDataBase<Course> getCourses() {
		return courses;
	}

	public SubDataBase<GradeItem> getGradeItems() {
		return gradeItems;
	}

	public SubDataBase<CourseCategory> getCourseCategories() {
		return courseCategories;
	}

	public SubDataBase<Section> getSections() {
		return sections;
	}

	/**
	 * @return the moodleUser
	 */
	public MoodleUser getMoodleUser() {
		return moodleUser;
	}

	/**
	 * @param moodleUser the moodleUser to set
	 */
	public void setMoodleUser(MoodleUser moodleUser) {
		this.moodleUser = moodleUser;
	}

	

}
