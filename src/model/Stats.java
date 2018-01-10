package model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.MainController;

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
	 * Instancia de las estadisticas.
	 */
	private static Stats stats = null;
	
	/**
	 * Map que almacena como clave el id del GradeReporLine(GRL)
	 * y como valor el objeto de estadisticas de Apache Commons Math.
	 */
	private Map<Integer, DescriptiveStatistics> generalGradesStats;
	
	/**
	 * Map que almacena como clave el nombre del grupo
	 * y como valor un HashMap con clave el id del GRL y valor el objeto de estadisticas de Apahce Commons Math.
	 */
	private Map<String, HashMap<Integer, DescriptiveStatistics>> groupsStats;
	
	/**
	 * Genera las estadisticas en caso de no estar ya generadas.
	 * @return
	 * 		La instancia de las estadisticas.
	 * @throws Exception
	 */
	public static Stats getStats(Session session) throws Exception {
		if (stats == null) {
			stats = new Stats(session);
		}
		return stats;
	}
	
	/**
	 * Elimina la instancia de las estadisticas.
	 */
	public static void removeStats() {
		stats = null;
	}
	
	/**
	 * Constructor de la clase Stats.
	 * @throws Exception 
	 */
	protected Stats(Session session) throws Exception{
		generateGeneralStats(session);
		generateGroupStats(session);
		logger.info("Estadisticas generadas con exito.");	
	}
	
	/**
	 * Genera las estatisticas generales del curso.
	 * 
	 * @throws Exception
	 */
	private void generateGeneralStats(Session session) throws Exception {
		try {
			logger.info("Generando las estadisticas generales para el curso cargado.");
			//Estadisticas Generales
			generalGradesStats = new HashMap<>();
			String grade = "";
			// Inicializamos el HashMap con cada una de los Grade Report Lines de la asignatura.
			for (GradeReportLine actualLine : session.getActualCourse().getGradeReportLines()) {
				generalGradesStats.put(actualLine.getId(), new DescriptiveStatistics());
			}
			
			// Añadimos las notas de cada usuario para cada GradeReportLine
			for(EnrolledUser enrroledUser: session.getActualCourse().getEnrolledUsers()) {
				for(GradeReportLine gradeReportLine: enrroledUser.getAllGradeReportLines()) {
					grade = gradeReportLine.getGradeAdjustedTo10();
					// Si la nota es "NaN", es que ese alumno no tiene nota en dicha calificacion, por tanto lo saltamos
					if(!grade.equals("NaN")) {
						this.addElementValue(generalGradesStats, gradeReportLine.getId(), this.parseStringGradeToDouble(grade));
					}
				}
			}
		}catch (Exception e) {
			logger.error("Error al generar las estadisticas generales." , e);
			throw new Exception("Error al generar las estadisticas generales.");
		}
	}
	
	/**
	 * Genera las estadisticas para cada grupo del curso.
	 * 
	 * @throws Exception
	 */
	private void generateGroupStats(Session session) throws Exception {
		try {
			logger.info("Generando las estadisticas de los gruopos para el curso cargado.");
			//Estadisticas de los grupos
			groupsStats = new HashMap<>();
			String grade = "";
			HashMap<Integer, DescriptiveStatistics> currentGroupStats;
			
			// Generamos estadisticas para cada uno de los grupos
			for(String group: session.getActualCourse().getGroups()) {
				
				// Inicializamos el HashMap con cada una de los Grade Report Lines de la asignatura para ese grupo.
				currentGroupStats = new HashMap<>();
				for (GradeReportLine actualLine : session.getActualCourse().getGradeReportLines()) {
					currentGroupStats.put(actualLine.getId(), new DescriptiveStatistics());
				}
				
				for(EnrolledUser enrroledUser: session.getActualCourse().getUsersInGroup(group)) {
					for(GradeReportLine gradeReportLine: enrroledUser.getAllGradeReportLines()) {
						grade = gradeReportLine.getGradeAdjustedTo10();
						// Si la nota es "NaN", es que ese alumno no tiene nota en dicha calificacion, por tanto lo saltamos
						if(!grade.equals("NaN")) {
							this.addElementValue(currentGroupStats, gradeReportLine.getId(), this.parseStringGradeToDouble(grade));
						}
					}
				}
				groupsStats.put(group, currentGroupStats);
			}
		} catch (Exception e) {
			logger.error("Error al generar las estadisticas de los grupos." , e);
			throw new Exception("Error al generar las estadisticas de los grupos.");
		}
	}
	
	/**
	 * Devuelve las estadisticas generales.
	 * @return
	 * 		HashMap con las estadisticas.
	 */
	public Map<Integer , DescriptiveStatistics> getGeneralStats() {
		return generalGradesStats;
	}
	
	/**
	 * Devuelve las estadisticas de un grupo.
	 * @param group
	 * 		El grupo.
	 * @return
	 * 		HashMap con las estadisticas de dicho grupo.
	 */
	public Map<Integer , DescriptiveStatistics> getGroupStats(String group) {
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
	public void addElementValue (Map<Integer,DescriptiveStatistics> statsHs, int gradeId, double value) {
		DescriptiveStatistics statistics = statsHs.get(gradeId);
		statistics.addValue(value);
		statsHs.put(gradeId, statistics);
	}
	
	/**
	 * Obtenemos la media del GRL.
	 * 
	 * @param gradeId
	 * 		El id del Grade Report Line
	 * @return
	 * 		La media del GRL.
	 */
	public String getElementMean(Map<Integer,DescriptiveStatistics> statsHs, int gradeId) {
		// Hacemos que la media tenga el formato #.## para mostrarlo
		// sin problemas en el gráfico
		DecimalFormat df = new DecimalFormat("#.##");
		DescriptiveStatistics statistics = statsHs.get(gradeId);
		String mean  = String.valueOf(statistics.getMean()).equals("NaN") ? "NaN" : df.format(statistics.getMean());
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
	public String getMedian(Map<Integer,DescriptiveStatistics> statsHs, int gradeId) {
		// Hacemos que la media tenga el formato #.## para mostrarlo
		// sin problemas en el gráfico
		DecimalFormat df = new DecimalFormat("#.##");
		DescriptiveStatistics statistics = statsHs.get(gradeId);
		String median  = String.valueOf(statistics.getPercentile(50)).equals("NaN") ? "NaN" : df.format(statistics.getPercentile(50));
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
	public String getElementPercentile(Map<Integer,DescriptiveStatistics> statsHs, int gradeId, double percentil) {
		// Hacemos que el percentil tenga el formato #.## para mostrarlo
		// sin problemas en el gráfico
		DecimalFormat df = new DecimalFormat("#.##");
		DescriptiveStatistics statistics = statsHs.get(gradeId);
		String percentile  = String.valueOf(statistics.getPercentile(percentil)).equals("NaN") ? "NaN" : df.format(statistics.getPercentile(percentil));
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
	public String getMaxmimum(Map<Integer,DescriptiveStatistics> statsHs, int gradeId) {
		// Hacemos que el maximo tenga el formato #.## para mostrarlo
		// sin problemas en el gráfico
		DecimalFormat df = new DecimalFormat("#.##");
		DescriptiveStatistics statistics = statsHs.get(gradeId);
		String maximum  = String.valueOf(statistics.getMax()).equals("NaN") ? "NaN" : df.format(statistics.getMax());
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
	public String getMinimum(Map<Integer,DescriptiveStatistics> statsHs, int gradeId) {
		// Hacemos que el minimo tenga el formato #.## para mostrarlo
		// sin problemas en el gráfico
		DecimalFormat df = new DecimalFormat("#.##");
		DescriptiveStatistics statistics = statsHs.get(gradeId);
		String minimum  = String.valueOf(statistics.getMin()).equals("NaN") ? "NaN" : df.format(statistics.getMin());
		minimum = minimum.replace(",", ".");
		return minimum;
	}
	
	/**
	 * Permite obtener el limite superior para el diagrama de tipo boxplot.
	 * 
	 * @param statsHs
	 * 		Las estadisticas.
	 * @param gradeId
	 * 		El id del Grade Report Line.
	 * @return
	 * 		El limite superior.
	 */
	public String getUpperLimit(Map<Integer,DescriptiveStatistics> statsHs, int gradeId) {
		DescriptiveStatistics statistics = statsHs.get(gradeId);
		// Calculamos el rango intercuartilico
		double ric = statistics.getPercentile(75) - statistics.getPercentile(25);
		double upperLimit = statistics.getPercentile(75) + ric * 1.5;
		upperLimit = Math.min(upperLimit, statistics.getMax());
		DecimalFormat df = new DecimalFormat("#.##");
		String upperLimitString = String.valueOf(upperLimit).equals("NaN") ? "NaN" : df.format(upperLimit);
		upperLimitString = upperLimitString.replace(",", ".");
		return upperLimitString;	
	}

	/**
	 * Permite obtener el limite inferior para el diagrama de tipo boxplot.
	 * 
	 * @param statsHs
	 * 		Las estadisticas.
	 * @param gradeId
	 * 		El id del Grade Report Line.
	 * @return
	 * 		El limite inferior.
	 */
	public String getLowerLimit(Map<Integer,DescriptiveStatistics> statsHs, int gradeId) {
		DescriptiveStatistics statistics = statsHs.get(gradeId);
		// Calculamos el rango intercuartilico
		double ric = statistics.getPercentile(75) - statistics.getPercentile(25);
		double lowerLimit = statistics.getPercentile(25) - ric * 1.5;
		lowerLimit = Math.max(lowerLimit, statistics.getMin());
		DecimalFormat df = new DecimalFormat("#.##");
		String lowerLimitString = String.valueOf(lowerLimit).equals("NaN") ? "NaN" : df.format(lowerLimit);
		lowerLimitString = lowerLimitString.replace(",", ".");
		return lowerLimitString;
	}
	
	
	/**
	 * Devuelve una lista con los valores atipicos para el digarama de tipo boxplot.
	 * 
	 * @param statsHs
	 * 		Las estadisticas.
	 * @param gradeId
	 * 		El id del Grade Report Line.
	 * @return
	 * 		Lista con los valores atipicos.
	 */
	public List<String> getAtypicalValues(Map<Integer,DescriptiveStatistics> statsHs, int gradeId) {
		DescriptiveStatistics statistics = statsHs.get(gradeId);
		List<String> atypicalValues = new ArrayList<>();
		double lowerLimit = Double.parseDouble(getLowerLimit(statsHs, gradeId));
		double upperLimit = Double.parseDouble(getUpperLimit(statsHs, gradeId));	
		double[] values = statistics.getValues();
		Arrays.sort(values);
		
		DecimalFormat df = new DecimalFormat("#.##");
	
		for(int i = 0; i < values.length; i++) {
			if(values[i] < lowerLimit) {
				String temp = df.format(values[i]);
				temp = temp.replaceAll(",", ".");
				atypicalValues.add(temp);
			} else {
				break;
			}
		}
		
		for(int i = values.length-1; i >= 0; i--) {
			if(values[i] > upperLimit) {
				String temp = df.format(values[i]);
				temp = temp.replaceAll(",", ".");
				atypicalValues.add(temp);
			} else {
				break;
			}
		}
		
		return atypicalValues;
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
