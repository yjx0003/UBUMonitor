package clustering.controller.collector;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import clustering.data.UserData;
import controllers.Controller;
import controllers.I18n;
import controllers.MainController;
import controllers.datasets.DatasSetCourseModule;
import controllers.ubulogs.GroupByAbstract;
import controllers.ubulogs.TypeTimes;
import javafx.scene.control.ListView;
import model.Course;
import model.CourseModule;
import model.EnrolledUser;

public class LogCourseModuleCollector extends DataCollector {

	private ListView<CourseModule> courseModules;

	public LogCourseModuleCollector(MainController mainController) {
		courseModules = mainController.getListViewCourseModule();
	}

	@Override
	public void collect(List<UserData> users) {
		List<CourseModule> selected = courseModules.getSelectionModel().getSelectedItems();
		DatasSetCourseModule dataSet = DatasSetCourseModule.getInstance();
		Course couse = Controller.getInstance().getActualCourse();
		GroupByAbstract<?> groupBy = couse.getLogStats().getByType(TypeTimes.ALL);
		List<EnrolledUser> enrolledUsers = users.stream().map(UserData::getEnrolledUser).collect(Collectors.toList());
		Map<EnrolledUser, Map<CourseModule, List<Long>>> result = dataSet.getUserCounts(groupBy, enrolledUsers,
				selected, null, null);
		for (UserData userData : users) {
			for (CourseModule component : selected) {
				double datum = result.get(userData.getEnrolledUser()).get(component).get(0);
				userData.addDatum(component.getModuleName(), datum);
			}
		}
	}

	@Override
	public String toString() {
		return I18n.get("tab.coursemodule");
	}

}
