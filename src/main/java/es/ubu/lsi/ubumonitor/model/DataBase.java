package es.ubu.lsi.ubumonitor.model;

import java.io.Serializable;
import java.time.ZoneId;

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

	private byte[] userPhoto;
	private String fullName;
	private ZoneId userZoneId;

	private SubDataBase<Role> roles;
	private SubDataBase<Group> groups;
	private SubDataBase<EnrolledUser> users;
	private SubDataBase<CourseModule> modules;
	private SubDataBase<Course> courses;
	private SubDataBase<GradeItem> gradeItems;
	private SubDataBase<CourseCategory> courseCategories;
	private SubDataBase<Section> sections;
	private SubDataBase<ForumDiscussion> forumDiscussions;
	private SubDataBase<DiscussionPost> discussionPosts;
	private SubDataBase<CourseEvent> courseEvents;

	/**
	 * Curso actual del usuario
	 */
	private Course actualCourse;

	public DataBase() {
		checkSubDatabases();

	}

	public void checkSubDatabases() {

		roles = checkSubDataBase(roles, Role::new);
		groups = checkSubDataBase(groups, Group::new);
		users = checkSubDataBase(users, EnrolledUser::new);
		modules = checkSubDataBase(modules, CourseModule::new);
		courses = checkSubDataBase(courses, Course::new);
		gradeItems = checkSubDataBase(gradeItems, GradeItem::new);
		courseCategories = checkSubDataBase(courseCategories, CourseCategory::new);
		sections = checkSubDataBase(sections, Section::new);
		
		
		forumDiscussions = checkSubDataBase(forumDiscussions, ForumDiscussion::new);
		discussionPosts = checkSubDataBase(discussionPosts, DiscussionPost::new);
		courseEvents = checkSubDataBase(courseEvents, CourseEvent::new);

	}

	public <E extends Serializable> SubDataBase<E> checkSubDataBase(SubDataBase<E> subDataBase,
			SerializableFunction<Integer, E> creatorFunction) {
		if (subDataBase == null) {
			return new SubDataBase<>(creatorFunction);
		}
		return subDataBase;
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
	 * @param course curso actual
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

	public SubDataBase<ForumDiscussion> getForumDiscussions() {
		return forumDiscussions;
	}

	public SubDataBase<DiscussionPost> getDiscussionPosts() {
		return discussionPosts;
	}

	public SubDataBase<CourseEvent> getCourseEvents() {
		return courseEvents;
	}

	/**
	 * @return the userPhoto
	 */
	public byte[] getUserPhoto() {
		return userPhoto;
	}

	/**
	 * @param userPhoto the userPhoto to set
	 */
	public void setUserPhoto(byte[] userPhoto) {
		this.userPhoto = userPhoto;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public ZoneId getUserZoneId() {
		return userZoneId;
	}

	public void setUserZoneId(ZoneId userZoneId) {
		this.userZoneId = userZoneId;
	}

}
