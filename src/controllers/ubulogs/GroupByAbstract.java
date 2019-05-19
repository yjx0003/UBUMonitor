package controllers.ubulogs;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import controllers.ubulogs.logcreator.Component;
import controllers.ubulogs.logcreator.ComponentEvent;
import controllers.ubulogs.logcreator.Event;
import model.EnrolledUser;
import model.LogLine;

public abstract class GroupByAbstract<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static final Logger logger = LoggerFactory.getLogger(GroupByAbstract.class);

	Map<EnrolledUser, Map<Component, Map<Event, Map<T, Long>>>> countsEvents;

	Map<EnrolledUser, Map<Component, Map<T, Long>>> countsComponents;

	private Set<EnrolledUser> enrolledUsers;

	private Map<Component, Map<T, DescriptiveStatistics>> componentStatistics;
	private Map<Component, Map<Event, Map<T, DescriptiveStatistics>>> componentEventStatistics;

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
		// quitamos los nulos, sino salta excepcion en el Collectors.groupingBy
		// LogLine::getUser
		countsEvents = logLines.stream()
				.filter(l -> l.getUser() != null)
				.collect(Collectors.groupingBy(LogLine::getUser,
						Collectors.groupingBy(LogLine::getComponent,
								Collectors.groupingBy(LogLine::getEventName,
										Collectors.groupingBy(getGroupByFunction(), Collectors.counting())))));

		logger.info("Contador de logs de componentes y eventos para " + getTypeTime() + ": " + countsEvents);
		Gson gsonBuilder = new GsonBuilder().create();
		logger.info("JSON del contador de logs de componentes y eventos  para " + getTypeTime() + " "
				+ gsonBuilder.toJson(countsEvents));

		countsComponents = logLines.stream()
				.filter(l -> l.getUser() != null)
				.collect(Collectors.groupingBy(LogLine::getUser,
						Collectors.groupingBy(LogLine::getComponent,
								Collectors.groupingBy(getGroupByFunction(), Collectors.counting()))));

		logger.info("Contador de logs de componentes para " + getTypeTime() + ": " + countsComponents);
		logger.info("JSON del contador de logs de componentes  para " + getTypeTime() + " "
				+ gsonBuilder.toJson(countsComponents));

	}

	public abstract List<T> getRange(LocalDate start, LocalDate end);

	public List<String> getRangeString(LocalDate start, LocalDate end) {
		return getRangeString(getRange(start, end));
	}

	public List<String> getRangeString(List<T> rangeList) {
		return rangeList.stream()
				.map(getStringFormatFunction())
				.collect(Collectors.toList());
	}

	public void generateComponentsStatistics(List<Component> components, List<T> groupByRange) {

		if (components.isEmpty() || groupByRange.isEmpty()) {
			return;
		}

		componentStatistics = new HashMap<>();
		// el metodo computeIfAbsent devuelve el valor de la key y si no existe la key
		// se crea y devuelve el valor nuevo.
		for (EnrolledUser user : enrolledUsers) {

			Map<Component, Map<T, Long>> userCounts = countsComponents.computeIfAbsent(user, k -> new HashMap<>());
			for (Component component : components) {

				Map<T, Long> userComponentsCounts = userCounts.computeIfAbsent(component, k -> new HashMap<>());
				Map<T, DescriptiveStatistics> statistics = componentStatistics.computeIfAbsent(component,
						k -> new HashMap<>());

				for (T groupBy : groupByRange) {

					long count = userComponentsCounts.computeIfAbsent(groupBy, k -> 0L);
					DescriptiveStatistics descriptiveStatistics = statistics.computeIfAbsent(groupBy,
							k -> new DescriptiveStatistics());
					descriptiveStatistics.addValue(count);
				}
			}
		}
		logger.info("Estadisticas de todos los usuarios del curso para components" + components + " y " + groupByRange
				+ ":\n"
				+ componentStatistics);

	}

	public void generateComponentsEventsStastistics(List<ComponentEvent> componentsEvents, List<T> groupByRange) {

		if (componentsEvents.isEmpty() || groupByRange.isEmpty()) {
			return;
		}
		componentEventStatistics = new HashMap<>();
		// el metodo computeIfAbsent devuelve el valor de la key y si no existe la key
		// se crea y devuelve el valor nuevo.
		for (EnrolledUser user : enrolledUsers) {

			Map<Component, Map<Event, Map<T, Long>>> componentCounts = countsEvents.computeIfAbsent(user,
					k -> new HashMap<>());
			for (ComponentEvent componentEvent : componentsEvents) {

				Component component = componentEvent.getComponent();
				Event event = componentEvent.getEventName();

				Map<Event, Map<T, Long>> componentsCounts = componentCounts.computeIfAbsent(component,
						k -> new HashMap<>());
				Map<Event, Map<T, DescriptiveStatistics>> componentStatistics = componentEventStatistics
						.computeIfAbsent(component, k -> new HashMap<>());

				Map<T, Long> eventsCounts = componentsCounts.computeIfAbsent(event, k -> new HashMap<>());
				Map<T, DescriptiveStatistics> eventDescriptiveStatistics = componentStatistics
						.computeIfAbsent(event, k -> new HashMap<>());

				for (T groupBy : groupByRange) {

					long count = eventsCounts.computeIfAbsent(groupBy, k -> 0L);
					DescriptiveStatistics descriptiveStatistics = eventDescriptiveStatistics.computeIfAbsent(groupBy,
							k -> new DescriptiveStatistics());
					descriptiveStatistics.addValue(count);
				}
			}

		}

		logger.info("Estadisticas de todos los usuarios del curso para components" + componentsEvents + " y "
				+ groupByRange + ":\n" + componentEventStatistics);
	}

	public Map<Component, List<Double>> getComponentsMeans(List<Component> components, LocalDate start,
			LocalDate end) {

		List<T> range = this.getRange(start, end);

		generateComponentsStatistics(components, range);

		Map<Component, List<Double>> results = new HashMap<>();
		for (Component component : components) {
			List<Double> means = new ArrayList<>();
			Map<T, DescriptiveStatistics> statistics = componentStatistics.get(component);
			for (T typeTime : range) {
				DescriptiveStatistics descriptiveStatistics = statistics.get(typeTime);
				means.add(descriptiveStatistics.getMean());
			}
			results.put(component, means);

		}

		return results;
	}

	public Map<ComponentEvent, List<Double>> getComponentEventMeans(List<ComponentEvent> componentsEvents,
			LocalDate start, LocalDate end) {

		List<T> range = this.getRange(start, end);

		generateComponentsEventsStastistics(componentsEvents, range);

		Map<ComponentEvent, List<Double>> results = new HashMap<>();
		for (ComponentEvent componentEvent : componentsEvents) {
			Component component = componentEvent.getComponent();
			Event event = componentEvent.getEventName();

			Map<Event, Map<T, DescriptiveStatistics>> eventStatistics = componentEventStatistics.get(component);
			Map<T, DescriptiveStatistics> statistics = eventStatistics.get(event);
			List<Double> means = new ArrayList<>();
			for (T typeTime : range) {
				DescriptiveStatistics descriptiveStatistics = statistics.get(typeTime);
				means.add(descriptiveStatistics.getMean());
			}
			results.put(componentEvent, means);
		}

		return results;
	}

	public Map<EnrolledUser, Map<Component, List<Long>>> getUsersComponentCounts(List<EnrolledUser> users,
			List<Component> components, LocalDate start, LocalDate end) {
		List<T> groupByRange = getRange(start, end);

		Map<EnrolledUser, Map<Component, List<Long>>> result = new HashMap<>();

		for (EnrolledUser user : users) {
			Map<Component, List<Long>> componentsCount = result.computeIfAbsent(user, k -> new HashMap<>());
			Map<Component, Map<T, Long>> userCounts = countsComponents.computeIfAbsent(user, k -> new HashMap<>());

			for (Component component : components) {
				List<Long> userComponentCounts = componentsCount.computeIfAbsent(component, k -> new ArrayList<>());
				Map<T, Long> counts = userCounts.computeIfAbsent(component, k -> new HashMap<>());

				for (T groupBy : groupByRange) {
					long count = counts.computeIfAbsent(groupBy, k -> 0L);
					userComponentCounts.add(count);
				}
			}
		}
		logger.info("Estadisticas de los usuarios: " + users + " para " + components + " y " + groupByRange + ":\n"
				+ result);
		return result;
	}

	public Map<EnrolledUser, Map<ComponentEvent, List<Long>>> getUserComponentEventCounts(List<EnrolledUser> users,
			List<ComponentEvent> componentsEvents, LocalDate start, LocalDate end) {

		List<T> groupByRange = getRange(start, end);
		Map<EnrolledUser, Map<ComponentEvent, List<Long>>> result = new HashMap<>();

		for (EnrolledUser user : users) {
			Map<ComponentEvent, List<Long>> componentEventResult = result.computeIfAbsent(user, k -> new HashMap<>());

			Map<Component, Map<Event, Map<T, Long>>> componentCounts = countsEvents.computeIfAbsent(user,
					k -> new HashMap<>());

			for (ComponentEvent componentEvent : componentsEvents) {
				Component component = componentEvent.getComponent();
				Event event = componentEvent.getEventName();

				List<Long> countsResult = componentEventResult.computeIfAbsent(componentEvent, k -> new ArrayList<>());
				Map<Event, Map<T, Long>> eventCounts = componentCounts.computeIfAbsent(component, k -> new HashMap<>());
				Map<T, Long> counts = eventCounts.computeIfAbsent(event, k -> new HashMap<>());
				
				for (T groupBy : groupByRange) {
					long count = counts.computeIfAbsent(groupBy, k -> 0L);
					countsResult.add(count);
				}
			}
		}

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
