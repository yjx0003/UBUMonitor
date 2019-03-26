package model;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase Stats para la obtención de estadisticas utilizando Apache Commons Math.
 * 
 * @author Félix Nogal Santamaría
 * @version 1.0
 *
 */
public class Stats {

	static final Logger logger = LoggerFactory.getLogger(Stats.class);

	private final NumberFormat numberFormat;
	private static final int MAX_DECIMAL_DIGITS = 2;
	private static final int MIN_DECIMAL_DIGITS = 0;

	/**
	 * Map que almacena como clave el id del GradeReporLine(GRL) y como valor el
	 * objeto de estadisticas de Apache Commons Math.
	 */
	private Map<GradeItem, DescriptiveStatistics> generalGradesStats;

	/**
	 * Map que almacena como clave el nombre del grupo y como valor un HashMap con
	 * clave el id del GRL y valor el objeto de estadisticas de Apahce Commons Math.
	 */
	private Map<Group, Map<GradeItem, DescriptiveStatistics>> groupsStats;

	public Stats(Course course, Locale locale) throws Exception {
		numberFormat = NumberFormat.getInstance(locale);
		numberFormat.setMaximumFractionDigits(MAX_DECIMAL_DIGITS);
		numberFormat.setMinimumFractionDigits(MIN_DECIMAL_DIGITS);
		generateGeneralStats(course);
		generateGroupStats(course);
		logger.info("Estadisticas generadas con exito.");
	}

	/**
	 * Genera las estatisticas generales del curso.
	 * 
	 * @param session
	 *            La sesión de la cual obtener las notas.
	 * 
	 * @throws Exception
	 */
	private void generateGeneralStats(Course course) throws Exception {
		try {
			logger.info("Generando las estadisticas generales para el curso cargado.");
			// Estadisticas Generales
			generalGradesStats = new HashMap<>();

			for (GradeItem gradeItem : course.getGradeItems()) {
				// guardamos las calificaciones de los matriculados de un gradeItem ignorando
				// los que no tienen notas (NaN)
				double[] gradesWithoutNaN = gradeItem.getEnrolledUserGrades()
						.stream()
						.mapToDouble(Double::doubleValue)
						.filter(grade -> !Double.isNaN(grade))
						.toArray();

				generalGradesStats.put(gradeItem, new DescriptiveStatistics(gradesWithoutNaN));

			}

		} catch (Exception e) {
			logger.error("Error al generar las estadisticas generales.", e);
			throw new Exception("Error al generar las estadisticas generales.");
		}
	}

	/**
	 * Genera las estadisticas para cada grupo del curso.
	 * 
	 * @param session
	 *            La sesión de la cual obtener las notas.
	 * @throws Exception
	 */
	private void generateGroupStats(Course course) throws Exception {
		try {
			logger.info("Generando las estadisticas de los gruopos para el curso cargado.");
			// Estadisticas de los grupos
			groupsStats = new HashMap<>();

			for (Group group : course.getGroups()) {
				Map<GradeItem, DescriptiveStatistics> mapGradeItems = new HashMap<>();
				groupsStats.put(group, mapGradeItems);

				for (GradeItem gradeItem : course.getGradeItems()) {
					DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
					mapGradeItems.put(gradeItem, descriptiveStatistics);
					for (EnrolledUser user : group.getEnrolledUsers()) {
						double grade = gradeItem.getEnrolledUserGrade(user);
						if (!Double.isNaN(grade)) {
							descriptiveStatistics.addValue(grade);
						}
					}

				}

			}

		} catch (Exception e) {
			logger.error("Error al generar las estadisticas de los grupos.", e);
			throw new Exception("Error al generar las estadisticas de los grupos.");
		}
	}

	/**
	 * Devuelve las estadisticas generales.
	 * 
	 * @return HashMap con las estadisticas.
	 */
	public Map<GradeItem, DescriptiveStatistics> getGeneralStats() {
		return generalGradesStats;
	}

	/**
	 * Devuelve las estadisticas de un grupo.
	 * 
	 * @param group
	 *            El grupo.
	 * @return HashMap con las estadisticas de dicho grupo.
	 */
	public Map<GradeItem, DescriptiveStatistics> getGroupStats(Group group) {
		return groupsStats.get(group);
	}

	private String format(double value) {
		return Double.isNaN(value) ? "NaN" : numberFormat.format(value);
	}

	/**
	 * Obtenemos la media del GRL.
	 * 
	 * @param statsHs
	 *            EL mapa con las estadisticas.
	 * @param gradeId
	 *            El id del Grade Report Line
	 * @return La media del GRL.
	 */
	public String getElementMean(Map<GradeItem, DescriptiveStatistics> statsHs, GradeItem gradeItem) {
		// Hacemos que la media tenga el formato #.## para mostrarlo
		// sin problemas en el gráfico

		DescriptiveStatistics statistics = statsHs.get(gradeItem);
		double mean = statistics.getMean();

		return format(mean);

	}

