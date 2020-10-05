package es.ubu.lsi.ubumonitor.view.chart.forum;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.DiscussionPost;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Chartjs;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;

public class ForumUserPostBar extends Chartjs {

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

	}

	@Override
	public void exportCSV(String path) throws IOException {

		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader("userId", "userName",
				"forumId", "forumName", "countDiscussionCreation", "countPostsReplies"))) {
			Set<EnrolledUser> users = new LinkedHashSet<>(getSelectedEnrolledUser());
			Set<CourseModule> selectedForums = new LinkedHashSet<>(listViewForums.getSelectionModel()
					.getSelectedItems());
			Map<EnrolledUser, Map<CourseModule, List<DiscussionPost>>> map = getMap(users, selectedForums);
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

	@Override
	public JSObject getOptions(JSObject jsObject) {
		boolean useHorizontal = mainConfiguration.getValue(getChartType(), "horizontalMode");
		jsObject.putWithQuote("typeGraph", useHorizontal ? "horizontalBar" : "bar");
		String xLabel = useHorizontal ? getXScaleLabel() : getYScaleLabel();
		String yLabel = useHorizontal ? getYScaleLabel() : getXScaleLabel();
		jsObject.put("scales", "{yAxes:[{" + yLabel + ",stacked:true,ticks:{stepSize:0}}],xAxes:[{" + xLabel
				+ (useHorizontal ? ",,stacked:true,ticks:{maxTicksLimit:10}" : "") + "}]}");
		jsObject.put("onClick",
				"function(t,e){if(e.length>0)javaConnector.dataPointSelection(e[0]._chart.data.userids[e[0]._index])}");
		JSObject callbacks = new JSObject();
		callbacks.put("title", "function(a,t){return a[0].xLabel+' ('+a[0].yLabel+')'}");
		callbacks.put("label", "function(e,t){return t.datasets[e.datasetIndex].forums[e.index]}");
		jsObject.put("tooltips", "{callbacks:" + callbacks + "}");

		return jsObject;
	}

	@Override
	public void update() {

		JSObject options = getOptions();
		String dataset = createDataset();

		webViewChartsEngine.executeScript("updateChartjs" + "(" + dataset + "," + options + ")");

	}

	private String createDataset() {
		Set<EnrolledUser> users = new LinkedHashSet<>(getSelectedEnrolledUser());
		Set<CourseModule> selectedForums = new LinkedHashSet<>(listViewForums.getSelectionModel()
				.getSelectedItems());
		Map<EnrolledUser, Map<CourseModule, List<DiscussionPost>>> map = getMap(users, selectedForums);
		JSObject data = new JSObject();
		JSArray labels = new JSArray();

		JSArray userids = new JSArray();

		data.put("labels", labels);
		data.put("userids", userids);

		JSObject replies = new JSObject();
		JSArray dataReplies = new JSArray();
		replies.put("data", dataReplies);
		replies.putWithQuote("label", I18n.get("text.replies"));
		replies.put("backgroundColor", colorToRGB(getConfigValue("text.replies")));
		JSArray forumsReplies = new JSArray();
		replies.put("forums", forumsReplies);
		replies.put("stack", 0);

		JSObject discussionCreation = new JSObject();
		JSArray dataDiscussionCreation = new JSArray();
		discussionCreation.put("data", dataDiscussionCreation);
		discussionCreation.putWithQuote("label", I18n.get("text.discussioncreation"));
		discussionCreation.put("backgroundColor", colorToRGB(getConfigValue("text.discussioncreation")));
		JSArray forumDiscussion = new JSArray();
		discussionCreation.put("forums", forumDiscussion);
		discussionCreation.put("stack", 0);

		for (Map.Entry<EnrolledUser, Map<CourseModule, List<DiscussionPost>>> entry : map.entrySet()) {
			EnrolledUser user = entry.getKey();
			labels.addWithQuote(user.getFullName());
			userids.add(user.getId());
			JSArray forumReplies = new JSArray();
			JSArray discussion = new JSArray();
			forumDiscussion.add(discussion);
			forumsReplies.add(forumReplies);

			long totalReplies = 0;
			long totalDiscussionCreation = 0;
			for (Map.Entry<CourseModule, List<DiscussionPost>> entryCourseModule : entry.getValue()
					.entrySet()) {

				long discussionCreationValue = entryCourseModule.getValue()
						.stream()
						.filter(d -> d.getParent()
								.getId() == 0)
						.count();
				long repliesValue = entryCourseModule.getValue()
						.size() - discussionCreationValue;

				if (repliesValue != 0) {
					forumReplies.addWithQuote(entryCourseModule.getKey()
							.getModuleName() + ": " + repliesValue);
				}

				if (discussionCreationValue != 0) {
					discussion.addWithQuote(entryCourseModule.getKey()
							.getModuleName() + ": " + discussionCreationValue);
				}

				totalReplies += repliesValue;
				totalDiscussionCreation += discussionCreationValue;
			}
			dataReplies.add(totalReplies);
			dataDiscussionCreation.add(totalDiscussionCreation);
		}

		data.put("datasets", "[" + replies + "," + discussionCreation + "]");

		return data.toString();
	}

	@Override
	public int onClick(int userid) {

		EnrolledUser user = Controller.getInstance()
				.getDataBase()
				.getUsers()
				.getById(userid);
		return getUsers().indexOf(user);
	}

	public Map<EnrolledUser, Map<CourseModule, List<DiscussionPost>>> getMap(Collection<EnrolledUser> users,
			Collection<CourseModule> forums) {
		Instant start = datePickerStart.getValue()
				.atStartOfDay(ZoneId.systemDefault())
				.toInstant();
		Instant end = datePickerEnd.getValue().plusDays(1)
				.atStartOfDay(ZoneId.systemDefault())
				.toInstant();
		return actualCourse.getDiscussionPosts()
				.stream()
				.filter(discussionPost -> users.contains(discussionPost.getUser())
						&& forums.contains(discussionPost.getForum()) && start.isBefore(discussionPost.getCreated())
						&& end.isAfter(discussionPost.getCreated()))
				.collect(Collectors.groupingBy(DiscussionPost::getUser,
						() -> new TreeMap<>(EnrolledUser.getNameComparator()),
						Collectors.groupingBy(DiscussionPost::getForum, LinkedHashMap::new, Collectors.toList())));

	}

}
