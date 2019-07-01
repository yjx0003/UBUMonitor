package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import model.mod.Module;
import model.mod.ModuleType;

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
	private Map<Integer, Role> roles;
	private Map<Integer, Group> groups;
	private Map<Integer, EnrolledUser> users;
	private Map<Integer, Module> modules;
	private Map<Integer, Course> courses;
	private Map<Integer, GradeItem> gradeItems;
	private Map<Integer, CourseCategory> courseCategories;

	/**
	 * Curso actual del usuario
	 */
	private Course actualCourse;

	public DataBase() {
		roles = new HashMap<>();
		groups = new HashMap<>();
		users = new HashMap<>();
		modules = new HashMap<>();
		courses = new HashMap<>();
		gradeItems = new HashMap<>();
		courseCategories = new HashMap<>();
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

	/**
	 * Devuelve todos los roles.
	 * 
	 * @return todos los roles
	 */
	public Map<Integer, Role> getRoles() {
		return roles;
	}

	/**
	 * Devuelve un rol por id, si no exisite esa instancia lo crea, lo añade en la
	 * BBDD y devuelve la nueva instancia.
	 * 
	 * @param id
	 *            del rol
	 * @return instancia única con este id
	 */
	public Role getRoleById(int id) {
		return roles.computeIfAbsent(id, key -> new Role(id));
	}

	/**
	 * Comprueba si existe un rol con este id.
	 * 
	 * @param id
	 *            del rol
	 * @return true si existe rol con este id, false en caso contrario
	 */
	public boolean containsRole(int id) {
		return roles.containsKey(id);
	}

	/**
	 * Añade y devuelve la misma instancia si no estaba en BBDD. Si ya existe no se
	 * añade el nuevo y devuelve el anterior.
	 * 
	 * @param role
	 *            nuevo rol
	 * @return el rol
	 */
	public Role putIfAbsentRole(Role role) {
		return roles.putIfAbsent(role.getRoleId(), role);
	}

	// #################################################################################
	/**
	 * Devuelve todos los grupos de BBDD
	 * 
	 * @return todos los grupos
	 */
	public Map<Integer, Group> getGroups() {
		return groups;
	}

	/**
	 * Devuelve un rol por id, si no exisite esa instancia lo crea, lo añade en en
	 * la BBDD y devuelve la nueva instancia.
	 * 
	 * @param id
	 *            del gurpo
	 * @return el grupo
	 */
	public Group getGroupById(int id) {
		return groups.computeIfAbsent(id, key -> new Group(id));
	}

	/**
	 * Comprueba si existe la instancia de grupos a partir del id
	 * 
	 * @param id
	 *            id
	 * @return true si existe el grupo con este id, false en caso contrario
	 */
	public boolean containsGroup(int id) {
		return groups.containsKey(id);
	}

	/**
	 * Añade el grupo si no existe una instancia con ese id y devuelve el mismo
	 * grupo, en caso contrario no se añade y devuelve la anterior instancia del
	 * grupo.
	 * 
	 * @param group
	 *            grupo a añadir
	 * @return la instancia del param si no existe en la BBDD o la instancia
	 *         anterior si ya llevaba
	 */
	public Group putIfAbsentGroup(Group group) {
		return groups.putIfAbsent(group.getGroupId(), group);
	}

	// #################################################################################
	/**
	 * Devuelve todos los usuarios de la BBDD.
	 * 
	 * @return todos los usuarios
	 */
	public Map<Integer, EnrolledUser> getUsers() {
		return users;
	}

	/**
	 * Devuelve el usuario por id.
	 * 
	 * @param id
	 *            id del usuario
	 * @return usuario con ese id
	 */
	public EnrolledUser getEnrolledUserById(int id) {
		return users.computeIfAbsent(id, key -> new EnrolledUser(id));
	}

	/**
	 * Comprueba si contiene un usuario con ese id.
	 * 
	 * @param id
	 *            id del usuario
	 * @return true si contiene, false en caso contrario
	 */
	public boolean containsEnrolledUser(int id) {
		return users.containsKey(id);
	}

	/**
	 * Añade el usuario si no está y devuelve null, si no devuelve el usuario del
	 * param
	 * 
	 * @param user
	 *            usuario
	 * @return la instancia que contenia de usuario si no null
	 */
	public EnrolledUser putIfAbsentEnrolledUser(EnrolledUser user) {
		return users.putIfAbsent(user.getId(), user);
	}

	// #################################################################################
	/**
	 * Devuelve los modulos del curso.
	 * 
	 * @return los modulos del curso
	 */
	public Map<Integer, Module> getModules() {
		return modules;
	}

	/**
	 * Devuelve el modulo del curso por id, si no existe crea una instancia de la
	 * clase {@link model.mod.Module}.
	 * 
	 * @param id
	 *            del modulo del curso
	 * @return el modulo del curso asociado con el id
	 */
	public Module getCourseModuleById(int id) {
		return modules.computeIfAbsent(id, key -> new Module(id));
	}

	/**
	 * Devuelve el modulo del curso a partir de la id, si no existe crea una
	 * instancia segun el {@link ModuleType}}.
	 * 
	 * @param id
	 *            del modulo
	 * @param moduleType
	 *            tipo de modulo que se crea si no existe en BBDD
	 * @return el modulo de ese id
	 */
	public Module getCourseModuleByIdOrCreate(int id, ModuleType moduleType) {
		return modules.computeIfAbsent(id, key -> moduleType.createInstance(id));
	}

	/**
	 * Comporueba si existe una instancia del modulo del curso con este id.
	 * 
	 * @param id
	 *            de modulo
	 * @return true si contiene, falso en caso contrario
	 */
	public boolean containsModule(int id) {
		return modules.containsKey(id);
	}

	/**
	 * Añade el modulo del curso si no existe
	 * 
	 * @param module modulo a añadir
	 * @return null si no existia o el anterior instancia.
	 */
	public Module putIfAbsentModule(Module module) {
		return modules.putIfAbsent(module.getCmid(), module);
	}

	// #################################################################################
	/**
	 * Devuelve todos los cursos.
	 * 
	 * @return todos los cursos
	 */
	public Map<Integer, Course> getCourses() {
		return courses;
	}

	/**
	 * Cmprueba si existe un curso a partir del id.
	 * 
	 * @param id
	 *            del curso
	 * @return true si existe, false en caso contrario
	 */
	public boolean containsCourse(int id) {
		return courses.containsKey(id);
	}

	/**
	 * Devuelve el curso mediante id
	 * 
	 * @param id
	 *            id del curso
	 * @return la instancia del curso si existe, si no crea una nueva
	 */
	public Course getCourseById(int id) {
		return courses.computeIfAbsent(id, key -> new Course(id));
	}

	/**
	 * Añade un curso si no hay instancia.
	 * 
	 * @param course
	 *            el curso a intentar añadir
	 * @return la instancia antigua del curso si existe, en caso cotnrario la nueva
	 */
	public Course putCourse(Course course) {
		return courses.putIfAbsent(course.getId(), course);
	}

	// #################################################################################
	/**
	 * Devuelve todos los grade items
	 * 
	 * @return los grade items
	 */
	public Map<Integer, GradeItem> getGradeItems() {
		return gradeItems;
	}

	/**
	 * Comprueba si existe un grade item a partir del id.
	 * 
	 * @param id
	 *            id del grade item
	 * @return true si contiene, false en caso contrario
	 */
	public boolean containsGradeItem(int id) {
		return gradeItems.containsKey(id);
	}

	public GradeItem getGradeItemById(int id) {
		return gradeItems.computeIfAbsent(id, key -> new GradeItem(id));
	}

	/**
	 * Añade el grade item a la base de datos si no existia anteriormente.
	 * 
	 * @param gradeItem
	 *            el gradeItem que se quiere añadir
	 * @return el valor anterior asociado al id del grade item, null si no contenia.
	 */
	public GradeItem putIfAbsentGradeItem(GradeItem gradeItem) {
		return gradeItems.putIfAbsent(gradeItem.getId(), gradeItem);
	}

	/**
	 * Elimina todos los grade item
	 */
	public void clearGradeItems() {
		gradeItems.clear();
	}

	// #################################################################################

	/**
	 * Devuelve todas las categorias de curso.
	 * 
	 * @return devuelve las categorias del curso
	 */
	public Map<Integer, CourseCategory> getCourseCategories() {
		return courseCategories;
	}

	/**
	 * Comprueba si existe un curso a partir del id.
	 * 
	 * @param id
	 *            id del curso
	 * @return true si existe curso, false en caso contrario
	 */
	public boolean containsCourseCategory(int id) {
		return courseCategories.containsKey(id);
	}

	/**
	 * Devuelve la categoria del curso.
	 * 
	 * @param id
	 *            id de la categoria
	 * @return devuelve la instancia de la categoria si existe, en caso contrario se
	 *         crea una nueva
	 */
	public CourseCategory getCourseCategoryById(int id) {
		return courseCategories.computeIfAbsent(id, key -> new CourseCategory(id));
	}

	/**
	 * Añade una categoria del curso si no existe.
	 * 
	 * @param courseCategory
	 *            categoria del curso
	 * @return la anterior categoria del curso si existe, si no existe devuelve la
	 *         nueva.
	 */
	public CourseCategory putCourseCategory(CourseCategory courseCategory) {
		return courseCategories.putIfAbsent(courseCategory.getId(), courseCategory);
	}

	@Override
	public String toString() {
		return "BBDD [\nroles=" + roles + ",\n groups=" + groups + ",\n users=" + users + ",\n modules=" + modules
				+ ",\n courses=" + courses + ",\n gradeItems=" + gradeItems + ",\n " + "actualCourse=" + actualCourse
				+ "]";
	}
}
