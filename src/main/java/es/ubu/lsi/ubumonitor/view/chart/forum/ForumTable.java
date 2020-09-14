package es.ubu.lsi.ubumonitor.view.chart.forum;

import java.io.IOException;
import java.util.ArrayList;
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
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Tabulator;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;

public class ForumTable extends Tabulator {

	private ListView<CourseModule> forums;

	public ForumTable(MainController mainController, WebView webView, ListView<CourseModule> forums) {
		super(mainController, ChartType.FORUM_TABLE, webView);
		this.forums = forums;
	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		List<DiscussionPost> discussionPosts = getSelectedDiscussionPosts();
		List<String> header = new ArrayList<>();
		header.add("userId");
		header.add("userFullname");
		header.addAll(enrolledUsers.stream()
				.map(EnrolledUser::getFullName)
				.collect(Collectors.toList()));

		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader(header.toArray(new String[0])))) {
			for (EnrolledUser from : enrolledUsers) {
				printer.print(from.getId());
				printer.print(from.getFullName());
				for (EnrolledUser to : enrolledUsers) {

					long countPosts = discussionPosts.stream()
							.filter(discussionPost -> from.equals(discussionPost.getUser())
									&& discussionPost.getParent() != null && to.equals(discussionPost.getParent()
											.getUser()))
							.count();
					printer.print(countPosts);
				}
				printer.println();
			}
		}
	}

	@Override
	public JSObject getOptions(JSObject jsObject) {
		jsObject.put("invalidOptionWarnings", false);
		jsObject.put("height", "height");
		jsObject.put("tooltipsHeader", true);
		jsObject.put("virtualDom", true);
		jsObject.putWithQuote("layout", "fitColumns");
		jsObject.put("rowClick", "function(e,row){javaConnector.dataPointSelection(row.getPosition());}");
		return jsObject;
	}

	@Override
	public void update() {
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();

		List<DiscussionPost> discussionPosts = getSelectedDiscussionPosts();

		JSArray tabledata = createData(enrolledUsers, discussionPosts);
		JSArray columns = createColumns(enrolledUsers);
		JSObject dataset = new JSObject();
		dataset.put("tabledata", tabledata);
		dataset.put("columns", columns);
		JSObject options = getOptions();
		webViewChartsEngine.executeScript("updateTabulator(" + dataset + "," + options + ")");
	}

	public List<DiscussionPost> getSelectedDiscussionPosts() {
		Set<CourseModule> selectedForums = new HashSet<>(forums.getSelectionModel()
				.getSelectedItems());
		return actualCourse.getDiscussionPosts()
				.stream()
				.filter(discussionPost -> selectedForums.contains(discussionPost.getDiscussion()
						.getForum()))
				.collect(Collectors.toList());
	}

	private JSArray createColumns(List<EnrolledUser> enrolledUsers) {
		JSArray jsArray = new JSArray();
		JSObject jsObject = new JSObject();
		jsObject.putWithQuote("title", I18n.get("text.selectedUsers"));
		jsObject.putWithQuote("field", "user");
		jsObject.put("hozAlign", "'center'");
		jsObject.put("sorter", "'string'");
		jsArray.add(jsObject);
		for (EnrolledUser enrolledUser : enrolledUsers) {
			jsObject = new JSObject();
			jsObject.putWithQuote("title", enrolledUser.getFullName());
			jsObject.put("field", "'ID" + enrolledUser.getId() + "'");
			jsObject.put("hozAlign", "'center'");
			jsObject.put("sorter", "'number'");
			jsObject.put("sorterParams", "{alignEmptyValues:'bottom'}");
			jsArray.add(jsObject);
		}
		return jsArray;

	}

	private JSArray createData(List<EnrolledUser> enrolledUsers, List<DiscussionPost> discussionPosts) {
		JSArray jsArray = new JSArray();
		for (EnrolledUser from : enrolledUsers) {
			JSObject jsObject = new JSObject();
			jsObject.putWithQuote("user", from.getFullName());
			jsArray.add(jsObject);
			for (EnrolledUser to : enrolledUsers) {

				long countPosts = discussionPosts.stream()
						.filter(discussionPost -> from.equals(discussionPost.getUser())
								&& discussionPost.getParent() != null && to.equals(discussionPost.getParent()
										.getUser()))
						.count();
				jsObject.put("ID" + to.getId(), countPosts == 0 ? null : countPosts);

			}
		}
		return jsArray;
	}
}
