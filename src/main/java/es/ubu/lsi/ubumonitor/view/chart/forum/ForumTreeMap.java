package es.ubu.lsi.ubumonitor.view.chart.forum;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;

public class ForumTreeMap extends Plotly {

	private ListView<CourseModule> forumListView;
	private DatePicker datePickerStart;
	private DatePicker datePickerEnd;

	public ForumTreeMap(MainController mainController, ListView<CourseModule> forumListView,
			DatePicker datePickerStart, DatePicker datePickerEnd) {
		super(mainController, ChartType.FORUM_TREE_MAP);
		this.forumListView = forumListView;
		this.datePickerStart = datePickerStart;
		this.datePickerEnd = datePickerEnd;
		useRangeDate = true;

	}



	

	

	private JSArray createJSArray(String key, JSObject data) {
		JSArray jsArray = new JSArray();
		data.put(key, jsArray);
		return jsArray;
	}

	public Map<CourseModule, Map<EnrolledUser, Long>> getMap(Collection<EnrolledUser> users,
			Collection<CourseModule> forums) {
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
						(Collectors.groupingBy(DiscussionPost::getUser, Collectors.counting()))));

	}

	@Override
	public void createData(JSArray dataArray) {
		List<EnrolledUser> users = getSelectedEnrolledUser();
		List<CourseModule> forums = new ArrayList<>(forumListView.getSelectionModel()
				.getSelectedItems());
		Map<CourseModule, Map<EnrolledUser, Long>> map = getMap(users, forums);
		JSObject data = new JSObject();
		data.put("type", "'treemap'");
		data.put("branchvalues", "'total'");

		if (map.size() > 0) {
			data.put("texttemplate",
					"'<b>%{label}</b><br>%{value}<br>%{percentParent} "
							+ UtilMethods.escapeJavaScriptText(I18n.get("user")) + "<br>%{percentRoot} "
							+ UtilMethods.escapeJavaScriptText(I18n.get("root")) + "'");
			data.put("hovertemplate",
					"'<b>%{label}</b><br>%{value}<br>%{percentParent:%} %{parent}<br>%{percentRoot:%} %{root}<extra></extra>'");
		}

		JSArray labels = createJSArray("labels", data);

		JSArray values = createJSArray("values", data);

		JSArray parents = createJSArray("parents", data);
		JSArray ids = createJSArray("ids", data);
		long courseTotal = 0;
		for (Map.Entry<CourseModule, Map<EnrolledUser, Long>> entry : map.entrySet()) {
			CourseModule forum = entry.getKey();
			long forumTotal = 0;
			for (Map.Entry<EnrolledUser, Long> entryUser : entry.getValue()
					.entrySet()) {

				EnrolledUser user = entryUser.getKey();
				long posts = entryUser.getValue();
				labels.addWithQuote(user.getFullName());
				values.add(posts);
				parents.add(forum.getCmid());
				ids.add("[" + forum.getCmid() + "," + user.getId() + "]");
				forumTotal += posts;
				courseTotal += posts;

			}
			labels.addWithQuote(forum.getModuleName());
			values.add(forumTotal);
			parents.add(0);
			ids.add(forum.getCmid());

		}
		labels.addWithQuote(actualCourse.getFullName());
		values.add(courseTotal);
		parents.add("''");
		ids.add(0);
		dataArray.add(data);
		
	}

	@Override
	public void createLayout(JSObject layout) {
		// do nothing
		
	}
	
	@Override
	public void exportCSV(String path) throws IOException {
		List<EnrolledUser> users = getSelectedEnrolledUser();
		List<CourseModule> forums = new ArrayList<>(forumListView.getSelectionModel()
				.getSelectedItems());
		Map<CourseModule, Map<EnrolledUser, Long>> map = getMap(users, forums);
		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader("forumId", "forumName", "userId", "userName", "posts"))) {
			for (Map.Entry<CourseModule, Map<EnrolledUser, Long>> entry : map.entrySet()) {
				CourseModule forum = entry.getKey();

				for (Map.Entry<EnrolledUser, Long> entryUser : entry.getValue()
						.entrySet()) {
					EnrolledUser user = entryUser.getKey();
					printer.printRecord(forum.getCmid(), forum.getModuleName(), user.getId(), user.getFullName(),
							entryUser.getValue());

				}

			}
		}
	}

}
