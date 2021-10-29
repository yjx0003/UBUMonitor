package es.ubu.lsi.ubumonitor.view.chart.enrollment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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

public class EnrollmentSankey extends Plotly {

	private Collection<Course> allCourses;

	public EnrollmentSankey(MainController mainController, Collection<Course> allCourses) {
		super(mainController, ChartType.ENROLLMENT_SANKEY);
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
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		Map<Course, List<EnrolledUser>> countCoursesWithUsers = countCoursesWithUsers(selectedUsers, allCourses);
		JSObject dataObject = new JSObject();
		String userColor = colorToRGB(getConfigValue("userColorNode"));
		String courseColor = colorToRGB(getConfigValue("courseColorNode"));
		dataObject.put("type", "'sankey'");
		dataObject.put("orientation", "'h'");
		JSObject node = new JSObject();
		dataObject.put("node", node);
		JSArray label = new JSArray();
		JSArray color = new JSArray();
		node.put("color", color);
		node.put("label", label);
		node.put("hovertemplate", "'%{label}<br>%{value:d}<extra></extra>'");
		
		label.addWithQuote(I18n.get("text.selectedUsers"));
		color.add(userColor);
		for (Course course : countCoursesWithUsers.keySet()) {
			label.addWithQuote(course.getFullName());
			color.add(courseColor);
		}

		JSObject link = new JSObject();
		dataObject.put("link", link);
		link.put("hovertemplate", "'%{target.label}<br>%{value:d}<extra></extra>'");
		JSArray source = new JSArray();
		link.put("source", source);
		JSArray target = new JSArray();
		link.put("target", target);
		JSArray value = new JSArray();
		link.put("value", value);

		int i = 1;
		for (Map.Entry<Course, List<EnrolledUser>> entry : countCoursesWithUsers.entrySet()) {
			List<EnrolledUser> users = entry.getValue();

			source.add(0);
			target.add(i);
			value.add(users.size());

			i++;
		}

		data.add(dataObject);

	}

	@Override
	public void createLayout(JSObject layout) {
		// unnecessary

	}

	private Map<Course, List<EnrolledUser>> countCoursesWithUsers(Collection<EnrolledUser> users,
			Collection<Course> courses) {
		TreeMap<Course, List<EnrolledUser>> courseCount = new TreeMap<>(Course.getCourseComparator());
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

		return courseCount;
	}

}
