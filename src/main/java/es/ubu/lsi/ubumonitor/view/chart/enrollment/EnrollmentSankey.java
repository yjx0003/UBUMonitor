package es.ubu.lsi.ubumonitor.view.chart.enrollment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;
import javafx.scene.paint.Color;

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
		String userColor = colorToRGB(Color.ALICEBLUE);
		String courseColor = colorToRGB(Color.DARKRED);
		dataObject.put("type", "'sankey'");
		dataObject.put("orientation", "'v'");
		JSObject node = new JSObject();
		dataObject.put("node", node);
		JSArray customdata = new JSArray();
		JSArray color = new JSArray();
		node.put("color", color);
		node.put("customdata", customdata);
		node.put("hovertemplate", "'%{customdata}<br>%{value:d}<extra></extra>'");
		for(Course course :countCoursesWithUsers.keySet()) {
			customdata.addWithQuote(course.getFullName());
			color.add(courseColor);
		}
				
		Map<EnrolledUser, Integer> userIndex = new HashMap<>(selectedUsers.size());
		int countCourses = countCoursesWithUsers.keySet()
				.size();
		for (int i = 0; i < selectedUsers.size(); i++) {
			EnrolledUser user = selectedUsers.get(i);
			userIndex.put(user, i + countCourses);
			customdata.addWithQuote(user.getFullName());
			color.add(userColor);
		}
		
		
		JSObject link = new JSObject();
		dataObject.put("link", link);
		link.put("hovertemplate", "'%{source.customdata} <br>%{target.customdata}<extra></extra>'");
		JSArray source = new JSArray();
		link.put("source", source);
		JSArray target = new JSArray();
		link.put("target", target);
		JSArray value = new JSArray();
		link.put("value", value);
		
		int i = 0;
		for(Map.Entry<Course, List<EnrolledUser>> entry:countCoursesWithUsers.entrySet()) {
			List<EnrolledUser> users = entry.getValue();
			
			for(EnrolledUser user: users) {
				source.add(i);
				target.add(userIndex.get(user));
				value.add(1);
			}
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
