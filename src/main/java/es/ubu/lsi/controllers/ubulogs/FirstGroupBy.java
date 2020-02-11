package controllers.ubulogs;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import model.EnrolledUser;
import model.LogLine;

public class FirstGroupBy<E extends Serializable, T extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;
	private Map<EnrolledUser, Map<E, Map<T, Long>>> counts;
	private Map<E, Map<T, DescriptiveStatistics>> statistics;
	private GroupByAbstract<T> groupByAbstract;

	public FirstGroupBy(GroupByAbstract<T> groupBy, List<LogLine> logLines, Predicate<LogLine> filter,
			Function<LogLine, E> getEFunction, Function<LogLine, T> getTFunction) {
		this.groupByAbstract = groupBy;
		setCounts(logLines, filter, getEFunction, getTFunction);
	}

	public void setCounts(List<LogLine> logLines, Predicate<LogLine> filter, Function<LogLine, E> getEFunction,
			Function<LogLine, T> getTFunction) {

		counts = logLines.stream().filter(filter).collect(Collectors.groupingBy(LogLine::getUser,
				Collectors.groupingBy(getEFunction, Collectors.groupingBy(getTFunction, Collectors.counting()))));
	}

	/**
	 * Genera las estadisticas para un usuario
	 * 
	 * @param enrolledUsers usuarios que se quiere generar las estadisticas
	 * @param elements primer listado de agrupamiento
	 * @param groupByRange rango de un tipo de agrupacion
	 */
	public void generateStatistics(List<EnrolledUser> enrolledUsers, List<E> elements, List<T> groupByRange) {
		statistics = new HashMap<>();
		if (enrolledUsers.isEmpty() || elements.isEmpty() || groupByRange.isEmpty()) {
			return;
		}

		// el metodo computeIfAbsent devuelve el valor de la key y si no existe la key
		// se crea y devuelve el valor nuevo.
		for (EnrolledUser user : enrolledUsers) {

			Map<E, Map<T, Long>> userCounts = counts.computeIfAbsent(user, k -> new HashMap<>());
			for (E element : elements) {

				Map<T, Long> userComponentsCounts = userCounts.computeIfAbsent(element, k -> new HashMap<>());
				Map<T, DescriptiveStatistics> statisticsMap = statistics.computeIfAbsent(element, k -> new HashMap<>());

				for (T groupBy : groupByRange) {

					long count = userComponentsCounts.computeIfAbsent(groupBy, k -> 0L);
					DescriptiveStatistics descriptiveStatistics = statisticsMap.computeIfAbsent(groupBy,
							k -> new DescriptiveStatistics());
					descriptiveStatistics.addValue(count);
				}
			}
		}

	}

	/**
	 * Devuelve las medias de los componentes de un listado de usuarios, componentes
	 * y fecha de inicio y de fin
	 * 
	 * @param enrolledUsers lista de usuarios
	 * @param components lista de componentes
	 * @param start fecha de inicio
	 * @param end fecha de fin
	 * @return medias de componentes
	 */
	public Map<E, List<Double>> getMeans(List<EnrolledUser> enrolledUsers, List<E> elements, LocalDate start,
			LocalDate end) {
		List<T> range = groupByAbstract.getRange(start, end);
		generateStatistics(enrolledUsers, elements, range);

		Map<E, List<Double>> results = new HashMap<>();
		for (E element : elements) {
			List<Double> means = new ArrayList<>();
			Map<T, DescriptiveStatistics> statisticsMap = statistics.computeIfAbsent(element, k -> new HashMap<>());
			for (T typeTime : range) {
				DescriptiveStatistics descriptiveStatistics = statisticsMap.computeIfAbsent(typeTime,
						k -> new DescriptiveStatistics());
				means.add(descriptiveStatistics.getMean());
			}
			results.put(element, means);

		}

		return results;
	}

	/**
	 * Devuelve los contadores de acceso a los registros de unos usuarios y
	 * componentes
	 * 
	 * @param users usuarios
	 * @param components componentes
	 * @param start inicio
	 * @param end fin
	 * @return mapa multinivel
	 */
	public Map<EnrolledUser, Map<E, List<Long>>> getUsersCounts(List<EnrolledUser> users, List<E> elements,
			LocalDate start, LocalDate end) {

		List<T> groupByRange = groupByAbstract.getRange(start, end);

		Map<EnrolledUser, Map<E, List<Long>>> result = new HashMap<>();

		for (EnrolledUser user : users) {
			Map<E, List<Long>> elementsCount = result.computeIfAbsent(user, k -> new HashMap<>());
			Map<E, Map<T, Long>> userCounts = counts.computeIfAbsent(user, k -> new HashMap<>());

			for (E element : elements) {
				List<Long> userComponentCounts = elementsCount.computeIfAbsent(element, k -> new ArrayList<>());
				Map<T, Long> countsMap = userCounts.computeIfAbsent(element, k -> new HashMap<>());

				for (T groupBy : groupByRange) {
					long count = countsMap.computeIfAbsent(groupBy, k -> 0L);
					userComponentCounts.add(count);
				}
			}
		}
		return result;
	}

	/**
	 * El maximo de los componentes de los usuarios
	 * 
	 * @param enrolledUsers usuarios que se quiere buscar el maximo
	 * @param components componentes
	 * @param start fecha de inicio
	 * @param end fecha de fin
	 * @return maximo encontrado
	 */
	public long getMaxElement(List<EnrolledUser> enrolledUsers, List<E> elements, LocalDate start, LocalDate end) {

		if (elements.isEmpty()) {
			return 1L;
		}

		List<T> range = groupByAbstract.getRange(start, end);

		Map<EnrolledUser, Map<T, Long>> sumComponentsMap = new HashMap<>();

		for (EnrolledUser enrolledUser : enrolledUsers) {
			Map<E, Map<T, Long>> elementsMap = counts.computeIfAbsent(enrolledUser, k -> new HashMap<>());
			Map<T, Long> sumComponents = sumComponentsMap.computeIfAbsent(enrolledUser, k -> new HashMap<>());
			for (E element : elements) {
				Map<T, Long> groupByMap = elementsMap.computeIfAbsent(element, k -> new HashMap<>());
				for (T groupBy : range) {
					sumComponents.merge(groupBy, groupByMap.getOrDefault(groupBy, 0L), Long::sum);
				}

			}
		}

		return getMax(sumComponentsMap);

	}

	public long getCumulativeMax(List<EnrolledUser> enrolledUsers, List<E> elements, LocalDate start, LocalDate end) {
		if (elements.isEmpty()) {
			return 1L;
		}

		List<T> range = groupByAbstract.getRange(start, end);
		long max = 0;

		for (EnrolledUser enrolledUser : enrolledUsers) {
			long cum = 0;
			Map<E, Map<T, Long>> elementsMap = counts.computeIfAbsent(enrolledUser, k -> new HashMap<>());
			for (E element : elements) {
				Map<T, Long> groupByMap = elementsMap.computeIfAbsent(element, k -> new HashMap<>());
				for (T groupBy : range) {
					cum += groupByMap.getOrDefault(groupBy, 0L);
				}

			}
			if (cum > max) {
				max = cum;
			}
		}
		return max;
	}

	public long getMeanDifferenceMax(List<EnrolledUser> enrolledUsers, List<E> logTypes, LocalDate start,
			LocalDate end) {
		if (logTypes.isEmpty()) {
			return 1L;
		}

		Map<E, List<Double>> means = getMeans(enrolledUsers, logTypes, start, end);
		List<T> range = groupByAbstract.getRange(start, end);

		List<Double> cumMeans = new ArrayList<>();
		double result = 0;
		for (int j = 0; j < range.size(); j++) {

			for (E typeLog : logTypes) {
				List<Double> times = means.get(typeLog);
				result += times.get(j);
			}
			cumMeans.add(result);

		}

		double max = 0;

		for (EnrolledUser enrolledUser : enrolledUsers) {

			Map<E, Map<T, Long>> elementsMap = counts.computeIfAbsent(enrolledUser, k -> new HashMap<>());
			long cum = 0;
			for (int i = 0; i < cumMeans.size(); i++) {

				for (E logType : logTypes) {
					Map<T, Long> groupByMap = elementsMap.computeIfAbsent(logType, k -> new HashMap<>());

					cum += groupByMap.getOrDefault(range.get(i), 0L);

				}
				if (Math.abs(Math.ceil(cum - cumMeans.get(i))) > max) {
					max = Math.abs(Math.ceil(cum - cumMeans.get(i)));
				}
			}

		}

		return (long) max;
	}

	/**
	 * Busca el máximo mapa de usuario por cada tipo de tiempo y el contador.
	 * 
	 * @param sumMap mapa de usuario por cada tipo de tiempo y el contador
	 * @return el valor maximo encontrado
	 */
	private long getMax(Map<EnrolledUser, Map<T, Long>> sumMap) {
		long max = 1L;

		for (Map<T, Long> sumComponents : sumMap.values()) {
			for (long values : sumComponents.values()) {
				if (values > max) {
					max = values;
				}
			}
		}
		return max;
	}
}
