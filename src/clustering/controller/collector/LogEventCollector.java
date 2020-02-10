package clustering.controller.collector;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import clustering.data.UserData;
import controllers.Controller;
import controllers.I18n;
import controllers.MainController;
import controllers.datasets.DataSetComponentEvent;
import controllers.ubulogs.GroupByAbstract;
import controllers.ubulogs.TypeTimes;
import javafx.scene.control.ListView;
import model.ComponentEvent;
import model.Course;
import model.EnrolledUser;

public class LogEventCollector extends DataCollector {
	
	private ListView<ComponentEvent> componentEvents;
	
	public LogEventCollector(MainController mainController) {
		componentEvents = mainController.getListViewEvents();
	}

	@Override
	public void collect(List<UserData> users) {
		List<ComponentEvent> selected = componentEvents.getSelectionModel().getSelectedItems();
		DataSetComponentEvent dataSet = DataSetComponentEvent.getInstance();
		Course couse = Controller.getInstance().getActualCourse();
		GroupByAbstract<?> groupBy = couse.getLogStats().getByType(TypeTimes.ALL);
		List<EnrolledUser> enrolledUsers = users.stream().map(UserData::getEnrolledUser).collect(Collectors.toList());
		Map<EnrolledUser, Map<ComponentEvent, List<Long>>> result = dataSet.getUserCounts(groupBy, enrolledUsers, selected, null, null);
		for (UserData userData : users) {
			for (ComponentEvent component : selected) {
				double datum = result.get(userData.getEnrolledUser()).get(component).get(0);
				userData.addDatum(component.toString(), datum);
			}
		}
	}
	
	@Override
	public String toString() {
		return I18n.get("tab.event");
	}

}
