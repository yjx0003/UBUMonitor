package es.ubu.lsi.ubumonitor.view.chart.enrollment;

import java.io.IOException;
import java.time.Instant;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;

public class EnrollmentBar extends Plotly {

	private Collection<Course> allCourses;

	public EnrollmentBar(MainController mainController, Collection<Course> allCourses) {
		super(mainController, ChartType.ENROLLMENT_BAR);
		this.allCourses = allCourses;
		this.useLegend = true;
	}

	@Override
	public void exportCSV(String path) throws IOException {
		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader("courseid", "courseName", "userid", "userFullName"))) {
			Month startMonth = getConfigValue("startMonth");
			Month endMonth = getConfigValue("endMonth");

			Map<Integer, Map<Course, List<EnrolledUser>>> courseCount = countCoursesWithUsers(getSelectedEnrolledUser(),
					allCourses, startMonth.getValue(), endMonth.getValue());

			for (Map.Entry<Integer, Map<Course, List<EnrolledUser>>> entry : courseCount.entrySet()) {

				Map<Course, List<EnrolledUser>> usersCourse = entry.getValue();
				for (Map.Entry<Course, List<EnrolledUser>> usersEntry : usersCourse.entrySet()) {
					List<EnrolledUser> users = usersEntry.getValue();
					Course course = usersEntry.getKey();
					for (EnrolledUser user : users) {
						printer.printRecord(course.getId(), course.getFullName(), user.getId(), user.getFullName());
					}
				}

			}
		}

	}

	public static Integer getYear(int startMonth, int endMonth, Instant firstAccess) {
		if (firstAccess == null || firstAccess.getEpochSecond() == 0) {
			return null;
		}
		ZonedDateTime zonedDateTime = firstAccess.atZone(ZoneId.systemDefault());
		int firstAccessMonth = zonedDateTime.getMonthValue();
		if (startMonth > endMonth && firstAccessMonth < startMonth && firstAccessMonth <= endMonth) {
			return zonedDateTime.getYear() - 1;
		} else if (startMonth < endMonth && firstAccessMonth > endMonth) {
			return zonedDateTime.getYear() + 1;
		}
		return zonedDateTime.getYear();
	}

	private Set<Integer> getUniqueYears(Map<Integer, Map<Course, List<EnrolledUser>>> courseCount) {
		Set<Integer> years = new TreeSet<>(Comparator.nullsFirst(Comparator.naturalOrder()));
		for (Integer year : courseCount.keySet()) {

			years.add(year);

		}
		return years;
	}

	private Map<Course, Integer> countCoursesWithUsers(Collection<EnrolledUser> users, Collection<Course> courses) {
		TreeMap<Course, Integer> courseCount = new TreeMap<>(Course.getCourseComparator());
		for (Course course : courses) {
			Set<EnrolledUser> selectedUsers = new HashSet<>(course.getEnrolledUsers());
			int enrolledUsers = 0;
			for (EnrolledUser user : users) {
				if (selectedUsers.contains(user)) {
					enrolledUsers++;
				}
			}
			if (enrolledUsers > 0) {
				courseCount.put(course, enrolledUsers);
			}

		}

		return courseCount.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	private Set<Course> getUniqueCourses(Map<Course, Integer> courseCount, List<EnrolledUser> selectedUsers, int min) {
		Map<Course, Integer> courses = new LinkedHashMap<>(courseCount);

		if (selectedUsers.size() >= min) {
			courses.entrySet()
					.removeIf(e -> e.getValue() < min);
		}
		return courses.keySet();
	}

	@Override
	public void createData(JSArray data) {
		List<EnrolledUser> users = getSelectedEnrolledUser();
		Month startMonth = getConfigValue("startMonth");
		Month endMonth = getConfigValue("endMonth");

		Map<Integer, Map<Course, List<EnrolledUser>>> courseCount = countCoursesWithUsers(users, allCourses,
				startMonth.getValue(), endMonth.getValue());
		Map<Course, Integer> courseCountTotal = countCoursesWithUsers(users, allCourses);

		Set<Integer> years = getUniqueYears(courseCount);
		Set<Course> courses = getUniqueCourses(courseCountTotal, users, getConfigValue("minFrequency"));
		for (Integer year : years) {
			String yearString;
			if (year == null) {
				yearString = I18n.get("unknown");
			} else if (startMonth.getValue() > endMonth.getValue()) {
				yearString = year + "-" + (year + 1);
			} else {
				yearString = year.toString();
			}
			data.add(createTrace(yearString, courses, courseCount.get(year), courseCountTotal,
					getConfigValue("horizontalMode")));
		}
	}

	public JSObject createTrace(String name, Set<Course> courses, Map<Course, List<EnrolledUser>> yearCourseCount,
			Map<Course, Integer> totalCount, boolean horizontalMode) {

		JSObject trace = new JSObject();
		JSArray x = new JSArray();
		JSArray y = new JSArray();
		JSArray enrolledUsers = new JSArray();
		JSArray usersIdsArray = new JSArray();
		int i = 0;
		for (Course course : courses) {
			List<EnrolledUser> users = yearCourseCount.getOrDefault(course, Collections.emptyList());

			StringBuilder usersTooltip = new StringBuilder();
			usersTooltip.append("<b>");
			usersTooltip.append(course.getFullName());
			usersTooltip.append(" (");
			usersTooltip.append(course.getId());
			usersTooltip.append(")<br><br>");
			usersTooltip.append(name);
			usersTooltip.append(": ");
			usersTooltip.append(users.size());
			usersTooltip.append("<br>");
			usersTooltip.append(I18n.get("total"));
			usersTooltip.append(totalCount.getOrDefault(course, 0));
			usersTooltip.append("</b><br><br>");
			JSArray usersIds = new JSArray();
			for (EnrolledUser user : users) {
				usersIds.add(user.getId());
				usersTooltip.append(" • ");
				usersTooltip.append(user.getFullName());
				Instant instant = user.getFirstaccess();
				if (instant != null && instant.getEpochSecond() != 0) {
					usersTooltip.append(" (" + instant.atZone(ZoneId.systemDefault())
							.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) + ")");
				}
				usersTooltip.append("<br>");
			}

			x.add(i++);
			y.add(users.size());
			enrolledUsers.addWithQuote(usersTooltip);
			usersIdsArray.add(usersIds);

		}

		Plotly.createAxisValuesHorizontal(horizontalMode, trace, x, y);

		trace.put("type", "'bar'");
		trace.putWithQuote("name", name);
		trace.put("customdata", enrolledUsers);
		trace.put("userids", usersIdsArray);
		trace.put("hovertemplate", "'%{customdata}<extra></extra>'");

		return trace;
	}

	@Override
	public String getOnClickFunction() {
		return Plotly.MULTIPLE_USER_ON_CLICK_FUNCTION;
	}

	private Map<Integer, Map<Course, List<EnrolledUser>>> countCoursesWithUsers(Collection<EnrolledUser> users,
			Collection<Course> courses, int startMonth, int endMonth) {
		Map<Integer, Map<Course, List<EnrolledUser>>> courseCount = new HashMap<>();
		for (Course course : courses) {
			Set<EnrolledUser> enrolledUsers = new HashSet<>(course.getEnrolledUsers());
			for (EnrolledUser user : users) {
				if (enrolledUsers.contains(user)) {

					Integer year = getYear(startMonth, endMonth, user.getFirstaccess());

					courseCount.computeIfAbsent(year, k -> new HashMap<>())
							.computeIfAbsent(course, k -> new ArrayList<>())
							.add(user);

				}
			}

		}

		return courseCount;

	}

	@Override
	public void createLayout(JSObject layout) {
		boolean horizontalMode = getConfigValue("horizontalMode");
		List<EnrolledUser> users = getSelectedEnrolledUser();

		Map<Course, Integer> courseCountTotal = countCoursesWithUsers(users, allCourses);

		Set<Course> courses = getUniqueCourses(courseCountTotal, users, getConfigValue("minFrequency"));

		JSArray ticktext = new JSArray();
		for (Course course : courses) {

			ticktext.addWithQuote(course.getFullName());

		}

		horizontalMode(layout, ticktext, horizontalMode, getXAxisTitle(), getYAxisTitle(), null);
//		if (horizontalMode) {
//			JSObject yaxis = (JSObject) layout.get("yaxis");
//			yaxis.put("categoryorder", "'total descending'");
//		} else {
//			JSObject xaxis = (JSObject) layout.get("xaxis");
//			xaxis.put("categoryorder", "'total descending'");
//		}

		JSArray annotations = new JSArray();
		int i = 0;
		for (Course course : courses) {
			Integer enrolled = courseCountTotal.getOrDefault(course, 0);
			JSObject annotation = new JSObject();
			annotation.put("showarrow", false);
			annotation.put("x", horizontalMode ? enrolled + enrolled * 0.05 : i);
			annotation.put("y", horizontalMode ? i : enrolled + enrolled * 0.05);
			annotation.put("text", enrolled);
			annotations.add(annotation);
			i++;
		}

		layout.put("annotations", annotations);
		layout.put("hovermode", "'closest'");
		layout.put("hoverlabel", "{align:'left'}");
		layout.put("barmode", "'stack'");
		layout.put("legend", "{traceorder:'normal'}");
	}

}
