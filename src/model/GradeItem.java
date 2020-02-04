package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;

/**
 * Clase que indica item de calificación.
 * 
 * @author Yi Peng Ji
 *
 */
public class GradeItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Grade item id
	 */
	private int id;

	/**
	 * Grade item name
	 */
	private String itemname;

	private ModuleType itemModule;
	private CourseModule module;
	private double weightraw;
	private TObjectDoubleMap<EnrolledUser> graderaw;
	private TObjectDoubleMap<EnrolledUser> percentages;
	private double grademin;
	private double grademax;

	private GradeItem father;
	private List<GradeItem> children;

	private int level;

	/**
	 * Constructor sin parametros.
	 */
	public GradeItem() {
		children = new ArrayList<>();
		graderaw = new TObjectDoubleHashMap<>();
		percentages = new TObjectDoubleHashMap<>();
	}

	/**
	 * Constructor inicializando con el id de grade item.
	 * 
	 * @param id id de grade item
	 */
	public GradeItem(int id) {
		this();
		this.id = id;
	}

	/**
	 * Contructor inicializado con el nombre del grade item.
	 * 
	 * @param name el nombre del grade item
	 */
	public GradeItem(String name) {
		this();
		this.itemname = name;
	}

	/**
	 * Devuelve la id del grade item.
	 * 
	 * @return la id del grade item
	 */
	public int getId() {
		return id;
	}

	/**
	 * Modifica la id del grade item.
	 * 
	 * @param id nueva id del grade item
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Devuelve el nombre del grade item.
	 * 
	 * @return nombre del grade item
	 */
	public String getItemname() {
		return itemname;
	}

	/**
	 * Modifica el nombre del grade item.
	 * 
	 * @param itemname nuevo nombre
	 */
	public void setItemname(String itemname) {
		this.itemname = itemname;
	}

	/**
	 * Modulo asociado a este grade item.
	 * 
	 * @return modulo asociado al grade item
	 */
	public CourseModule getModule() {
		return module;
	}

	/**
	 * Modifica el modulo del curso asociado.
	 * 
	 * @param module el nuevo modulo
	 */
	public void setModule(CourseModule module) {
		this.module = module;
	}

	/**
	 * Devuelve el peso calculado.
	 * 
	 * @return el peso calculado
	 */
	public double getWeightraw() {
		return weightraw;
	}

	/**
	 * Modifica el peso calculado.
	 * 
	 * @param weightraw nuevo peso calculado
	 */
	public void setWeightraw(double weightraw) {
		this.weightraw = weightraw;
	}

	/**
	 * Devuelve la nota mínima que se puede sacar en este grade item.
	 * 
	 * @return nota mínima posible
	 */
	public double getGrademin() {
		return grademin;
	}

	/**
	 * Modifica la calificación mínima posible.
	 * 
	 * @param grademin calificación mínima
	 */
	public void setGrademin(double grademin) {
		this.grademin = grademin;
	}

	/**
	 * Devuelve el grado máximo posible del grade item
	 * 
	 * @return el grado máximo posible
	 */
	public double getGrademax() {
		return grademax;
	}

	/**
	 * Modifica el grado máximo posible.
	 * 
	 * @param grademax el nuevo grado maximo posible
	 */
	public void setGrademax(double grademax) {
		this.grademax = grademax;
	}

	/**
	 * Devuelve el padre dentro de la jerarquía del grade item o null si es la raíz.
	 * 
	 * @return el padre dentro de la jerarquía o null si es la raíz
	 */
	public GradeItem getFather() {
		return father;
	}

	/**
	 * Modifica el padre del grade item.
	 * 
	 * @param father el nuevo padre
	 */
	public void setFather(GradeItem father) {
		this.father = father;
	}

	/**
	 * Devuelve los hijos dentro de la jerarquia.
	 * 
	 * @return los hijos dentro de la jearquía
	 */
	public List<GradeItem> getChildren() {
		return children;
	}

	/**
	 * Modifica los hijos del grade item.
	 * 
	 * @param children los nuevos hijos
	 */
	public void setChildren(List<GradeItem> children) {
		this.children = children;
	}

	/**
	 * Añade un hijo del grade item.
	 * 
	 * @param children nuevo hijo
	 */
	public void addChildren(GradeItem children) {
		this.children.add(children);
	}

	/**
	 * Elimina todos los hijos del grade item.
	 */
	public void clearChildren() {
		this.children.clear();
	}

	/**
	 * Devuelve el nivel en que está dentro de la jerarquía de 1 a N.
	 * 
	 * @return nivel dentro de la jeraquía
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Modifica el nivel de la jerarquía.
	 * 
	 * @param level nivel de jearquía
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Añade una nota de un usuario.
	 * 
	 * @param enrolledUser el usuario
	 * @param grade nota del usuario
	 */
	public void addUserGrade(EnrolledUser enrolledUser, double grade) {
		graderaw.put(enrolledUser, grade);

	}

	public void addUserPercentage(EnrolledUser enrolledUser, double percentage) {
		percentages.put(enrolledUser, percentage);
	}

	/**
	 * Devuelve el tipo de modulo.
	 * 
	 * @return el tipo de modulo
	 */
	public ModuleType getItemModule() {
		return itemModule;
	}

	/**
	 * Modifica el tipo de modulo.
	 * 
	 * @param itemModule el nuevo tipo de modulo
	 */
	public void setItemModule(ModuleType itemModule) {
		this.itemModule = itemModule;
	}

	/**
	 * Devuelve todas las notas del los usarios.
	 * 
	 * @return devuelve todas las notas de los usuario
	 */
	public double[] getEnrolledUserPercentages() {
		return percentages.values();
	}

	/**
	 * Devuelve la nota de un usuaro o NaN si no existe
	 * 
	 * @param user usuario que se busca la nota
	 * @return la nota o NaN si no existe
	 */
	public double getEnrolledUserGrade(EnrolledUser user) {
		return graderaw.containsKey(user) ? graderaw.get(user) : Double.NaN;
	}

	/**
	 * Devuelve el porcentaje del usuario
	 * 
	 * @param user usuario
	 * @return el porcentaje o NaN si no existe.
	 */
	public double getEnrolledUserPercentage(EnrolledUser user) {
		return percentages.containsKey(user) ? percentages.get(user) : Double.NaN;
	}

	/**
	 * Normaliza la nota de 0 a 10.
	 * 
	 * @param user la nota de un usuario
	 * @return nota normalizada de 0 a 10 o NaN si grade es NaN
	 */
	public double adjustTo10(EnrolledUser user) {
		double grade = getEnrolledUserGrade(user);

		return adjustTo10(grade);
	}

	/**
	 * Normalizar la nota de 0 a 10.
	 * 
	 * @param grade nota
	 * @return nota normalizada de 0 a 10 o NaN si grade es NaN
	 */
	public double adjustTo10(double grade) {
		if (!Double.isNaN(grade)) {
			// Normalizacion de la nota del alumno en rango [0,10]
			return ((grade - grademin) / (grademax - grademin)) * 10;

		}

		return grade;
	}

	public TObjectDoubleMap<EnrolledUser> getGraderaw() {
		return graderaw;
	}

	public void setGraderaw(TObjectDoubleMap<EnrolledUser> graderaw) {
		this.graderaw = graderaw;
	}

	public TObjectDoubleMap<EnrolledUser> getPercentages() {
		return percentages;
	}

	public void setPercentages(TObjectDoubleMap<EnrolledUser> percentages) {
		this.percentages = percentages;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof GradeItem))
			return false;
		GradeItem other = (GradeItem) obj;
		return id == other.id;

	}

	@Override
	public String toString() {
		return this.itemname;
	}

}
