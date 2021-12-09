package es.ubu.lsi.ubumonitor.view.chart.forum;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.DiscussionPost;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.ForumDiscussion;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;

public class ForumBar extends Plotly {

	private ListView<CourseModule> listViewForum;
	private DatePicker datePickerStart;
	private DatePicker datePickerEnd;

	public ForumBar(MainController mainController, ListView<CourseModule> listViewForum, DatePicker datePickerStart,
			DatePicker datePickerEnd) {
		super(mainController, ChartType.FORUM_BAR);
		this.listViewForum = listViewForum;
		this.datePickerStart = datePickerStart;
		this.datePickerEnd = datePickerEnd;
		useRangeDate = true;
		useLegend = true;

	}

	@Override
	public void createData(JSArray data) {
		List<EnrolledUser> users = getSelectedEnrolledUser();
		List<CourseModule> forums = new ArrayList<>(listViewForum.getSelectionModel()
				.getSelectedItems());
		Map<CourseModule, Map<ForumDiscussion, Long>> map = createForumDiscussionCounts(users, forums);
		data.add(createTrace(I18n.get("tab.forum"), forums, map, getConfigValue("forumBarColor"),
				getConfigValue("horizontalMode")));
	}

	@Override
	public void createLayout(JSObject layout) {
		boolean horizontalMode = getConfigValue("horizontalMode");
		List<CourseModule> forums = new ArrayList<>(listViewForum.getSelectionModel()
				.getSelectedItems());

		JSArray ticktext = new JSArray();

		for (CourseModule forum : forums) {
			ticktext.addWithQuote(forum.getModuleName());
		}

		horizontalMode(layout, ticktext, horizontalMode, getXAxisTitle(), getYAxisTitle(), null);

		layout.put("hovermode", "'x unified'");

	}

	public JSObject createTrace(String name, List<CourseModule> forums,
			Map<CourseModule, Map<ForumDiscussion, Long>> map, Color color, boolean horizontalMode) {
		JSObject trace = new JSObject();
		JSArray x = new JSArray();
		JSArray y = new JSArray();
		JSArray discussions = new JSArray();
		for (int i = 0; i < forums.size(); ++i) {
			CourseModule forum = forums.get(i);
			Map<ForumDiscussion, Long> mapForumDiscussion = map.getOrDefault(forum, Collections.emptyMap());
			long totalPosts = 0;
			StringBuilder discussionString = new StringBuilder();
			for (Map.Entry<ForumDiscussion, Long> entry : mapForumDiscussion.entrySet()) {
				discussionString.append(entry.getKey()
						.getName());
				discussionString.append(": ");
				discussionString.append(entry.getValue());
				discussionString.append("<br>");
				totalPosts += entry.getValue();
			}

			x.add(i);
			y.add(totalPosts);
			discussions.addWithQuote(discussionString);
		}

		Plotly.createAxisValuesHorizontal(horizontalMode, trace, x, y);

		trace.put("type", "'bar'");
		trace.putWithQuote("name", name);
		JSObject marker = new JSObject();
		marker.put("color", colorToRGB(color));
		trace.put("marker", marker);
		trace.put("customdata", discussions);

		trace.put("hovertemplate", "'<br>%{customdata}<br><b>" + I18n.get("text.total") + ":</b> %{" + (horizontalMode ? "x" : "y")
				+ "}<extra></extra>'");

		return trace;
	}

	public Map<CourseModule, Map<ForumDiscussion, Long>> createForumDiscussionCounts(List<EnrolledUser> users,
			List<CourseModule> forums) {
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
				.collect(Collectors.groupingBy(DiscussionPost::getForum,
						Collectors.groupingBy(DiscussionPost::getDiscussion,
								() -> new TreeMap<>(Comparator.comparing(ForumDiscussion::getId)),
								Collectors.counting())));

	}

	@Override
	public void exportCSV(String path) throws IOException {
		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader("courseModuleId",
				"forumName", "discussionId", "discussionName", "posts"))) {
			List<EnrolledUser> users = getSelectedEnrolledUser();
			List<CourseModule> forums = listViewForum.getSelectionModel()
					.getSelectedItems();
			Map<CourseModule, Map<ForumDiscussion, Long>> map = createForumDiscussionCounts(users, forums);
			for (CourseModule forum : forums) {
				Map<ForumDiscussion, Long> mapForumDiscussion = map.getOrDefault(forum, Collections.emptyMap());
				for (Map.Entry<ForumDiscussion, Long> entry : mapForumDiscussion.entrySet()) {
					ForumDiscussion forumDiscussion = entry.getKey();
					printer.printRecord(forum.getCmid(), forum.getModuleName(), forumDiscussion.getId(),
							forumDiscussion.getName(), entry.getValue());
				}
			}
		}
	}

}