	/**
	 * Obtenemos la mediana del GRL.
	 * 
	 * @param statsHs
	 *            EL mapa con las estadisticas.
	 * @param gradeId
	 *            El id del Grade Report Line
	 * @return La mediana del GRL.
	 */
	public String getMedian(Map<GradeItem, DescriptiveStatistics> statsHs, GradeItem gradeItem) {

		return getElementPercentile(statsHs, gradeItem, 50.0);
	}

	/**
	 * Obtenemos el percentil del GRL.
	 * 
	 * @param statsHs
	 *            EL mapa con las estadisticas.
	 * @param gradeId
	 *            El id del Grade Report Line.
	 * @param percentil
	 *            El percentil que queremos.
	 * @return El percentil del GRL.
	 */
	public String getElementPercentile(Map<GradeItem, DescriptiveStatistics> statsHs, GradeItem gradeItem,
			double percentil) {

		DescriptiveStatistics statistics = statsHs.get(gradeItem);
		double percentile = statistics.getPercentile(percentil);

		return format(percentile);
	}

	/**
	 * Obtenemos el maximo del GRL.
	 * 
	 * @param statsHs
	 *            EL mapa con las estadisticas.
	 * @param gradeId
	 *            El id del Grade Report Line.
	 * @return El maximo del GRL.
	 */
	public String getMaxmimum(Map<GradeItem, DescriptiveStatistics> statsHs, GradeItem gradeItem) {

		DescriptiveStatistics statistics = statsHs.get(gradeItem);
		double max = statistics.getMax();
		return format(max);
	}

	/**
	 * Obtenemos el minimo del GRL.
	 * 
	 * @param statsHs
	 *            EL mapa con las estadisticas.
	 * @param gradeId
	 *            El id del Grade Report Line.
	 * @return El minimo del GRL.
	 */
	public String getMinimum(Map<GradeItem, DescriptiveStatistics> statsHs, GradeItem gradeItem) {

		DescriptiveStatistics statistics = statsHs.get(gradeItem);
		double max = statistics.getMin();
		return format(max);
	}

	/**
	 * Permite obtener el limite superior para el diagrama de tipo boxplot.
	 * 
	 * @param statsHs
	 *            EL mapa con las estadisticas.
	 * @param gradeId
	 *            El id del Grade Report Line.
	 * @return El limite superior.
	 */
	public String getUpperLimit(Map<GradeItem, DescriptiveStatistics> statsHs, GradeItem gradeItem) {
		DescriptiveStatistics statistics = statsHs.get(gradeItem);
		// Calculamos el rango intercuartilico
		double ric = statistics.getPercentile(75) - statistics.getPercentile(25);
		double upperLimit = statistics.getPercentile(75) + ric * 1.5;
		upperLimit = Math.min(upperLimit, statistics.getMax());

		return format(upperLimit);
	}

	/**
	 * Permite obtener el limite inferior para el diagrama de tipo boxplot.
	 * 
	 * @param statsHs
	 *            EL mapa con las estadisticas.
	 * @param gradeId
	 *            El id del Grade Report Line.
	 * @return El limite inferior.
	 */
	public String getLowerLimit(Map<GradeItem, DescriptiveStatistics> statsHs, GradeItem gradeItem) {
		DescriptiveStatistics statistics = statsHs.get(gradeItem);
		// Calculamos el rango intercuartilico
		double ric = statistics.getPercentile(75) - statistics.getPercentile(25);
		double lowerLimit = statistics.getPercentile(25) - ric * 1.5;
		lowerLimit = Math.max(lowerLimit, statistics.getMin());

		return format(lowerLimit);
	}

	/**
	 * Devuelve una lista con los valores atipicos para el digarama de tipo boxplot.
	 * 
	 * @param statsHs
	 *            EL mapa con las estadisticas.
	 * @param gradeId
	 *            El id del Grade Report Line.
	 * @return Lista con los valores atipicos.
	 */
	public List<String> getAtypicalValues(Map<GradeItem, DescriptiveStatistics> statsHs, GradeItem gradeItem) {
		DescriptiveStatistics statistics = statsHs.get(gradeItem);
		List<String> atypicalValues = new ArrayList<>();
		double lowerLimit = Double.parseDouble(getLowerLimit(statsHs, gradeItem));
		double upperLimit = Double.parseDouble(getUpperLimit(statsHs, gradeItem));
		double[] values = statistics.getValues();
		Arrays.sort(values);

		for (int i = 0; i < values.length; i++) {
			if (values[i] < lowerLimit) {

				atypicalValues.add(format(values[i]));
			} else {
				break;
			}
		}

		for (int i = values.length - 1; i >= 0; i--) {
			if (values[i] > upperLimit) {

				atypicalValues.add(format(values[i]));
			} else {
				break;
			}
		}

		return atypicalValues;
	}

	public Map<GradeItem, DescriptiveStatistics> getGroupStats(String group) {
		Group grupo = groupsStats.keySet().stream()
				.filter(g -> g.getName().equals(group))
				.findFirst()
				.get();
		return getGroupStats(grupo);
	}

}
