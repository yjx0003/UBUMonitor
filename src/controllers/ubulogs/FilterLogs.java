package controllers.ubulogs;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import controllers.ubulogs.logcreator.Component;
import model.EnrolledUser;
import model.LogLine;

public class FilterLogs {

	public static Predicate<LogLine> isBetweenTwoDates(ZonedDateTime start, ZonedDateTime end) {
		return l -> l.getTime().isAfter(start) && l.getTime().isBefore(end);
	}

	public static Predicate<LogLine> containsComponent(List<Component> components) {
		return l -> components.contains(l.getComponent());
	}
	
	public static <T> Map<EnrolledUser, Map<T, Map<Component, Long>>> getCount(List<EnrolledUser> enrolledUsers,
			List<Component> components, Function<LogLine, T> typeTime, ZonedDateTime start, ZonedDateTime end) {

		Map<EnrolledUser, Map<T, Map<Component, Long>>> userCounts = new HashMap<>();
		for (EnrolledUser user : enrolledUsers) {
			List<LogLine> logLines = user.getLogs();

			Map<T, Map<Component, Long>> counts = logLines.stream()
					.filter(containsComponent(components).and(isBetweenTwoDates(start, end)))
					.collect(
							Collectors.groupingBy(typeTime,
									Collectors.groupingBy(
											LogLine::getComponent, Collectors.counting())));

			userCounts.put(user, counts);
		}

		return userCounts;

	}
	

}
