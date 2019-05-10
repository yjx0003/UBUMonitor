package controllers.ubulogs;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.ubulogs.logcreator.Component;
import model.EnrolledUser;
import model.LogLine;

public abstract class GroupByAbstract<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static final Logger logger = LoggerFactory.getLogger(GroupByAbstract.class);

	Map<EnrolledUser, Map<Component, Map<T, Long>>> counts;

	private Set<EnrolledUser> enrolledUsers;
	private Map<Component, Map<T, DescriptiveStatistics>> statistics;

	public GroupByAbstract(List<LogLine> logLines, Set<EnrolledUser> enrolledUsers) {
		this.enrolledUsers = enrolledUsers;
		setCounts(logLines);

	}

	public static Predicate<LogLine> isBetweenTwoDates(ZonedDateTime start, ZonedDateTime end) {
		return l -> l.getTime().isAfter(start) && l.getTime().isBefore(end);
	}

	public static Predicate<LogLine> containsComponent(List<Component> components) {
		return l -> components.contains(l.getComponent());
	}

	public void setCounts(List<LogLine> logLines) {

		counts = logLines.stream()
				.filter(l -> l.getUser() != null) // quitamos los nulos, sino salta excepcion en el
													// Collectors.groupingBy
				.collect(Collectors.groupingBy(LogLine::getUser,
						Collectors.groupingBy(LogLine::getComponent,
								Collectors.groupingBy(getGroupByFunction(), Collectors.counting()))));
		
		logger.info("Contador de logs para" + getTypeTime() + ": " + counts);
		
	}

	public abstract List<T> getRange(ZonedDateTime start, ZonedDateTime end);

	public List<String> getRangeString(ZonedDateTime start, ZonedDateTime end) {
		return getRangeString(getRange(start, end));
	}

	public List<String> getRangeString(List<T> rangeList) {
		return rangeList.stream()
				.map(getStringFormatFunction())
				.collect(Collectors.toList());
	}

	public void generateStatistics(List<Component> components, List<T> groupByRange) {

		if(components.isEmpty()||groupByRange.isEmpty()) {
			return;
		}
		
		
		statistics = new HashMap<>();
		// el metodo computeIfAbsent devuelve el valor de la key y si no existe la key
		// se crea y devuelve el valor nuevo.
		for (EnrolledUser user : enrolledUsers) {
			Map<Component, Map<T, Long>> userCounts = counts.computeIfAbsent(user, k -> new HashMap<>());
			for (Component component : components) {
				Map<T, Long> userComponentsCounts = userCounts.computeIfAbsent(component, k -> new HashMap<>());
				Map<T, DescriptiveStatistics> componentStatistics = statistics.computeIfAbsent(component,
						k -> new HashMap<>());
				for (T groupBy : groupByRange) {
					
					long count = userComponentsCounts.computeIfAbsent(groupBy, k -> 0L);
					DescriptiveStatistics descriptiveStatistics = componentStatistics.computeIfAbsent(groupBy,
							k -> new DescriptiveStatistics());
					descriptiveStatistics.addValue(count);
				}
			}
		}
		logger.info("Estadisticas de todos los usuarios del curso para " + components + " y " + groupByRange + ":\n"
				+ statistics);

	}

	public Map<Component, List<Long>> getComponentsMeans(List<Component> components, ZonedDateTime start,
			ZonedDateTime end) {

		List<T> range = this.getRange(start, end);
		System.out.println(range);

		generateStatistics(components, range);
		
		
		

		Map<Component, List<Long>> results = new LinkedHashMap<>(); // importa el orden de insercion
		for (Component component : components) {
			List<Long> means = new ArrayList<>();
			Map<T, DescriptiveStatistics> componentStatistics = statistics.get(component);
			for (T typeTime : range) {
				DescriptiveStatistics descriptiveStatistics = componentStatistics.get(typeTime);
				means.add((long) descriptiveStatistics.getMean());
			}
			results.put(component, means);

		}

		return results;
	}

	public Map<EnrolledUser, Map<Component, List<Long>>> getUsersCounts(List<EnrolledUser> users,
			List<Component> components, ZonedDateTime start, ZonedDateTime end) {
		List<T> groupByRange=getRange(start, end);
		
		Map<EnrolledUser, Map<Component, List<Long>>> result = new HashMap<>();
		for (EnrolledUser user : users) {
			Map<Component, List<Long>> componentsCount = result.computeIfAbsent(user, k -> new HashMap<>());
			Map<Component, Map<T, Long>> userCounts = counts.computeIfAbsent(user, k -> new HashMap<>());
			for (Component component : components) {
				List<Long> userComponentCounts = componentsCount.computeIfAbsent(component, k -> new ArrayList<>());
				Map<T, Long> userComponentsCounts = userCounts.computeIfAbsent(component, k -> new HashMap<>());
				for (T groupBy : groupByRange) {
					long count = userComponentsCounts.computeIfAbsent(groupBy, k -> 0L);
					userComponentCounts.add(count);
				}
			}
		}
		logger.info("Estadisticas de todos los usuarios" + users + " para " + components + " y " + groupByRange + ":\n"
				+ result);
		return result;
	}

	public abstract Function<LogLine, T> getGroupByFunction();

	public abstract Function<T, String> getStringFormatFunction();

	public abstract TypeTimes getTypeTime();

	@Override
	public String toString() {
		return getTypeTime().toString();
	}
}
