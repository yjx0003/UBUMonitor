package es.ubu.lsi.ubumonitor.view.chart.enrollment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.VisNetwork;
import javafx.util.Pair;

public class CourseEnrollmentNetwork extends VisNetwork {
	private Collection<Course> allCourses;

	public CourseEnrollmentNetwork(MainController mainController, Collection<Course> allCourses) {
		super(mainController, ChartType.ENROLLMENT_COURSE_NETWORK);
		this.allCourses = allCourses;
	}

	@Override
	public void exportCSV(String path) throws IOException {
		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader("fromId", "fromName", "toId", "toName", "enrolledUsers"))) {
			int minFrequency = getConfigValue("minFrequency");
			
			List<EnrolledUser> users = getSelectedEnrolledUser();
			Map<Course, Set<EnrolledUser>> courses = coursesWithUser(users, allCourses);
			Map<Pair<Course, Course>, List<EnrolledUser>> courseEdges = courseEdges(courses, minFrequency);
		
			
			for(Map.Entry<Pair<Course, Course>, List<EnrolledUser>> entry: courseEdges.entrySet()) {
				Course from = entry.getKey().getKey();
				Course to = entry.getKey().getValue();
				
				List<EnrolledUser> usersList = entry.getValue();
				printer.printRecord(from.getId(), from.getFullName(), to.getId(), to.getFullName(), usersList.size());
			}
		}

	}

	@Override
	public JSObject getEdgesOptions() {
		JSObject edges = super.getEdgesOptions();
		edges.put("arrows", "{to:{enabled:false}}");
		edges.put("dashes", getConfigValue("edges.dashes"));
		edges.put("physics", false);

		JSObject scaling = new JSObject();
		scaling.put("max", getConfigValue("edges.scaling.max"));
		scaling.put("min", getConfigValue("edges.scaling.min"));
		edges.put("scaling", scaling);
		return edges;
	}

	@Override
	public JSObject getNodesOptions() {
		JSObject nodes = super.getNodesOptions();

		nodes.put("shape", "'dot'");

		nodes.put("borderWidth", getConfigValue("nodes.borderWidth"));
		JSObject scaling = new JSObject();
		scaling.put("max", getConfigValue("nodes.scaling.max"));
		scaling.put("min", getConfigValue("nodes.scaling.min"));
		nodes.put("scaling", scaling);
		nodes.put("font", "{multi:true,bold:{size:16}}");
		return nodes;
	}

	@Override
	public JSObject getPhysicsOptions() {
		JSObject physics = super.getPhysicsOptions();
		physics.put("enabled", true);
		Solver solver = getConfigValue("physics.solver");
		physics.putWithQuote("solver", solver.getName());
		switch (solver) {
		case BARNES_HUT:
			JSObject barnesHut = new JSObject();
			barnesHut.put("theta", getConfigValue("physics.barnesHut.theta"));
			barnesHut.put("gravitationalConstant", getConfigValue("physics.barnesHut.gravitationalConstant"));
			barnesHut.put("centralGravity", getConfigValue("physics.barnesHut.centralGravity"));
			barnesHut.put("springLength", getConfigValue("physics.barnesHut.springLength"));
			barnesHut.put("springConstant", getConfigValue("physics.barnesHut.springConstant"));
			barnesHut.put("damping", getConfigValue("physics.barnesHut.damping"));
			barnesHut.put("avoidOverlap", getConfigValue("physics.barnesHut.avoidOverlap"));
			physics.put("barnesHut", barnesHut);
			break;
		case FORCE_ATLAS_2_BASED:
			JSObject forceAtlas2Based = new JSObject();
			forceAtlas2Based.put("theta", getConfigValue("physics.forceAtlas2Based.theta"));
			forceAtlas2Based.put("gravitationalConstant",
					getConfigValue("physics.forceAtlas2Based.gravitationalConstant"));
			forceAtlas2Based.put("centralGravity", getConfigValue("physics.forceAtlas2Based.centralGravity"));
			forceAtlas2Based.put("springLength", getConfigValue("physics.forceAtlas2Based.springLength"));
			forceAtlas2Based.put("springConstant", getConfigValue("physics.forceAtlas2Based.springConstant"));
			forceAtlas2Based.put("damping", getConfigValue("physics.forceAtlas2Based.damping"));
			forceAtlas2Based.put("avoidOverlap", getConfigValue("physics.forceAtlas2Based.avoidOverlap"));
			physics.put("forceAtlas2Based", forceAtlas2Based);
			break;
		case REPULSION:
			JSObject repulsion = new JSObject();
			repulsion.put("nodeDistance", getConfigValue("physics.repulsion.nodeDistance"));
			repulsion.put("centralGravity", getConfigValue("physics.repulsion.centralGravity"));
			repulsion.put("springLength", getConfigValue("physics.repulsion.springLength"));
			repulsion.put("sprinConstant", getConfigValue("physics.repulsion.springConstant"));
			repulsion.put("damping", getConfigValue("physics.repulsion.damping"));
			physics.put("repulsion", repulsion);
			break;
		default:
			// default barneshut with default parameters
			break;
		}

		return physics;

	}

	@Override
	public JSObject getLayoutOptions() {
		JSObject layout = super.getLayoutOptions();
		String randomSeed = getConfigValue("layout.randomSeed");

		layout.put("randomSeed", StringUtils.isBlank(randomSeed) ? "undefined" : randomSeed);

		layout.put("clusterThreshold", getConfigValue("layout.clusterThreshold"));
		return layout;
	}

	@Override
	public void update() {
		int minFrequency = getConfigValue("minFrequency");
		boolean showNonConnected = getConfigValue("showNonConnected");
		boolean useInitialNames = getConfigValue("useInitialNames");
		boolean showUsernames = getConfigValue("nodes.showUsernames");
		boolean moreInfoEdge = getConfigValue("edges.moreInfoEdge");
		
		List<EnrolledUser> users = getSelectedEnrolledUser();
		Map<Course, Set<EnrolledUser>> courses = coursesWithUser(users, allCourses);
		Map<Pair<Course, Course>, List<EnrolledUser>> courseEdges = courseEdges(courses, minFrequency);
	
		if(!showNonConnected) {
			Set<Course> coursesWithEdge = courseWithEdge(courseEdges);
			courses.keySet().retainAll(coursesWithEdge);
		}
		
		JSObject data = new JSObject();
		data.put("nodes", createNodes(courses, useInitialNames, showUsernames));
		data.put("edges", createEdges(courseEdges, moreInfoEdge));

		
		webViewChartsEngine.executeScript("updateVisNetwork(" + data + "," + getOptions() + ")");
	}

	private JSArray createEdges(Map<Pair<Course, Course>, List<EnrolledUser>> courseEdges, boolean moreInfoEdge) {
		JSArray edges = new JSArray();
		for(Map.Entry<Pair<Course, Course>, List<EnrolledUser>> entry:courseEdges.entrySet()) {
			Course from = entry.getKey().getKey();
			Course to = entry.getKey().getValue();
			
			List<EnrolledUser> users = entry.getValue();
			
			JSObject edge = new JSObject();
			edges.add(edge);
			edge.put("from", from.getId());
			edge.put("to", to.getId());
			
			if(moreInfoEdge) {
				StringBuilder title = new StringBuilder();
				title.append("<b>• ");
				title.append(from.getFullName());
				title.append("<br>• ");
				title.append(to.getFullName());
				title.append("<br><br>");
				title.append(I18n.get("label.enrolledusers"));
				title.append(": ");
				title.append(users.size());
				title.append("</b><br>");
				for(EnrolledUser enrolledUser: users) {
					title.append("<br>• ");
					title.append(enrolledUser.getFullName());
					
				}
				edge.putWithQuote("title", title.toString());
			}else {
				edge.put("title", users.size());
			}
			
			edge.put("value", users.size());
		}
		return edges;
	}

	private JSArray createNodes(Map<Course, Set<EnrolledUser>> courses, boolean useInitialNames, boolean showUsernames) {
		JSArray nodes = new JSArray();
		for(Map.Entry<Course, Set<EnrolledUser>> entry: courses.entrySet()) {
			Course course = entry.getKey();
			Set<EnrolledUser> users = entry.getValue();
			JSObject node = new JSObject();
			nodes.add(node);
			node.put("id", course.getId());
			if (showUsernames) {
				StringBuilder title = new StringBuilder();
				title.append("<b>");
				title.append(course.getFullName());
				title.append("<br><br>");
				title.append(I18n.get("label.enrolledusers"));
				title.append(": ");
				title.append(users.size());
				title.append("</b><br>");
				for(EnrolledUser enrolledUser: users) {
					title.append("<br>• ");
					title.append(enrolledUser.getFullName());
					
				}
				node.putWithQuote("title", title.toString());
			}else {
				node.putWithQuote("title", course.getFullName());
			}
			
			node.put("color", rgb(course.getId() * 31));
			node.put("value", users.size());
			if(useInitialNames) {
				node.putWithQuote("label", WordUtils.initials(course.getFullName(), ' ', '('));
			}
		}
		
		return nodes;
		
		
		
	}
	
	private Set<Course> courseWithEdge(Map<Pair<Course, Course>, List<EnrolledUser>> courseEdges) {
		Set<Course> courses = new HashSet<>();
		for(Pair<Course, Course> pair: courseEdges.keySet()) {
			courses.add(pair.getKey());
			courses.add(pair.getValue());
		}
		return courses;
		
	}

	/**
	 * Return courses where one or more selected users is enrolled
	 * 
	 * @param selectedUsers selected courses
	 * @param courses       courses
	 * @return list of courses where one or more selected is enrolled
	 */
	private Map<Course, Set<EnrolledUser>> coursesWithUser(List<EnrolledUser> selectedUsers,
			Collection<Course> courses) {
		Map<Course, Set<EnrolledUser>> courseWithUser = new HashMap<>();
		Set<EnrolledUser> users = new HashSet<>(selectedUsers);
		for (Course course : courses) {
			Set<EnrolledUser> containsUser = users.stream()
					.filter(u -> course.getEnrolledUsers()
							.contains(u))
					.collect(Collectors.toCollection(() -> new TreeSet<>(EnrolledUser.getNameComparator())));

			if (!containsUser.isEmpty()) {
				courseWithUser.put(course, containsUser);
			}
		}
		return courseWithUser;

	}

	private Map<Pair<Course, Course>, List<EnrolledUser>> courseEdges(Map<Course, Set<EnrolledUser>> coursesEnrolled, int minFrequency) {
		ArrayList<Course> courses = new ArrayList<>(coursesEnrolled.keySet());
		courses.sort((k1, k2)-> coursesEnrolled.get(k2).size() - coursesEnrolled.get(k1).size());
		
		Map<Pair<Course, Course>, List<EnrolledUser>> map = new HashMap<>();
		for (int i = 0; i < courses.size(); i++) {
			Set<EnrolledUser> usersCourseA = coursesEnrolled.get(courses.get(i));
			for (int j = i + 1; j < courses.size(); j++) {
				Set<EnrolledUser> usersCourseB = coursesEnrolled.get(courses.get(j));
				List<EnrolledUser> usersIntersection = usersCourseA.stream()
						.filter(usersCourseB::contains)
						.collect(Collectors.toList());

				if (usersIntersection.size() >= minFrequency) {
					map.put(new Pair<Course, Course>(courses.get(i), courses.get(j)), usersIntersection);
				}
			}
		}
		return map;
	}

}
