package model;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

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
	
	/**
	 * HashMap que almacena como clave el id del GradeReporLine(GRL)
	 * y como valor el objeto de estadisticas de Apache Commons Math.
	 */
	private HashMap<Integer, DescriptiveStatistics> gradesStats;
	
	/**
	 * Constructor de la clase Stats.
	 */
	public Stats() {
		gradesStats = new HashMap<>();
		String grade = "";
		// Inicializamos el HashMap con cada una de los Grade Report Lines de la asignatura.
		for (GradeReportLine actualLine : UBUGrades.session.getActualCourse().getGradeReportLines()) {
			this.createElementStats(actualLine.getId());
		}
		
		// Añadimos las notas de cada usuario para cada GradeReportLine
		for(EnrolledUser enrroledUser: UBUGrades.session.getActualCourse().getEnrolledUsers()) {
			for(GradeReportLine gradeReportLine: enrroledUser.getAllGradeReportLines()) {
				grade = gradeReportLine.getGrade();
				// Si la nota es "-", es que ese alumno no tiene nota en dicha calificacion, por tanto lo saltamos
				if(!grade.equals("-")) {
				this.addElementValue(gradeReportLine.getId(), this.parseStringGradeToDouble(grade));
				}
			}
		}
	}
	
	/**
	 * Crea un nuevo par clave, valor para el id especificado.
	 * 
	 * @param gradeId
	 * 				El id del Grade Report Line que queremos añadir. 
	 */
	public void createElementStats (int gradeId) {
		gradesStats.put(gradeId, new DescriptiveStatistics());
	}
	
	/**
	 * Añadimos una nota.
	 * 
	 * @param gradeId
	 * 		El id del Grade Report Line.
	 * @param value
	 * 		La nota a añadir.
	 */
	public void addElementValue (int gradeId, double value) {
		DescriptiveStatistics stats = gradesStats.get(gradeId);
		stats.addValue(value);
		gradesStats.put(gradeId, stats);
	}
	
	/**
	 * Obtenemos la media del GRL.
	 * 
	 * @param gradeId
	 * 		El id del Grade Report Line
	 * @return
	 * 		La media del GRL.
	 */
	public String getElementMean(int gradeId) {
		// Hacemos que la media tenga el formato #.## para mostrarlo
		// sin problemas en el gráfico
		DecimalFormat df = new DecimalFormat("#.##");
		DescriptiveStatistics stats = gradesStats.get(gradeId);
		String mean = df.format(stats.getMean());
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
	public String getMedian(int gradeId) {
		// Hacemos que la media tenga el formato #.## para mostrarlo
		// sin problemas en el gráfico
		DecimalFormat df = new DecimalFormat("#.##");
		DescriptiveStatistics stats = gradesStats.get(gradeId);
		String median = df.format(stats.getPercentile(50));
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
	public String getElementPercentile(int gradeId, double percentil) {
		// Hacemos que el percentil tenga el formato #.## para mostrarlo
		// sin problemas en el gráfico
		DecimalFormat df = new DecimalFormat("#.##");
		DescriptiveStatistics stats = gradesStats.get(gradeId);
		String percentile = df.format(stats.getPercentile(percentil));
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
	public String getMaxmimum(int gradeId) {
		// Hacemos que el maximo tenga el formato #.## para mostrarlo
		// sin problemas en el gráfico
		DecimalFormat df = new DecimalFormat("#.##");
		DescriptiveStatistics stats = gradesStats.get(gradeId);
		String maximum = df.format(stats.getMax());
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
	public String getMinimum(int gradeId) {
		// Hacemos que el minimo tenga el formato #.## para mostrarlo
		// sin problemas en el gráfico
		DecimalFormat df = new DecimalFormat("#.##");
		DescriptiveStatistics stats = gradesStats.get(gradeId);
		String minimum = df.format(stats.getMin());
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
