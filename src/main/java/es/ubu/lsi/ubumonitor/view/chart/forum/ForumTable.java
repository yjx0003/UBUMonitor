package es.ubu.lsi.ubumonitor.view.chart.forum;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.DiscussionPost;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
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
		// TODO Auto-generated method stub

	}

	@Override
	public String getOptions(JSObject jsObject) {
		jsObject.put("invalidOptionWarnings", false);
		jsObject.put("height", "height");
		jsObject.put("tooltipsHeader", true);
		jsObject.put("virtualDom", true);
		jsObject.putWithQuote("layout", "fitColumns");
		jsObject.put("rowClick", "function(e,row){javaConnector.dataPointSelection(row.getPosition());}");
		return jsObject.toString();
	}

	@Override
	public void update() {
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		Set<CourseModule> selectedForums = new HashSet<>(forums.getSelectionModel()
				.getSelectedItems());
		List<DiscussionPost> discussionPosts = Controller.getInstance()
				.getActualCourse()
				.getDiscussionPosts()
				.stream()
				.filter(discussionPost -> selectedForums.contains(discussionPost.getDiscussion()
						.getForum()))
				.collect(Collectors.toList());
		System.out.println(Controller.getInstance().getActualCourse().getDiscussionPosts().size());
		Controller.getInstance().getActualCourse().getDiscussionPosts().forEach(d->System.out.println(d.getDiscussion().getForum()));
		discussionPosts.forEach(d->System.out.println(d.getParent().getUser()));
		JSArray tabledata = createData(enrolledUsers, discussionPosts);
		JSArray columns = createColumns(enrolledUsers);
		JSObject dataset = new JSObject();
		dataset.put("tabledata", tabledata);
		dataset.put("columns", columns);
		String options = getOptions();
		webViewChartsEngine.executeScript("updateTabulator(" + dataset + "," + options + ")");
	}

	private JSArray createColumns(List<EnrolledUser> enrolledUsers) {
		JSArray jsArray = new JSArray();
		JSObject jsObject = new JSObject();
		jsObject.putWithQuote("title", "user"); //TODO 
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
				jsObject.put("ID"+to.getId(), countPosts);
			
				
			}
		}
		return jsArray;
	}

}
