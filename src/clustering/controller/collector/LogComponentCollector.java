package clustering.controller.collector;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import clustering.data.UserData;
import controllers.Controller;
import controllers.I18n;
import controllers.MainController;
import controllers.datasets.DataSetComponent;
import controllers.ubulogs.GroupByAbstract;
import controllers.ubulogs.TypeTimes;
import javafx.scene.control.ListView;
import model.Component;
import model.Course;
import model.EnrolledUser;

public class LogComponentCollector extends DataCollector {

	private ListView<Component> components;

	public LogComponentCollector(MainController mainController) {
		components = mainController.getListViewComponents();
	}

	@Override
	public void collect(List<UserData> users) {
		List<Component> selected = components.getSelectionModel().getSelectedItems();
		DataSetComponent dataSet = DataSetComponent.getInstance();
		Course couse = Controller.getInstance().getActualCourse();
		GroupByAbstract<?> groupBy = couse.getLogStats().getByType(TypeTimes.ALL);
		List<EnrolledUser> enrolledUsers = users.stream().map(UserData::getEnrolledUser).collect(Collectors.toList());
		Map<EnrolledUser, Map<Component, List<Long>>> result = dataSet.getUserCounts(groupBy, enrolledUsers, selected, null, null);
		for (UserData userData : users) {
			for (Component component : selected) {
				double datum = result.get(userData.getEnrolledUser()).get(component).get(0);
				userData.addDatum(component.getName(), datum);
			}
		}
	}
	
	@Override
	public String toString() {
		return I18n.get("tab.component");
	}

}
