package clustering.controller.collector;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import clustering.data.UserData;
import controllers.Controller;
import controllers.I18n;
import controllers.MainController;
import controllers.datasets.DataSetSection;
import controllers.ubulogs.GroupByAbstract;
import controllers.ubulogs.TypeTimes;
import javafx.scene.control.ListView;
import model.Course;
import model.EnrolledUser;
import model.Section;

public class LogSectionCollector extends DataCollector {

	private ListView<Section> sections;

	public LogSectionCollector(MainController mainController) {
		sections = mainController.getListViewSection();
	}

	@Override
	public void collect(List<UserData> users) {
		List<Section> selected = sections.getSelectionModel().getSelectedItems();
		DataSetSection dataSet = DataSetSection.getInstance();
		Course couse = Controller.getInstance().getActualCourse();
		GroupByAbstract<?> groupBy = couse.getLogStats().getByType(TypeTimes.ALL);
		List<EnrolledUser> enrolledUsers = users.stream().map(UserData::getEnrolledUser).collect(Collectors.toList());
		Map<EnrolledUser, Map<Section, List<Long>>> result = dataSet.getUserCounts(groupBy, enrolledUsers, selected,
				null, null);
		for (UserData userData : users) {
			for (Section section : selected) {
				double datum = result.get(userData.getEnrolledUser()).get(section).get(0);
				userData.addDatum(section.getName(), datum);
			}
		}
	}

	@Override
	public String toString() {
		return I18n.get("tab.section");
	}

}
