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
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;

public class ForumTreeMap extends Plotly {

	private ListView<CourseModule> forumListView;
	private DatePicker datePickerStart;
	private DatePicker datePickerEnd;

	public ForumTreeMap(MainController mainController, WebView webView, ListView<CourseModule> forumListView,
			DatePicker datePickerStart, DatePicker datePickerEnd) {
		super(mainController, ChartType.FORUM_TREE_MAP, webView);
		this.forumListView = forumListView;
		this.datePickerStart = datePickerStart;
		this.datePickerEnd = datePickerEnd;
		useRangeDate = true;

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

	@Override
	public JSObject getOptions(JSObject jsObject) {
		return jsObject;
	}

	@Override
	public void update() {
		List<EnrolledUser> users = getSelectedEnrolledUser();
		List<CourseModule> forums = new ArrayList<>(forumListView.getSelectionModel()
				.getSelectedItems());
		Map<CourseModule, Map<EnrolledUser, Long>> map = getMap(users, forums);
		JSObject data = new JSObject();
		data.put("type", "'treemap'");
		data.put("branchvalues", "'total'");
		data.put("textinfo", "'label+value+percent parent+percent root'");
		data.put("hoverinfo", "'label+value+percent parent+percent root'");
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
		parents.add(null);
		ids.add(0);

		webViewChartsEngine.executeScript("updatePlotly([" + data + "]," + getOptions() + ")");

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
		Instant end = datePickerEnd.getValue().plusDays(1)
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

}
