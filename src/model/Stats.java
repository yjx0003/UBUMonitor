package model;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.MainController;
import controllers.UBUGrades;

/**
 * Clase Stats para la obtención de estadisticas utilizando
 * Apache Commons Math.
 * 
 * @author Félix Nogal Santamaría
 * @version 1.0
 *
 */
public class Stats {
	
	static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	/**
	 * HashMap que almacena como clave el id del GradeReporLine(GRL)
	 * y como valor el objeto de estadisticas de Apache Commons Math.
	 */
	private HashMap<Integer, DescriptiveStatistics> generalGradesStats;
	
	/**
	 * HashMap que almacena como clave el nombre del grupo
	 * y como valor un HashMap con clave el id del GRL y valor el objeto de estadisticas de Apahce Commons Math.
	 */
	private HashMap<String, HashMap<Integer, DescriptiveStatistics>> groupsStats;
	
	/**
	 * Constructor de la clase Stats.
	 */
	public Stats() {
		logger.info("Generando las estadisticas generales para el curso cargado.");
		//Estadisticas Generales
		generalGradesStats = new HashMap<>();
		String grade = "";
		// Inicializamos el HashMap con cada una de los Grade Report Lines de la asignatura.
		for (GradeReportLine actualLine : UBUGrades.session.getActualCourse().getGradeReportLines()) {
			generalGradesStats.put(actualLine.getId(), new DescriptiveStatistics());
		}
		
		// Añadimos las notas de cada usuario para cada GradeReportLine
		for(EnrolledUser enrroledUser: UBUGrades.session.getActualCourse().getEnrolledUsers()) {
			for(GradeReportLine gradeReportLine: enrroledUser.getAllGradeReportLines()) {
				grade = gradeReportLine.getGrade();
				// Si la nota es "-", es que ese alumno no tiene nota en dicha calificacion, por tanto lo saltamos
				if(!grade.equals("-") && !grade.equals("")) {
					this.addElementValue(generalGradesStats, gradeReportLine.getId(), this.parseStringGradeToDouble(grade));
				}
			}
		}
		
		logger.info("Generando las estadisticas de los gruopos para el curso cargado.");
		//Estadisticas de los grupos
		groupsStats = new HashMap<>();
		HashMap<Integer, DescriptiveStatistics> currentGroupStats;
		
		// Generamos estadisticas para cada uno de los grupos
		for(String group: UBUGrades.session.getActualCourse().getGroups()) {
			
			// Inicializamos el HashMap con cada una de los Grade Report Lines de la asignatura para ese grupo.
			currentGroupStats = new HashMap<>();
			for (GradeReportLine actualLine : UBUGrades.session.getActualCourse().getGradeReportLines()) {
				currentGroupStats.put(actualLine.getId(), new DescriptiveStatistics());
			}
			
			for(EnrolledUser enrroledUser: UBUGrades.session.getActualCourse().getUsersInGroup(group)) {
				for(GradeReportLine gradeReportLine: enrroledUser.getAllGradeReportLines()) {
					grade = gradeReportLine.getGrade();
					// Si la nota es "-", es que ese alumno no tiene nota en dicha calificacion, por tanto lo saltamos
					if(!grade.equals("-") && !grade.equals("")) {
						this.addElementValue(currentGroupStats, gradeReportLine.getId(), this.parseStringGradeToDouble(grade));
					}
				}
			}
			groupsStats.put(group, currentGroupStats);
		}
		logger.info("Estadisticas generadas.");
	}
	
	/**
	 * Devuelve las estadisticas generales.
	 * @return
	 * 		HashMap con las estadisticas.
	 */
	public HashMap<Integer , DescriptiveStatistics> getGeneralStats() {
		return generalGradesStats;
	}
	
	/**
	 * Devuelve las estadisticas de un grupo.
	 * @param group
	 * 		El grupo.
	 * @return
	 * 		HashMap con las estadisticas de dicho grupo.
	 */
	public HashMap<Integer , DescriptiveStatistics> getGroupStats(String group) {
		return groupsStats.get(group);
	}
		
	/**
	 * Añadimos una nota.
	 * 
	 * @param gradeId
	 * 		El id del Grade Report Line.
	 * @param value
	 * 		La nota a añadir.
	 */
	public void addElementValue (HashMap<Integer,DescriptiveStatistics> statsHs, int gradeId, double value) {
		DescriptiveStatistics stats = statsHs.get(gradeId);
		if(stats != null) {
			stats.addValue(value);
			statsHs.put(gradeId, stats);
		}
	}
	
