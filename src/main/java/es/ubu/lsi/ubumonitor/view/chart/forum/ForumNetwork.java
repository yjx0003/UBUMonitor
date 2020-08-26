package es.ubu.lsi.ubumonitor.view.chart.forum;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import es.ubu.lsi.ubumonitor.view.chart.VisNetwork;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;

public class ForumNetwork extends VisNetwork {

	private ListView<CourseModule> listViewForum;

	public ForumNetwork(MainController mainController, WebView webView, ListView<CourseModule> listViewForum) {
		super(mainController, ChartType.FORUM_NETWORK, webView);
		this.listViewForum = listViewForum;
	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		List<DiscussionPost> discussionPosts = getSelectedDiscussionPosts();

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
	public String getOptions(JSObject jsObject) {
		JSObject options = new JSObject();
		options.put("edges", "{arrows:'to', scaling:{max:10}}");
		options.put("nodes", "{scaling:{min:20,max:40},shape:'circularImage',brokenImage:'../img/default_user.png'}");
		options.put("interaction", "{navigationButtons:true,keyboard:true}");
		jsObject.put("options", options);
		return jsObject.toString();
	}

	@Override
	public void update() {
		List<EnrolledUser> users = getSelectedEnrolledUser();
		List<DiscussionPost> discussionPosts = getSelectedDiscussionPosts();
		JSObject data = new JSObject();

		JSArray nodes = new JSArray();

		JSArray edges = new JSArray();
		for (EnrolledUser from : users) {
			JSObject node = new JSObject();
			nodes.add(node);
			node.put("id", from.getId());
			node.putWithQuote("title", from.getFullName());
			node.put("color", rgb(from.getId()));
			node.put("image", "'" + from.getImageBase64() + "'");
			long totalPosts = 0;
			for (EnrolledUser to : users) {
				long countPosts = discussionPosts.stream()
						.filter(discussionPost -> from.equals(discussionPost.getUser())
								&& to.equals(discussionPost.getParent()
										.getUser()))
						.count();

				if (countPosts > 0) {
					JSObject edge = new JSObject();
					edge.put("from", from.getId());
					edge.put("to", to.getId());
					edge.put("title", countPosts);
					edge.put("value", countPosts);

					edges.add(edge);
					totalPosts += countPosts;
				}
			}
			node.put("value", totalPosts);
			if (totalPosts > 0) {

				node.put("label", "'" + totalPosts + "'");
			}

		}

		data.put("nodes", nodes);
		data.put("edges", edges);
		webViewChartsEngine.executeScript("updateVisNetwork(" + data + "," + getOptions() + ")");
	}

	public List<DiscussionPost> getSelectedDiscussionPosts() {
		Set<CourseModule> selectedForums = new HashSet<>(listViewForum.getSelectionModel()
				.getSelectedItems());
		return actualCourse.getDiscussionPosts()
				.stream()
				.filter(discussionPost -> selectedForums.contains(discussionPost.getForum()))
				.collect(Collectors.toList());
	}

}
