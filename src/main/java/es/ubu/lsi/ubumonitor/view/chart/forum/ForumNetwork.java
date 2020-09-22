package es.ubu.lsi.ubumonitor.view.chart.forum;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.DiscussionPost;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.VisNetwork;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;

public class ForumNetwork extends VisNetwork {

	private static final Pattern INITIAL_LETTER_PATTERN = Pattern.compile("\\b\\w|,\\s");
	private ListView<CourseModule> listViewForum;
	private DatePicker datePickerStart;
	private DatePicker datePickerEnd;

	public ForumNetwork(MainController mainController, WebView webView, ListView<CourseModule> listViewForum,
			DatePicker datePickerStart, DatePicker datePickerEnd) {
		super(mainController, ChartType.FORUM_NETWORK, webView);
		this.listViewForum = listViewForum;
		this.datePickerStart = datePickerStart;
		this.datePickerEnd = datePickerEnd;
		useRangeDate = true;

	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		Set<CourseModule> selectedForums = new HashSet<>(listViewForum.getSelectionModel()
				.getSelectedItems());
		List<DiscussionPost> discussionPosts = getSelectedDiscussionPosts(enrolledUsers, selectedForums);

		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader("fromId", "fromName", "toId", "toName", "countPostsReplies"))) {
			for (EnrolledUser from : enrolledUsers) {
				for (EnrolledUser to : enrolledUsers) {
					long countPosts = discussionPosts.stream()
							.filter(discussionPost -> from.equals(discussionPost.getUser())
									&& discussionPost.getParent() != null && to.equals(discussionPost.getParent()
											.getUser()))
							.count();
					if (countPosts > 0) {
						printer.printRecord(from.getId(), from.getFullName(), to.getId(), to.getFullName(), countPosts);
					}

				}

			}
		}

	}

	@Override
	public JSObject getOptions(JSObject jsObject) {
		JSObject options = new JSObject();

		jsObject.put("physicsAfterDraw", getConfigValue("physicsAfterDraw"));
		options.put("edges", getEdgesOptions());
		options.put("nodes", getNodesOptions());
		options.put("physics", getPhysicsOptions());
		options.put("interaction", getInteractionOptions());
		options.put("layout", getLayoutOptions());
		jsObject.put("options", options);

		return jsObject;
	}

	private JSObject getEdgesOptions() {
		JSObject edges = new JSObject();
		edges.put("arrows", "{to:{enabled:true,scaleFactor:" + getConfigValue("edges.arrows.to.scaleFactor") + "}}");
		edges.put("arrowStrikethrough", false);
		edges.put("dashes", getConfigValue("edges.dashes"));
		JSObject scaling = new JSObject();
		scaling.put("max", getConfigValue("edges.scaling.max"));
		scaling.put("min", getConfigValue("edges.scaling.min"));
		edges.put("scaling", scaling);
		return edges;
	}

	private JSObject getNodesOptions() {
		JSObject nodes = new JSObject();
		if ((boolean) getConfigValue("usePhoto")) {
			nodes.put("shape", "'circularImage'");
			nodes.put("brokenImage", "'../img/default_user.png'");
		} else {
			nodes.put("shape", "'circle'");
		}
		nodes.put("borderWidth", getConfigValue("nodes.borderWidth"));
		JSObject scaling = new JSObject();
		scaling.put("max", getConfigValue("nodes.scaling.max"));
		scaling.put("min", getConfigValue("nodes.scaling.min"));
		nodes.put("scaling", scaling);
		nodes.put("font", "{multi:true,bold:{size:16}}");
		return nodes;
	}

	private JSObject getPhysicsOptions() {
		JSObject physics = new JSObject();
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

	private JSObject getInteractionOptions() {
		JSObject interaction = new JSObject();
		interaction.put("keyboard", getConfigValue("interaction.keyboard"));
		interaction.put("multiselect", getConfigValue("interaction.multiselect"));
		interaction.put("navigationButtons", getConfigValue("interaction.navigationButtons"));
		interaction.put("tooltipDelay", getConfigValue("interaction.tooltipDelay"));
		return interaction;
	}

	private JSObject getLayoutOptions() {
		JSObject layout = new JSObject();
		String randomSeed = getConfigValue("layout.randomSeed");

		layout.put("randomSeed", StringUtils.isBlank(randomSeed) ? "undefined" : randomSeed);

		layout.put("clusterThreshold", getConfigValue("layout.clusterThreshold"));
		return layout;
	}

	@Override
	public void update() {

		List<EnrolledUser> users = getSelectedEnrolledUser();
		Set<CourseModule> selectedForums = new HashSet<>(listViewForum.getSelectionModel()
				.getSelectedItems());
		List<DiscussionPost> discussionPosts = getSelectedDiscussionPosts(users, selectedForums);

		Map<EnrolledUser, Map<EnrolledUser, Long>> map = discussionPosts.stream()
				.collect(Collectors.groupingBy(DiscussionPost::getUser, Collectors.groupingBy(dp -> dp.getParent()
						.getUser(), Collectors.counting())));

		Set<EnrolledUser> usersWithEdges = new HashSet<>();
		Map<EnrolledUser, Long> fromMap = new HashMap<>();
		Map<EnrolledUser, Long> toMap = new HashMap<>();
		Map<EnrolledUser, Long> toSelfMap = new HashMap<>();
		Map<EnrolledUser, Long> discussionCreations = actualCourse.getDiscussionPosts()
				.stream()
				.filter(d -> d.getParent()
						.getId() == 0 && selectedForums.contains(d.getForum()) && users.contains(d.getUser()))
				.collect(Collectors.groupingBy(DiscussionPost::getUser, Collectors.counting()));
		usersWithEdges.addAll(discussionCreations.keySet());
		JSObject data = new JSObject();

		JSArray edges = new JSArray();

		for (Map.Entry<EnrolledUser, Map<EnrolledUser, Long>> entry : map.entrySet()) {
			EnrolledUser from = entry.getKey();
			usersWithEdges.add(from);
			for (Map.Entry<EnrolledUser, Long> toEntry : entry.getValue()
					.entrySet()) {
				EnrolledUser to = toEntry.getKey();
				Long countPosts = toEntry.getValue();
				JSObject edge = new JSObject();
				if (from.equals(to)) {
					addCountPosts(toSelfMap, to, countPosts);
					edge.put("arrows", "{to:{type:'curve'}}");
				} else {
					addCountPosts(fromMap, from, countPosts);
					addCountPosts(toMap, to, countPosts);
				}

				usersWithEdges.add(to);

				if (countPosts > 0) {

					edge.put("from", from.getId());
					edge.put("to", to.getId());
					edge.put("title", countPosts);
					edge.put("value", countPosts);
					edges.add(edge);
				}
			}

		}

		boolean showNonConnected = getConfigValue("showNonConnected");

		data.put("nodes",

				createNodes(showNonConnected ? users : usersWithEdges, fromMap, toMap, toSelfMap, discussionCreations));
		data.put("edges", edges);
		webViewChartsEngine.executeScript("updateVisNetwork(" + data + "," + getOptions() + ")");
	}

	private JSArray createNodes(Collection<EnrolledUser> users, Map<EnrolledUser, Long> fromMap,
			Map<EnrolledUser, Long> toMap, Map<EnrolledUser, Long> toSelf,
			Map<EnrolledUser, Long> discussionCreations) {
		JSArray nodes = new JSArray();

		for (EnrolledUser user : users) {

			JSObject node = new JSObject();
			long fromValue = fromMap.getOrDefault(user, 0L);
			long toValue = toMap.getOrDefault(user, 0L);
			long discussionCreated = discussionCreations.getOrDefault(user, 0L);
			long toSelfValue = toSelf.getOrDefault(user, 0L);
			nodes.add(node);
			node.put("id", user.getId());
			node.putWithQuote("title", user.getFullName());
			node.put("color", rgb(user.getId() * 31));
			node.put("image", "'" + user.getImageBase64() + "'");

			if (fromValue + toValue + toSelfValue + discussionCreated != 0) {
				StringBuilder builder = new StringBuilder();
				builder.append("'");
				if ((boolean) getConfigValue("useInitialNames")) {
					initialLetterNames(user, builder);
				}

				long total = 0;
				double weightSendPost = getConfigValue("nodes.weightSendPost");
				double weightReceivePost = getConfigValue("nodes.weightReceivePost");
				double weightSelfPost = getConfigValue("nodes.weightSelfPost");
				double weightDiscussionCreation = getConfigValue("nodes.weightDiscussionCreation");

				if ((boolean) getConfigValue("showNumberPosts")) {
					StringJoiner stringJoiner = new StringJoiner(", ", " (", ")");
					addValueToLabel(fromValue, stringJoiner, "<b>⬈</b>");
					addValueToLabel(toValue, stringJoiner, "<b>⬋</b>");
					addValueToLabel(toSelfValue, stringJoiner, "<b>⟳</b>");
					addValueToLabel(discussionCreated, stringJoiner, "<b>☝</b>");
					builder.append(stringJoiner);
				}

				total += fromValue * weightSendPost;

				total += toValue * weightReceivePost;

				total += toSelfValue * weightSelfPost;

				total += discussionCreated * weightDiscussionCreation;

				builder.append("'");

				node.put("value", total);
				node.put("label", builder);
			}
		}
		return nodes;
	}

	public void addValueToLabel(long fromValue, StringJoiner stringJoiner, String icon) {
		if (fromValue != 0) {
			stringJoiner.add(fromValue + icon);
		}
	}

	public void initialLetterNames(EnrolledUser user, StringBuilder builder) {
		Matcher m = INITIAL_LETTER_PATTERN.matcher(user.getFullName());

		while (m.find()) {
			builder.append(m.group());
		}
	}

	private void addCountPosts(Map<EnrolledUser, Long> map, EnrolledUser user, Long posts) {
		Long actualPosts = map.get(user);
		if (actualPosts == null) {
			map.put(user, posts);
		} else {
			map.put(user, actualPosts + posts);
		}
	}

	public List<DiscussionPost> getSelectedDiscussionPosts(Collection<EnrolledUser> selectedUsers,
			Collection<CourseModule> selectedForums) {

		Set<EnrolledUser> users = new HashSet<>(selectedUsers);
		Instant start = datePickerStart.getValue()
				.atStartOfDay(ZoneId.systemDefault())
				.toInstant();
		Instant end = datePickerEnd.getValue()
				.atStartOfDay(ZoneId.systemDefault())
				.toInstant();
		return actualCourse.getDiscussionPosts()
				.stream()

				.filter(discussionPost -> selectedForums.contains(discussionPost.getForum())
						&& users.contains(discussionPost.getUser()) && users.contains(discussionPost.getParent()
								.getUser())
						&& start.isBefore(discussionPost.getCreated()) && end.isAfter(discussionPost.getCreated()))
				.collect(Collectors.toList());
	}

	public enum Solver {
		BARNES_HUT("barnesHut"), FORCE_ATLAS_2_BASED("forceAtlas2Based"), REPULSION("repulsion");

		private String name;

		private Solver(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return I18n.get(name());
		}
	}
}