	/**
	 * Obtenemos la media del GRL.
	 * 
	 * @param gradeId
	 * 		El id del Grade Report Line
	 * @return
	 * 		La media del GRL.
	 */
	public String getElementMean(HashMap<Integer,DescriptiveStatistics> statsHs, int gradeId) {
		// Hacemos que la media tenga el formato #.## para mostrarlo
		// sin problemas en el gráfico
		DecimalFormat df = new DecimalFormat("#.##");
		DescriptiveStatistics stats = statsHs.get(gradeId);
		String mean  = String.valueOf(stats.getMean()).equals("NaN") ? "NaN" : df.format(stats.getMean());
		mean = mean.replace(",", ".");
		return mean;
	}
	
	/**
	 * Obtenemos la mediana del GRL.
	 * 
	 * @param gradeId
	 * 		El id del Grade Report Line
	 * @return
	 * 		La mediana del GRL.
	 */
	public String getMedian(HashMap<Integer,DescriptiveStatistics> statsHs, int gradeId) {
		// Hacemos que la media tenga el formato #.## para mostrarlo
		// sin problemas en el gráfico
		DecimalFormat df = new DecimalFormat("#.##");
		DescriptiveStatistics stats = statsHs.get(gradeId);
		String median  = String.valueOf(stats.getPercentile(50)).equals("NaN") ? "NaN" : df.format(stats.getPercentile(50));
		median = median.replace(",", ".");
		return median;
	}
	
	/**
	 * Obtenemos el percentil del GRL.
	 * 
	 * @param gradeId
	 * 		El id del Grade Report Line.
	 * @param percentil
	 * 		El percentil que queremos.
	 * @return
	 * 		El percentil del GRL.
	 */
	public String getElementPercentile(HashMap<Integer,DescriptiveStatistics> statsHs, int gradeId, double percentil) {
		// Hacemos que el percentil tenga el formato #.## para mostrarlo
		// sin problemas en el gráfico
		DecimalFormat df = new DecimalFormat("#.##");
		DescriptiveStatistics stats = statsHs.get(gradeId);
		String percentile  = String.valueOf(stats.getPercentile(percentil)).equals("NaN") ? "NaN" : df.format(stats.getPercentile(percentil));
		percentile = percentile.replace(",", ".");
		return percentile;
	}
	
	/**
	 * Obtenemos el maximo del GRL.
	 * 
	 * @param gradeId
	 * 		El id del Grade Report Line.
	 * @return
	 * 		El maximo del GRL.
	 */
	public String getMaxmimum(HashMap<Integer,DescriptiveStatistics> statsHs, int gradeId) {
		// Hacemos que el maximo tenga el formato #.## para mostrarlo
		// sin problemas en el gráfico
		DecimalFormat df = new DecimalFormat("#.##");
		DescriptiveStatistics stats = statsHs.get(gradeId);
		String maximum  = String.valueOf(stats.getMax()).equals("NaN") ? "NaN" : df.format(stats.getMax());
		maximum = maximum.replace(",", ".");
		return maximum;
	}
	
	/**
	 * Obtenemos el minimo del GRL.
	 * 
	 * @param gradeId
	 * 		El id del Grade Report Line.
	 * @return
	 * 		El minimo del GRL.
	 */
	public String getMinimum(HashMap<Integer,DescriptiveStatistics> statsHs, int gradeId) {
		// Hacemos que el minimo tenga el formato #.## para mostrarlo
		// sin problemas en el gráfico
		DecimalFormat df = new DecimalFormat("#.##");
		DescriptiveStatistics stats = statsHs.get(gradeId);
		String minimum  = String.valueOf(stats.getMin()).equals("NaN") ? "NaN" : df.format(stats.getMin());
		minimum = minimum.replace(",", ".");
		return minimum;
	}
	
    /**
     * Convierte la nota  de String a Double
     * 
     * @param grade
     * 		La nota a parsear.
     * @return
     * 		La nota como tipo Double.
     */
    private double parseStringGradeToDouble(String grade) {
    	grade = grade.replace(",", ".");
    	return Double.parseDouble(grade);
    }
}
