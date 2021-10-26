package es.ubu.lsi.ubumonitor.view.chart.enrollment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
import javafx.scene.paint.Color;

public class EnrollmentBar extends Plotly {

	private Collection<Course> allCourses;

	public EnrollmentBar(MainController mainController, Collection<Course> allCourses) {
		super(mainController, ChartType.ENROLLMENT_BAR);
		this.allCourses = allCourses;
	}

	@Override
	public void exportCSV(String path) throws IOException {

		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader("courseid", "courseName", "userid", "userFullName"))) {
			Map<Course, List<EnrolledUser>> courseCount = countCoursesWithUsers(getSelectedEnrolledUser(), allCourses);
			for (Map.Entry<Course, List<EnrolledUser>> entry : courseCount.entrySet()) {
				Course course = entry.getKey();
				List<EnrolledUser> users = entry.getValue();
				for (EnrolledUser user : users) {
					printer.printRecord(course.getId(), course.getFullName(), user.getId(), user.getFullName());
				}

			}
		}
	}

	@Override
	public void createData(JSArray data) {
		List<EnrolledUser> users = getSelectedEnrolledUser();
		Map<Course, List<EnrolledUser>> courseCount = countCoursesWithUsers(users, allCourses);
		data.add(createTrace("Cursos", users, courseCount, getConfigValue("enrollmentBarColor"),
				getConfigValue("horizontalMode"), getConfigValue("minFrequency")));
	}

	public JSObject createTrace(String name, List<EnrolledUser> selectedUsers, Map<Course, List<EnrolledUser>> courseCount, Color color,
			boolean horizontalMode, int minFrequency) {
		JSObject trace = new JSObject();
		JSArray x = new JSArray();
		JSArray y = new JSArray();
		JSArray enrolledUsers = new JSArray();
		JSArray usersIdsArray = new JSArray();
		int i = 0;
		for (Map.Entry<Course, List<EnrolledUser>> entry : courseCount.entrySet()) {
			List<EnrolledUser> users = entry.getValue();
			if (selectedUsers.size() < minFrequency || users.size() >= minFrequency) {

				Course course = entry.getKey();
				StringBuilder usersTooltip = new StringBuilder();
				usersTooltip.append("<b>");
				usersTooltip.append(course.getFullName());
				usersTooltip.append("</b><br><br>");
				JSArray usersIds = new JSArray();
				for (EnrolledUser user : users) {
					usersIds.add(user.getId());
					usersTooltip.append(user.getFullName());
					usersTooltip.append("<br>");
				}

				x.add(i++);
				y.add(users.size());
				enrolledUsers.addWithQuote(usersTooltip);
				usersIdsArray.add(usersIds);
			}
		}

		Plotly.createAxisValuesHorizontal(horizontalMode, trace, x, y);

		trace.put("type", "'bar'");
		trace.putWithQuote("name", name);
		JSObject marker = new JSObject();
		marker.put("color", colorToRGB(color));
		trace.put("marker", marker);
		trace.put("customdata", enrolledUsers);
		trace.put("userids", usersIdsArray);
		trace.put("hovertemplate", "'%{customdata}<extra></extra>'");

		return trace;
	}

	@Override
	public String getOnClickFunction() {
		return Plotly.MULTIPLE_USER_ON_CLICK_FUNCTION;
	}

	private Map<Course, List<EnrolledUser>> countCoursesWithUsers(Collection<EnrolledUser> users,
			Collection<Course> courses) {
		TreeMap<Course, List<EnrolledUser>> courseCount = new TreeMap<>(Comparator.comparing(Course::getFullName));
		for (Course course : courses) {
			Set<EnrolledUser> selectedUsers = new HashSet<>(course.getEnrolledUsers());
			List<EnrolledUser> enrolledUsers = new ArrayList<>();
			for (EnrolledUser user : users) {
				if (selectedUsers.contains(user)) {
					enrolledUsers.add(user);
				}
			}
			// guardamos los cursos que tienen algun usuario matriculado
			if (!enrolledUsers.isEmpty()) {
				courseCount.put(course, enrolledUsers);
			}
		}
		
		// ordenamos por número de usuarios matriculados
		Comparator<List<EnrolledUser>> sizeComparator = Comparator.comparing(List::size);
		
		return courseCount.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue(sizeComparator.reversed()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	@Override
	public void createLayout(JSObject layout) {
		boolean horizontalMode = getConfigValue("horizontalMode");
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		Map<Course, List<EnrolledUser>> courseCount = countCoursesWithUsers(selectedUsers, allCourses);

		JSArray ticktext = new JSArray();
		int minFrequency = getConfigValue("minFrequency");
		for (Map.Entry<Course, List<EnrolledUser>> entry : courseCount.entrySet()) {
			Course course = entry.getKey();
			List<EnrolledUser> enrolledUsers = entry.getValue();
			if (selectedUsers.size() < minFrequency || enrolledUsers.size() >= minFrequency) {
				ticktext.addWithQuote(course.getFullName());
			}
		}

		horizontalMode(layout, ticktext, horizontalMode, getXAxisTitle(), getYAxisTitle(), null);
		layout.put("hovermode", "'closest'");

	}

}
