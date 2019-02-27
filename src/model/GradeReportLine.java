package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import webservice.CourseWS;

/**
 * Clase GradeReportLine (GRL). Representa a una línea en el calificador de un
 * alumno. Cada línea se compone de su id, nombre, nivel, nota, porcentaje,
 * peso, rango y tipo principalmente.
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class GradeReportLine implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private int level;
	private String grade;
	private float percentage;
	private float weight;
	private String rangeMin;
	private String rangeMax;
	private boolean type; // False = Category, True = Item
	private String typeName;
	private ArrayList<GradeReportLine> children;
	private Activity activity;

	/**
	 * Contructor GRL con 4 parámetros. Le añadimos solo los necesarios a la hora de generar el grafico de lineas.
	 * 
	 * @param id
	 * 		Id de la categoría.
	 * @param name
	 * 		Nombre de la categoria.
	 * @param grade
	 * 		La nota del elemento.
	 * @param rangeMin
	 * 		Rango minimo de la calificación.
	 * @param rangeMax
	 * 		Rango maximo de la calificaión.
	 */
	public GradeReportLine(int id, String name, String grade, String rangeMin, String rangeMax) {
		this.id = id;
		this.name = name;
		this.grade = grade;
		this.rangeMin = rangeMin;
		this.rangeMax = rangeMax;
	}
	
	/**
	 * Constructor GRL con 4 parámetros. Se le añaden a posteriori los elementos
	 * de su suma de calificaciones.
	 * 
	 * @param id
	 * 		Id de la categoría.
	 * @param name
	 * 		Nombre de la categoría.
	 * @param level
	 * 		Nivel de profundidad.
	 * @param type
	 * 		Categoría o item.
	 */
	public GradeReportLine(int id, String name, int level, boolean type) {
		this.id = id;
		this.name = name;
		this.level = level;
		this.type = type;
		this.children = new ArrayList<>();
	}

	/**
	 * Constructor de GRL con 7 parámetros.
	 * 
	 * @param id
	 * 		Id de la categoría.
	 * @param name
	 * 		Nombre de la categoría.
	 * @param level
	 * 		Nivel de profundidad.
	 * @param type
	 * 		Categoría o item.
	 * @param weight
	 * 		Peso.
	 * @param rangeMin
	 * 		Rango mínimo de calificación.
	 * @param rangeMax
	 * 		Rango máximo de calificación.
	 * @param grade
	 * 		La Nota.
	 * @param percentage
	 * 		El porcentaje de la actividad.
	 * @param nameType
	 * 		El tipo de actividad.
	 */
	public GradeReportLine(int id, String name, int level, boolean type, float weight, String rangeMin, String rangeMax,
			String grade, float percentage, String nameType) {
		this.id = id;
		this.name = name;
		this.level = level;
		this.weight = weight;
		this.type = type;
		this.rangeMax = rangeMax;
		this.rangeMin = rangeMin;
		this.grade = grade;
		this.percentage = percentage;
		this.typeName = nameType;
		this.children = new ArrayList<>();
	}

	/**
	 * Devuelve el id del GradeReportLine.
	 * 
	 * @return id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Modifica el id del GradeReportLine.
	 * 
	 * @param id
	 * 		El id.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Devuelve el nombre del GradeReportLine.
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Modifica el nombre del GradeReportLine.
	 * 
	 * @param name
	 * 		El nombre.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Devuelve el nivel del GradeReportLine en el árbol del calificador.
	 * 
	 * @return level
	 */
	public int getLevel() {
		return this.level;
	}

	/**
	 * Modifica el nivel del GradeReportLine en el árbol del calificador.
	 * 
	 * @param level
	 * 		El nivel.
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Devuelve la nota del GradeReportLine.
	 * 
	 * @return grade
	 */
	public String getGrade() {
		return grade;
	}
	
	/**
	 * Devuelve la nota del GradeReportLine ajustada al rango de 0 a 10.
	 * 
	 * @return
	 * 		La nota ajustada.
	 */
	public String getGradeAdjustedTo10() {
		Float gradeAdjusted = CourseWS.getFloat(grade);
		if (!Float.isNaN(gradeAdjusted)) {
			Float newRangeMax = Float.parseFloat(rangeMax);
			Float newRangeMin = Float.parseFloat(rangeMin);
			
			newRangeMax -= newRangeMin;
			gradeAdjusted -= newRangeMin;
			
			gradeAdjusted = (float) ((gradeAdjusted*10.0) / newRangeMax); 	
		}
		return Float.toString(gradeAdjusted);
	}
	
	/**
	 * Devuelve la nota de una escala ajustada al rango de 0 a 10.
	 * 
	 * @param course
	 * 		El curso del que obtener la escala.
	 * 
	 * @return
	 * 		La nota ajustada.
	 */
	public String getGradeWithScale(Course course) {
		Float gradeAdjusted;
		int scaleId;
		
		GradeReportLine grl = course.getGradeReportLineByName(name);
		scaleId = ((Assignment) grl.getActivity()).getScaleId();
				
		if (scaleId != 0 && !grade.equals("NaN")) {
			Scale scale = course.getScale(scaleId);
			gradeAdjusted = (float) scale.getElements().indexOf(this.grade);
			Float newRangeMax = (float) scale.getElements().size()-1;
			gradeAdjusted = (float) ((gradeAdjusted*10.0) / newRangeMax); 
		} else {
			return getGradeAdjustedTo10();
		}
		return Float.toString(gradeAdjusted);
	}
	
	/**
	 * Modifica la nota del GradeReportLine.
	 * 
	 * @param grade
	 * 		La nota.
	 */
	public void setGrade(String grade) {
		this.grade = grade;
	}

	/**
	 * Devuelve el peso del GradeReportLine.
	 * 
	 * @return weight
	 */
	public float getWeight() {
		return this.weight;
	}

	/**
	 * Modifica el peso del GradeReportLine.
	 * 
	 * @param weight
	 * 		El peso.
	 */
	public void setWeight(float weight) {
		this.weight = weight;
	}

	/**
	 * Devuelve el rango máximo de nota.
	 * 
	 * @return rangeMax
	 */
	// RMS changed
	public String getRangeMax() {
		return this.rangeMax;
	}

	/**
	 * Modifica el rango máximo de nota.
	 * 
	 * @param rangeMax
	 * 		EL rango máximo.
	 */
	public void setRangeMax(String rangeMax) {
		this.rangeMax = rangeMax;
	}

	/**
	 * Devuelve el rango mínimo de nota.
	 * 
	 * @return rangeMin
	 */
	public String getRangeMin() {
		return this.rangeMin;
	}

	/**
	 * Modifica el rango mínimo de nota.
	 * 
	 * @param rangeMin
	 * 		El rango mínimo.
	 */
	public void setRangeMin(String rangeMin) {
		this.rangeMin = rangeMin;
	}

	/**
	 * Devuelve el porcentaje del GradeReportLine.
	 * 
	 * @return percentage
	 */
	public float getPercentage() {
		return percentage;
	}

	/**
	 * Modifica el porcentaje del GradeReportLine.
	 * 
	 * @param percentage
	 * 		El porcentaje.
	 */
	public void setPercentage(float percentage) {
		this.percentage = percentage;
	}

	/**
	 * Devuelve el tipo (boolean) de GradeReportLine.
	 * False = Category, True = Item.
	 * 
	 * @return type
	 */
	public boolean getType() {
		return this.type;
	}

	/**
	 * Modifica el tipo (boolean) del GradeReportLine.
	 * 
	 * @param type
	 * 		El tipo. False = Category, True = Item.
	 */
	public void setType(boolean type) {
		this.type = type;
	}

	/**
	 * Devuelve el tipo de GradeReportLine.
	 * 
	 * @return nameType
	 */
	public String getNameType() {
		return this.typeName;
	}

	/**
	 * Modifica el tipo de GradeReportLine.
	 * 
	 * @param nameType
	 * 		El tipo.
	 */
	public void setNameType(String nameType) {
		this.typeName = nameType;
	}

	/**
	 * Devuelve la actividad asociada al GradeReportLine.
	 * 
	 * @return activity
	 */
	public Activity getActivity() {
		return this.activity;
	}

	/**
	 * Crea una actividad a partir del GradeReportLine.
	 * 
	 * @param activity
	 * 		La actividad.
	 */
	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	/**
	 * Devuelve los hijos que tiene el GradeReportLine.
	 * 
	 * @return children
	 */
	public List<GradeReportLine> getChildren() {
		return this.children;
	}

	/**
	 * Añade un hijo al GradeReportLine.
	 * 
	 * @param child
	 * 		El hijo.
	 */
	public void addChild(GradeReportLine child) {
		this.children.add(child);
	}
	
	/**
	 * Convierte el GradeReportLine a un String con su nombre.
	 */
	public String toString() {
		return this.getName();
	}
}
