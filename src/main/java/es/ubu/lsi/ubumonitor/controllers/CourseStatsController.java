package es.ubu.lsi.ubumonitor.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class CourseStatsController implements Initializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(CourseStatsController.class);

	@FXML
	private WebView webView;

	private JSArray data;

	private JSArray keys;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		Course actualCourse = Controller.getInstance()
				.getActualCourse();
		JSArray headers = new JSArray();
		data = new JSArray();
		keys = new JSArray();
		headers.addWithQuote(actualCourse.getFullName());
		headers.addWithQuote(I18n.get("text.stats"));

		setValues(actualCourse);

		JSArray jsArray = new JSArray();
		jsArray.add(keys);
		jsArray.add(data);
		WebEngine webEngine = webView.getEngine();
		webEngine.load(getClass().getResource("/graphics/PlotlyTable.html")
				.toString());
		webEngine.getLoadWorker()
				.stateProperty()
				.addListener((ov, oldState, newState) -> {
					if (Worker.State.SUCCEEDED != newState)
						return;
					if (webEngine.getDocument() == null) {
						webEngine.reload();
						return;
					}

					webView.getEngine()
							.executeScript("update(" + headers + "," + jsArray + ")");
				});
	}

	public void setValues(Course actualCourse) {
		setValue("label.enrolledusers", actualCourse.getEnrolledUsersCount());
		setValue("label.logentries", actualCourse.getLogs()
				.getList()
				.size());
		setValue("label.componenttypes", actualCourse.getUniqueComponents()
				.size());
		setValue("label.typecomponentevents", actualCourse.getUniqueComponentsEvents()
				.size());
		setValue("label.sections", actualCourse.getSections()
				.size());
		setValue("label.coursemodules", actualCourse.getModules()
				.size());
		setValue("label.gradebook", actualCourse.getGradeItems()
				.size());
		setValue("label.gradebookcm", actualCourse.getGradeItems()
				.stream()
				.filter(cm -> cm.getModule() != null)
				.count());
		setValue("label.activities", actualCourse.getModules()
				.stream()
				.filter(cm -> !(cm.getActivitiesCompletion()
						.isEmpty()))
				.count());
		setValue("label.posts", actualCourse.getDiscussionPosts()
				.size());
		setValue("label.calendarevents", actualCourse.getCourseEvents()
				.size());
		
	}

	private void setValue(String key, Object value) {
		keys.addWithQuote(I18n.get(key));
		data.add(value);
	}

	/**
	 * Log course general statistics.
	 * 
	 * @since v2.6.3-stable
	 */
	public static void logStatistics(Course actualCourse) {
		LOGGER.info("COURSE STATISTICS: {} {}", actualCourse.getId(), actualCourse.getFullName());
		LOGGER.info("# enrolled users: {}", actualCourse.getEnrolledUsersCount());
		LOGGER.info("# logs entries: {}", actualCourse.getLogs()
				.getList()
				.size());
		LOGGER.info("# component types: {}", actualCourse.getUniqueComponents()
				.size());
		LOGGER.info("# type events: {}", actualCourse.getUniqueComponentsEvents()
				.size());
		LOGGER.info("# sections: {}", actualCourse.getSections()
				.size());
		LOGGER.info("# course modules: {}", actualCourse.getModules()
				.size());
		LOGGER.info("# grade book all items: {}", actualCourse.getGradeItems()
				.size());
		LOGGER.info("# grade book course module items: {}", actualCourse.getGradeItems()
				.stream()
				.filter(cm -> cm.getModule() != null)
				.count());
		LOGGER.info("# activity completion items: {}", actualCourse.getModules()
				.stream()
				.filter(cm -> !(cm.getActivitiesCompletion()
						.isEmpty()))
				.count());
	}

}
