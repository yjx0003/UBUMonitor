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
	HashMap<Integer, DescriptiveStatistics> gradesStats;
	
	/**
	 * Constructor de la clase Stats.
	 */
	public Stats() {
		gradesStats = new HashMap<>();
		// Inicializamos el HashMap con cada una de los Grade Report Lines de la asignatura.
		for (GradeReportLine actualLine : UBUGrades.session.getActualCourse().getGradeReportLines()) {
			this.createElementStats(actualLine.getId());
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
}
