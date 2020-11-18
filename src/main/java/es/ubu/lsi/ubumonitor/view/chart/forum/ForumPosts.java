package es.ubu.lsi.ubumonitor.view.chart.forum;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.DiscussionPost;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.ForumDiscussion;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.Parsers;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.VisNetwork;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;

public class ForumPosts extends VisNetwork {

	private DatePicker datePickerStart;
	private DatePicker datePickerEnd;
	private ListView<CourseModule> listViewForum;

	public ForumPosts(MainController mainController, ListView<CourseModule> listViewForum,
			DatePicker datePickerStart, DatePicker datePickerEnd) {
		super(mainController, ChartType.FORUM_POSTS);
		this.datePickerStart = datePickerStart;
		this.datePickerEnd = datePickerEnd;
		this.listViewForum = listViewForum;
		useRangeDate = true;
	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<EnrolledUser> users = getSelectedEnrolledUser();
		List<CourseModule> forums = new ArrayList<>(listViewForum.getSelectionModel()
				.getSelectedItems());

		Set<DiscussionPost> discussionsPosts = getDiscussionsPosts(users, forums);
		List<DiscussionPost> filteredDiscussionPosts = filterDiscussionPosts(actualCourse.getDiscussionPosts(),
				discussionsPosts.stream()
						.map(DiscussionPost::getDiscussion)
						.distinct()
						.collect(Collectors.toSet()));

		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader("forumId", "forumName", "discussionId", "discussionName", "parentId", "parentName", "discussionPostId", "discussionPostName", "userId",
						"userName",  "timeCreated", "message"))) {
			for (DiscussionPost d : filteredDiscussionPosts) {
				printer.printRecord(
						d.getForum().getCmid(),
						d.getForum().getModuleName(),
						d.getDiscussion().getId(),
						d.getDiscussion().getName(),
						d.getParent().getId(),
						d.getParent().getSubject(),
						d.getId(),
						d.getSubject(),
						d.getUser().getId(),
						d.getUser().getFullName(),
						Controller.DATE_TIME_FORMATTER.format(LocalDateTime.ofInstant(d.getCreated(), ZoneId.systemDefault())),
						d.getMessage());
			}
		}

	}

	@Override
	public JSObject getOptions(JSObject jsObject) {
		JSObject options = new JSObject();

		options.put("edges", getEdgesOptions());
		options.put("nodes", getNodesOptions());
		options.put("physics", getPhysicsOptions());
		options.put("layout", getLayoutOptions());
		options.put("interaction", getInteractionOptions());
		jsObject.put("options", options);

		return jsObject;

	}

	@Override
	public void update() {
		List<EnrolledUser> users = getSelectedEnrolledUser();
		List<CourseModule> forums = new ArrayList<>(listViewForum.getSelectionModel()
				.getSelectedItems());

		Set<DiscussionPost> discussionsPosts = getDiscussionsPosts(users, forums);
		List<DiscussionPost> filteredDiscussionPosts = filterDiscussionPosts(actualCourse.getDiscussionPosts(),
				discussionsPosts.stream()
						.map(DiscussionPost::getDiscussion)
						.distinct()
						.collect(Collectors.toSet()));

		JSArray edges = new JSArray();
		JSArray nodes = new JSArray();
		JSObject node;

		Set<EnrolledUser> userWithParticipation = new HashSet<>();
		Set<CourseModule> forumWithParticipation = new HashSet<>();
		Color colorContains = getConfigValue("colorContains");
		Color colorNotContains = getConfigValue("colorNotContains");

		for (DiscussionPost discussionPost : filteredDiscussionPosts) {
			JSObject edge = new JSObject();
			edges.add(edge);
			edge.put("to", discussionPost.getId());
			edge.putWithQuote("title", discussionPost.getSubject());
			userWithParticipation.add(discussionPost.getUser());
			forumWithParticipation.add(discussionPost.getForum());
			int parentId = discussionPost.getParent()
					.getId();
			if (parentId == 0) {
				edge.put("from", "'f" + discussionPost.getForum()
						.getCmid() + "'");

			} else {
				edge.put("from", parentId);
			}

			node = new JSObject();
			nodes.add(node);
			node.put("id", discussionPost.getId());
			node.put("image", "userPhotos[" + discussionPost.getUser()
					.getId() + "]");
			node.putWithQuote("label", WordUtils.initials(discussionPost.getUser()
					.getFullName()));
			node.putWithQuote("title", createTooltip(discussionPost));
			node.put("color", colorToRGB(discussionsPosts.contains(discussionPost) ? colorContains : colorNotContains));

		}
		for (CourseModule forum : forumWithParticipation) {
			node = new JSObject();
			node.put("id", "'f" + forum.getCmid() + "'");
			node.put("image", "'../img/forum.png'");
			node.put("color", colorToRGB(colorContains));
			node.putWithQuote("label", forum.getModuleName());
			if (StringUtils.isNotBlank(forum.getDescription())) {
				node.putWithQuote("title", forum.getDescription());
			} else {
				node.putWithQuote("title", forum.getModuleName());
			}
			nodes.add(node);
		}

		JSObject userPhotos = new JSObject();
		userWithParticipation.forEach(u -> userPhotos.put(String.valueOf(u.getId()), "'" + u.getImageBase64() + "'"));

		JSObject data = new JSObject();
		data.put("nodes", nodes);
		data.put("edges", edges);

		webViewChartsEngine.executeScript("userPhotos = " + userPhotos);
		webViewChartsEngine.executeScript("updateVisNetwork(" + data + "," + getOptions() + ")");
		webViewChartsEngine.executeScript("userPhotos = null");

	}

	private String createTooltip(DiscussionPost discussionPost) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<b>");
		stringBuilder.append(discussionPost.getSubject());
		stringBuilder.append("<br><br>");
		stringBuilder.append(discussionPost.getUser()
				.getFullName());
		stringBuilder.append("</b> - ");
		stringBuilder.append(LocalDateTime.ofInstant(discussionPost.getCreated(), ZoneId.systemDefault())
				.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.SHORT)));
		stringBuilder.append("<br><hr><br>");
		stringBuilder.append(Parsers.changeImages(Parsers.parseToHtml(discussionPost.getMessage(), discussionPost.getMessageformat()), "../img/image_not_available.png"));
		return stringBuilder.toString();
	}

	@Override
	public JSObject getNodesOptions() {

		JSObject nodes = super.getNodesOptions();
		if ((boolean) getConfigValue("usePhoto")) {
			nodes.put("shape", "'circularImage'");

		} else {
			nodes.put("shape", "'circle'");
			nodes.put("brokenImage", "undefined");
		}
		nodes.put("borderWidth", getConfigValue("nodes.borderWidth"));
		return nodes;
	}

	@Override
	public JSObject getLayoutOptions() {

		JSObject layout = super.getLayoutOptions();
		layout.put("hierarchical", "{direction:'UD',sortMethod:'directed',shakeTowards:'roots'}");
		return layout;
	}

	@Override
	public JSObject getEdgesOptions() {

		JSObject edges = super.getEdgesOptions();
		JSObject smooth = new JSObject();
		smooth.put("type", "'cubicBezier'");
		smooth.put("forceDirection", "'vertical'");
		smooth.put("roundness", 0.4);
		edges.put("smooth", smooth);
		edges.put("color", "{inherit:'both'}");
		edges.put("width", getConfigValue("edges.width"));
		return edges;
	}

	@Override
	public JSObject getPhysicsOptions() {

		JSObject physics = super.getPhysicsOptions();
		physics.put("enabled", false);
		return physics;
	}

	public Set<DiscussionPost> getDiscussionsPosts(Collection<EnrolledUser> users, Collection<CourseModule> forums) {
		Instant start = datePickerStart.getValue()
				.atStartOfDay(ZoneId.systemDefault())
				.toInstant();
		Instant end = datePickerEnd.getValue()
				.plusDays(1)
				.atStartOfDay(ZoneId.systemDefault())
				.toInstant();

		return actualCourse.getDiscussionPosts()
				.stream()
				.filter(discussionPost -> forums.contains(discussionPost.getForum())
						&& users.contains(discussionPost.getUser()) && start.isBefore(discussionPost.getCreated())
						&& end.isAfter(discussionPost.getCreated()))

				.collect(Collectors.toSet());

	}

	private List<DiscussionPost> filterDiscussionPosts(Collection<DiscussionPost> discussionPosts,
			Collection<ForumDiscussion> discussions) {
		return discussionPosts.stream()
				.filter(dp -> discussions.contains(dp.getDiscussion()))
				.sorted(Comparator.comparing(DiscussionPost::getCreated))
				.collect(Collectors.toList());
	}

}
