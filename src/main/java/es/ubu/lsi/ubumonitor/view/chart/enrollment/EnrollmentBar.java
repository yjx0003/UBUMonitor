package es.ubu.lsi.ubumonitor.view.chart.enrollment;

import java.io.IOException;
import java.time.Instant;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
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

	private Integer getYear(int startMonth, int endMonth, Instant firstAccess) {
		if (firstAccess == null) {
			return null;
		}
		ZonedDateTime zonedDateTime = firstAccess.atZone(ZoneId.systemDefault());
		if (startMonth > endMonth && zonedDateTime.getMonth()
				.getValue() < startMonth) {
			return zonedDateTime.getYear() - 1;
		}
		return zonedDateTime.getYear();
	}

	private Set<Integer> getUniqueYears(Map<Integer, Map<Course, List<EnrolledUser>>> courseCount) {
		Set<Integer> years = new TreeSet<>(Comparator.nullsLast(Comparator.naturalOrder()));
		for (Integer year : courseCount.keySet()) {

			years.add(year);

		}
		return years;
	}

	private Set<Course> getUniqueCourses(Map<Integer, Map<Course, List<EnrolledUser>>> courseCount,
			List<EnrolledUser> selectedUsers, int min) {
		Map<Course, Integer> courses = new TreeMap<>(Course.getCourseComparator().reversed());

		for (Map<Course, List<EnrolledUser>> map : courseCount.values()) {
			for (Map.Entry<Course, List<EnrolledUser>> entry : map.entrySet()) {
				Course course = entry.getKey();
				List<EnrolledUser> users = entry.getValue();
				Integer enrolls = courses.get(course);
				courses.put(course, enrolls == null ? users.size() : enrolls + users.size());
			}
		}
		if (selectedUsers.size() >= min) {
			courses.entrySet()
					.removeIf(e -> e.getValue() < min);
		}
		return courses.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue())
				.map(Map.Entry::getKey)
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	@Override
	public void createData(JSArray data) {
		List<EnrolledUser> users = getSelectedEnrolledUser();
		Month startMonth = getConfigValue("startMonth");
		Month endMonth = getConfigValue("endMonth");

		Map<Integer, Map<Course, List<EnrolledUser>>> courseCount = countCoursesWithUsers(users, allCourses,
				startMonth.getValue(), endMonth.getValue());

		Set<Integer> years = getUniqueYears(courseCount);
		Set<Course> courses = getUniqueCourses(courseCount, users, getConfigValue("minFrequency"));
		for (Integer year : years) {
			data.add(createTrace(year == null ? "Unknown" : year.toString(), courses, courseCount.get(year),
					getConfigValue("horizontalMode")));
		}
	}

	public JSObject createTrace(String name, Set<Course> courses, Map<Course, List<EnrolledUser>> courseCount,
			boolean horizontalMode) {
		JSObject trace = new JSObject();
		JSArray x = new JSArray();
		JSArray y = new JSArray();
		JSArray enrolledUsers = new JSArray();
		JSArray usersIdsArray = new JSArray();
		JSArray text = new JSArray();
		int i = 0;
		for (Course course : courses) {
			List<EnrolledUser> users = courseCount.getOrDefault(course, Collections.emptyList());

			StringBuilder usersTooltip = new StringBuilder();
			usersTooltip.append("<b>");
			usersTooltip.append(course.getFullName());
			usersTooltip.append(" (");
			usersTooltip.append(course.getId());
			usersTooltip.append(")</b><br><br>");
			JSArray usersIds = new JSArray();
			for (EnrolledUser user : users) {
				usersIds.add(user.getId());
				usersTooltip.append(" • ");
				usersTooltip.append(user.getFullName());
				usersTooltip.append("<br>");
			}

			x.add(i++);
			y.add(users.size());
			enrolledUsers.addWithQuote(usersTooltip);
			usersIdsArray.add(usersIds);
			text.add(users.size());
		}

		Plotly.createAxisValuesHorizontal(horizontalMode, trace, x, y);

		trace.put("type", "'bar'");
		trace.putWithQuote("name", name);
		trace.put("customdata", enrolledUsers);
		trace.put("userids", usersIdsArray);
		trace.put("text", text);
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
		Month startMonth = getConfigValue("startMonth");
		Month endMonth = getConfigValue("endMonth");

		Map<Integer, Map<Course, List<EnrolledUser>>> courseCount = countCoursesWithUsers(users, allCourses,
				startMonth.getValue(), endMonth.getValue());

		Set<Course> courses = getUniqueCourses(courseCount, users, getConfigValue("minFrequency"));

		JSArray ticktext = new JSArray();
		for (Course course : courses) {

			ticktext.addWithQuote(course.getFullName());

		}

		horizontalMode(layout, ticktext, horizontalMode, getXAxisTitle(), getYAxisTitle(), null);
		if (horizontalMode) {
			JSObject yaxis = (JSObject) layout.get("yaxis");
			yaxis.put("categoryorder", "'total descending'");
		} else {
			JSObject xaxis = (JSObject) layout.get("xaxis");
			xaxis.put("categoryorder", "'total descending'");
		}
		layout.put("hovermode", "'closest'");
		layout.put("hoverlabel", "{align:'left'}");
		layout.put("barmode", "'stack'");
	}

}
