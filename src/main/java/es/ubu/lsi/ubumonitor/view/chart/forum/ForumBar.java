package es.ubu.lsi.ubumonitor.view.chart.forum;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
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
import es.ubu.lsi.ubumonitor.view.chart.Chartjs;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;

public class ForumBar extends Chartjs {

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

	}

	@Override
	public void exportCSV(String path) throws IOException {
		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader("courseModuleId",
				"forumName", "discussionId", "discussionName", "posts"))) {
			List<EnrolledUser> users = getSelectedEnrolledUser();
			List<CourseModule> forums = listViewForum.getSelectionModel()
					.getSelectedItems();
			Map<CourseModule, Map<ForumDiscussion, Long>> map = getMap(users, forums);
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

	public Map<CourseModule, Map<ForumDiscussion, Long>> getMap(List<EnrolledUser> users, List<CourseModule> forums) {
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
						Collectors.groupingBy(DiscussionPost::getDiscussion,
								() -> new TreeMap<>(Comparator.comparing(ForumDiscussion::getId)),
								Collectors.counting())));

	}

	@Override
	public JSObject getOptions(JSObject jsObject) {
		boolean useHorizontal = mainConfiguration.getValue(getChartType(), "horizontalMode");
		jsObject.putWithQuote("typeGraph", useHorizontal ? "horizontalBar" : "bar");
		String xLabel = useHorizontal ? getXScaleLabel() : getYScaleLabel();
		String yLabel = useHorizontal ? getYScaleLabel() : getXScaleLabel();
		jsObject.put("scales", "{yAxes:[{" + yLabel + ",ticks:{stepSize:0}}],xAxes:[{" + xLabel
				+ (useHorizontal ? ",ticks:{maxTicksLimit:10}" : "") + "}]}");
		jsObject.put("onClick", null);
		JSObject callbacks = new JSObject();
		callbacks.put("title", "function(a,t){return a[0].xLabel+' ('+a[0].yLabel+')'}");
		callbacks.put("label", "function(e,t){return t.datasets[e.datasetIndex].discussions[e.index]}");
		jsObject.put("tooltips", "{callbacks:" + callbacks + "}");
		return jsObject;

	}

	@Override
	public void update() {
		JSObject options = getOptions();
		String dataset = createDataset(listViewForum.getSelectionModel()
				.getSelectedItems());
		webViewChartsEngine.executeScript("updateChartjs" + "(" + dataset + "," + options + ")");

	}

	private String createDataset(List<CourseModule> forums) {
		List<EnrolledUser> users = getSelectedEnrolledUser();
		Map<CourseModule, Map<ForumDiscussion, Long>> map = getMap(users, forums);
		JSObject data = new JSObject();
		JSArray labels = new JSArray();
		JSObject dataset = new JSObject();
		data.put("labels", labels);
		dataset.putWithQuote("label", I18n.get("tab.forum"));
		dataset.put("backgroundColor", rgba(I18n.get("tab.forum"), OPACITY));
		JSArray dataArray = new JSArray();
		JSArray discussions = new JSArray();
		dataset.put("data", dataArray);
		dataset.put("discussions", discussions);
		for (CourseModule forum : forums) {
			JSArray discussion = new JSArray();
			labels.addWithQuote(forum.getModuleName());
			Map<ForumDiscussion, Long> mapForumDiscussion = map.getOrDefault(forum, Collections.emptyMap());
			long totalPosts = 0;
			for (Map.Entry<ForumDiscussion, Long> entry : mapForumDiscussion.entrySet()) {

				discussion.addWithQuote(entry.getKey()
						.getName() + ": " + entry.getValue());
				totalPosts += entry.getValue();
			}
			discussions.add(discussion);

			dataArray.add(totalPosts);

		}
		data.put("datasets", "[" + dataset + "]");
		return data.toString();
	}

}
