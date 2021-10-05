package es.ubu.lsi.ubumonitor.view.chart.forum;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.DiscussionPost;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.ManageDuplicate;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;

public class ForumUserPostBar extends Plotly {

	private ListView<CourseModule> listViewForums;
	private DatePicker datePickerStart;
	private DatePicker datePickerEnd;

	public ForumUserPostBar(MainController mainController, ListView<CourseModule> listViewForums,
			DatePicker datePickerStart, DatePicker datePickerEnd) {
		super(mainController, ChartType.FORUM_USER_POST_BAR);
		this.listViewForums = listViewForums;
		this.datePickerStart = datePickerStart;
		this.datePickerEnd = datePickerEnd;
		useRangeDate = true;
		useLegend = true;

	}

	@Override
	public void createData(JSArray data) {
		Set<EnrolledUser> users = new LinkedHashSet<>(getSelectedEnrolledUser());
		Set<CourseModule> selectedForums = new LinkedHashSet<>(listViewForums.getSelectionModel()
				.getSelectedItems());
		Map<EnrolledUser, Map<CourseModule, List<DiscussionPost>>> map = getUserDiscussionPosts(users, selectedForums);
		createTraces(data, users, selectedForums, I18n.get("text.replies"), I18n.get("text.discussioncreation"),
				getConfigValue("text.replies"), getConfigValue("text.discussioncreation"), map,
				getConfigValue("horizontalMode"));
	}

	private void createTraces(JSArray data, Set<EnrolledUser> users, Set<CourseModule> forums, String nameReplies,
			String nameDiscussionCreation, Color colorReplies, Color colorDiscussionCreation,
			Map<EnrolledUser, Map<CourseModule, List<DiscussionPost>>> map, boolean horizontalMode) {

		ManageDuplicate manageDuplicate = new ManageDuplicate();
		JSArray yDiscussionCreated = new JSArray();
		JSArray yReplies = new JSArray();
		JSArray textDiscussionCreated = new JSArray();
		JSArray textReplies = new JSArray();
		JSArray x = new JSArray();
		JSArray userids = new JSArray();
		for (EnrolledUser user : users) {

			Map<CourseModule, List<DiscussionPost>> mapForums = map.get(user);
			if(mapForums == null) {
				continue;
			}
			long totalReplies = 0;
			long totalDiscussionCreation = 0;
			StringBuilder discussionCreatedString = new StringBuilder();
			StringBuilder repliesString = new StringBuilder();
			for (CourseModule forum : forums) {
				List<DiscussionPost> discussionPosts = mapForums.getOrDefault(forum, Collections.emptyList());
				long discussionCreationValue = discussionPosts.stream()
						.filter(d -> d.getParent()
								.getId() == 0)
						.count();
				long repliesValue = discussionPosts.size() - discussionCreationValue;

				if (repliesValue != 0) {
					createForumPostString(repliesString, forum.getModuleName(), repliesValue);

				}

				if (discussionCreationValue != 0) {

					createForumPostString(discussionCreatedString, forum.getModuleName(), discussionCreationValue);

				}

				totalReplies += repliesValue;
				totalDiscussionCreation += discussionCreationValue;
			}
			yReplies.add(totalReplies);
			yDiscussionCreated.add(totalDiscussionCreation);
			textReplies.addWithQuote(repliesString);
			textDiscussionCreated.addWithQuote(discussionCreatedString);
			x.addWithQuote(manageDuplicate.getValue(user.getFullName()));
			userids.add(user.getId());
		}

		data.add(createTrace(nameReplies, colorReplies, x, yReplies, textReplies, userids, horizontalMode));
		data.add(createTrace(nameDiscussionCreation, colorDiscussionCreation, x, yDiscussionCreated,
				textDiscussionCreated, userids, horizontalMode));
	}

	public void createForumPostString(StringBuilder stringBuilder, String forumName, long value) {
		stringBuilder.append(" • ");
		stringBuilder.append(forumName);
		stringBuilder.append(": ");
		stringBuilder.append(value);
		stringBuilder.append("<br>");
	}

	private JSObject createTrace(String name, Color color, JSArray x, JSArray y, JSArray text, JSArray userids,
			boolean horizontalMode) {
		JSObject trace = new JSObject();
		Plotly.createAxisValuesHorizontal(horizontalMode, trace, x, y);

		trace.put("type", "'bar'");
		trace.putWithQuote("name", name);
		JSObject marker = new JSObject();
		marker.put("color", colorToRGB(color));
		trace.put("marker", marker);
		trace.put("text", text);
		trace.put("userids", userids);

		trace.put("hovertemplate", "'<b>%{" + (horizontalMode ? "y" : "x") + "}<br>%{data.name}: </b>%{"
				+ (horizontalMode ? "x" : "y") + "}<br><br>%{text}<extra></extra>'");

		return trace;
	}

	@Override
	public void createLayout(JSObject layout) {
		boolean horizontalMode = getConfigValue("horizontalMode");

		JSObject xaxis = new JSObject();
		JSObject yaxis = new JSObject();

		if (horizontalMode) {
			Plotly.defaultAxisValues(xaxis, getXAxisTitle() , "");
			Plotly.defaultAxisValues(yaxis, getYAxisTitle(), null);
			yaxis.put("type", "'category'");
			layout.put("xaxis", yaxis);
			layout.put("yaxis", xaxis);
		} else {
			Plotly.defaultAxisValues(xaxis, getXAxisTitle(), null);
			Plotly.defaultAxisValues(yaxis, getYAxisTitle(), "");
			xaxis.put("type", "'category'");
			layout.put("xaxis", xaxis);
			layout.put("yaxis", yaxis);
		}
		layout.put("barmode", "'stack'");
		layout.put("hovermode", "'closest'");

	}

	@Override
	public void exportCSV(String path) throws IOException {

		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader("userId", "userName",
				"forumId", "forumName", "countDiscussionCreation", "countPostsReplies"))) {
			Set<EnrolledUser> users = new LinkedHashSet<>(getSelectedEnrolledUser());
			Set<CourseModule> selectedForums = new LinkedHashSet<>(listViewForums.getSelectionModel()
					.getSelectedItems());
			Map<EnrolledUser, Map<CourseModule, List<DiscussionPost>>> map = getUserDiscussionPosts(users,
					selectedForums);
			for (Map.Entry<EnrolledUser, Map<CourseModule, List<DiscussionPost>>> entry : map.entrySet()) {
				EnrolledUser user = entry.getKey();
				printer.print(user.getId());
				printer.print(user.getFullName());
				for (Map.Entry<CourseModule, List<DiscussionPost>> entryCourseModule : entry.getValue()
						.entrySet()) {
					long discussionCreationValue = entryCourseModule.getValue()
							.stream()
							.filter(d -> d.getParent()
									.getId() == 0)
							.count();
					long repliesValue = entryCourseModule.getValue()
							.size() - discussionCreationValue;
					printer.print(entryCourseModule.getKey()
							.getCmid());
					printer.print(entryCourseModule.getKey()
							.getModuleName());
					printer.print(discussionCreationValue);
					printer.print(repliesValue);
					printer.println();
				}
			}
		}
	}

	public Map<EnrolledUser, Map<CourseModule, List<DiscussionPost>>> getUserDiscussionPosts(
			Collection<EnrolledUser> users, Collection<CourseModule> forums) {
		Instant start = datePickerStart.getValue()
				.atStartOfDay(ZoneId.systemDefault())
				.toInstant();
		Instant end = datePickerEnd.getValue()
				.plusDays(1)
				.atStartOfDay(ZoneId.systemDefault())
				.toInstant();
		return actualCourse.getDiscussionPosts()
				.stream()
				.filter(discussionPost -> users.contains(discussionPost.getUser())
						&& forums.contains(discussionPost.getForum()) && start.isBefore(discussionPost.getCreated())
						&& end.isAfter(discussionPost.getCreated()))
				.collect(Collectors.groupingBy(DiscussionPost::getUser,
						Collectors.groupingBy(DiscussionPost::getForum, Collectors.toList())));

	}

}
